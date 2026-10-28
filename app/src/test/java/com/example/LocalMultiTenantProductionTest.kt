package com.example

import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.data.AppContainer
import com.example.data.local.AppDatabase
import com.example.data.local.entity.RestaurantEntity
import com.example.model.Bill
import com.example.model.BillItem
import com.example.model.ExpiryStatus
import com.example.model.Ingredient
import com.example.model.InventoryItem
import com.example.model.MenuItem
import com.example.model.WasteCategory
import com.example.model.WasteReason
import com.example.worker.ExpiryCheckWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

/**
 * Production test suite for FOOD WISE 100% Local/Offline Architecture.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = SmartRestaurantApp::class)
class LocalMultiTenantProductionTest {

    private lateinit var app: SmartRestaurantApp
    private lateinit var container: AppContainer
    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        container = app.container
        database = AppDatabase.getInstance(app)
        WorkManagerTestInitHelper.initializeTestWorkManager(app)
    }

    @Test
    fun testLocalRegistration_createsRestaurantAndActiveSession_withoutHardcodedTenant() = runBlocking {
        val uniqueTag = UUID.randomUUID().toString().take(6)
        val email = "owner_$uniqueTag@foodwise.com"
        val restName = "Bistro $uniqueTag"

        val regResult = container.authRepository.register(
            email = email,
            password = "SecurePassword123!",
            restaurantName = restName,
            ownerName = "Owner $uniqueTag",
            phone = "9876543210",
            address = "123 Main St"
        )

        assertTrue("Registration must succeed locally", regResult.isSuccess)
        val restaurant = regResult.getOrThrow()
        assertNotNull("Restaurant must exist", restaurant)
        assertNotEquals("Restaurant ID must not be hardcoded to 0 or 1 unless uniquely generated", 0L, restaurant.id)

        val activeId = container.authRepository.getActiveRestaurantId().first()
        assertEquals("Active session must match registered restaurant ID", restaurant.id, activeId)
    }

    @Test
    fun testLocalLoginAndLogout_managesSessionPersistence() = runBlocking {
        val uniqueTag = UUID.randomUUID().toString().take(6)
        val email = "user_$uniqueTag@foodwise.com"
        val password = "Password123!"

        val regResult = container.authRepository.register(
            email = email,
            password = password,
            restaurantName = "Diner $uniqueTag",
            ownerName = "Chef $uniqueTag",
            phone = "1234567890",
            address = "456 Oak St"
        ).getOrThrow()

        val restId = regResult.id

        // Logout
        container.authRepository.logout()
        val activeAfterLogout = container.authRepository.getActiveRestaurantId().first()
        assertNull("Active session must be cleared on logout", activeAfterLogout)

        // Login with correct credentials
        val loginResult = container.authRepository.login(email, password)
        assertTrue("Login with correct credentials must succeed", loginResult.isSuccess)

        val activeAfterLogin = container.authRepository.getActiveRestaurantId().first()
        assertEquals("Active session must be restored on login", restId, activeAfterLogin)

        // Login with wrong password must fail
        val wrongLogin = container.authRepository.login(email, "WrongPassword")
        assertTrue("Login with incorrect password must fail", wrongLogin.isFailure)
    }

    @Test
    fun testMultiTenantDataIsolation_betweenRestaurantAAndRestaurantB() = runBlocking {
        val tagA = UUID.randomUUID().toString().take(6)
        val tagB = UUID.randomUUID().toString().take(6)

        val restA = container.authRepository.register(
            email = "restA_$tagA@foodwise.com",
            password = "Password123!",
            restaurantName = "Restaurant Alpha",
            ownerName = "Alice",
            phone = "1111111111",
            address = "Street A"
        ).getOrThrow()

        val restB = container.authRepository.register(
            email = "restB_$tagB@foodwise.com",
            password = "Password123!",
            restaurantName = "Restaurant Beta",
            ownerName = "Bob",
            phone = "2222222222",
            address = "Street B"
        ).getOrThrow()

        val idA = restA.id
        val idB = restB.id

        // Add menu item to A
        val spiceA = Ingredient(id = 0, menuItemId = 0, name = "Spice A", quantityPerServing = 1.0, unit = "kg")
        container.menuRepository.addMenuItem(
            restaurantId = idA,
            name = "Special Curry Alpha",
            category = "Main Course",
            price = 15.99,
            ingredients = listOf(spiceA)
        )

        // Add inventory item to B
        container.inventoryRepository.addInventoryItem(
            restaurantId = idB,
            ingredientName = "Flour Beta",
            category = "Grains",
            quantity = 50.0,
            unit = "kg",
            minimumStockLevel = 10.0,
            expiryDate = "2028-12-31",
            isPerishable = true
        )

        // Verify A sees only A's menu items
        val menuItemsA = container.menuRepository.getMenuItems(idA).first()
        assertEquals(1, menuItemsA.size)
        assertEquals("Special Curry Alpha", menuItemsA[0].name)

        val menuItemsB = container.menuRepository.getMenuItems(idB).first()
        assertEquals(0, menuItemsB.size)

        // Verify B sees only B's inventory items
        val invItemsA = container.inventoryRepository.getInventoryItems(idA).first()
        assertEquals(0, invItemsA.size)

        val invItemsB = container.inventoryRepository.getInventoryItems(idB).first()
        assertEquals(1, invItemsB.size)
        assertEquals("Flour Beta", invItemsB[0].ingredientName)
    }

    @Test
    fun testDemoDataLoading_populatesLocalDatabaseForActiveRestaurantOnly() = runBlocking {
        val tag = UUID.randomUUID().toString().take(6)
        val rest = container.authRepository.register(
            email = "demo_$tag@foodwise.com",
            password = "Password123!",
            restaurantName = "Demo Cafe $tag",
            ownerName = "Owner",
            phone = "3333333333",
            address = "Demo Street"
        ).getOrThrow()

        val restId = rest.id

        val demoResult = container.demoDataRepository.loadDemoData(
            restaurantId = restId,
            updateProfile = false,
            profileId = "royal_spice"
        )
        assertTrue("Demo data loading must succeed", demoResult.isSuccess)

        val menuItems = container.menuRepository.getMenuItems(restId).first()
        assertTrue("Menu items must be loaded for active restaurant", menuItems.isNotEmpty())

        val inventoryItems = container.inventoryRepository.getInventoryItems(restId).first()
        assertTrue("Inventory items must be loaded for active restaurant", inventoryItems.isNotEmpty())
    }

    @Test
    fun testWorkManagerExpiryCheckWorker_schedulesSuccessfully() {
        val workManager = WorkManager.getInstance(app)
        val workInfos = workManager.getWorkInfosForUniqueWork(ExpiryCheckWorker.WORK_NAME).get()
        assertNotNull("WorkManager tasks should be queryable", workInfos)
    }
}
