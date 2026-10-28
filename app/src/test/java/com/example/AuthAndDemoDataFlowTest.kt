package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.data.AppContainer
import com.example.data.local.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = SmartRestaurantApp::class)
class AuthAndDemoDataFlowTest {

    private lateinit var app: SmartRestaurantApp
    private lateinit var container: AppContainer
    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        container = app.container
        database = AppDatabase.getInstance(app)
    }

    @Test
    fun testRegistrationAndSafeDemoDataLoadPreservesCustomProfile() = runBlocking {
        val uniqueTag = UUID.randomUUID().toString().take(6)
        val testEmail = "manager_$uniqueTag@sunrisecafe.com"
        val customHotelName = "Hotel Sunrise $uniqueTag"
        val customOwnerName = "Chef Vikram $uniqueTag"

        // 1. Register new restaurant
        val regResult = container.authRepository.register(
            email = testEmail,
            password = "SecurePassword@123",
            restaurantName = customHotelName,
            ownerName = customOwnerName,
            phone = "+91 9876543210",
            address = "42 MG Road, Bengaluru"
        )
        assertTrue("Registration must be successful", regResult.isSuccess)
        val registeredRestaurant = regResult.getOrThrow()
        val restaurantId = registeredRestaurant.id
        assertTrue("Restaurant ID must be positive", restaurantId > 0)
        assertEquals(customHotelName, registeredRestaurant.restaurantName)
        assertEquals(customOwnerName, registeredRestaurant.ownerName)

        // 2. Verify active session ID is the registered restaurant ID
        val activeRestId = container.authRepository.getActiveRestaurantId().first()
        assertEquals(restaurantId, activeRestId)

        // 3. Load demo data with updateProfile = false
        val demoLoadResult = container.demoDataRepository.loadDemoData(
            restaurantId = restaurantId,
            updateProfile = false,
            profileId = "royal_spice"
        )
        assertTrue("Demo data loading must succeed", demoLoadResult.isSuccess)

        // 4. Verify that the restaurant profile was NOT overwritten by demo profile
        val restaurantInDb = container.restaurantRepository.getRestaurant(restaurantId).first()
        assertNotNull("Restaurant must exist in database", restaurantInDb)
        assertEquals("Restaurant name must remain unchanged after demo data load", customHotelName, restaurantInDb?.restaurantName)
        assertEquals("Owner name must remain unchanged after demo data load", customOwnerName, restaurantInDb?.ownerName)

        // 5. Verify that demo menu items and inventory were loaded specifically for this restaurantId
        val menuItems = container.menuRepository.getMenuItems(restaurantId).first()
        assertTrue("Menu items must be loaded for this restaurant", menuItems.isNotEmpty())

        val inventoryItems = container.inventoryRepository.getInventoryItems(restaurantId).first()
        assertTrue("Inventory items must be loaded for this restaurant", inventoryItems.isNotEmpty())
    }

    @Test
    fun testMultiTenantIsolationBetweenTwoRestaurantsWithDemoData() = runBlocking {
        val tagA = UUID.randomUUID().toString().take(6)
        val tagB = UUID.randomUUID().toString().take(6)

        // Register Restaurant A
        val regA = container.authRepository.register(
            email = "rest_a_$tagA@foodwise.com",
            password = "PasswordA@123",
            restaurantName = "Hotel Alpha $tagA",
            ownerName = "Manager Alpha",
            phone = "1111111111",
            address = "Alpha Road"
        ).getOrThrow()

        // Register Restaurant B
        val regB = container.authRepository.register(
            email = "rest_b_$tagB@foodwise.com",
            password = "PasswordB@123",
            restaurantName = "Hotel Beta $tagB",
            ownerName = "Manager Beta",
            phone = "2222222222",
            address = "Beta Street"
        ).getOrThrow()

        val idA = regA.id
        val idB = regB.id
        assertTrue("IDs must be distinct", idA != idB)

        // Load demo data only for Restaurant A
        val loadResultA = container.demoDataRepository.loadDemoData(idA, false, "royal_spice")
        assertTrue(loadResultA.isSuccess)

        val menuA = container.menuRepository.getMenuItems(idA).first()
        assertTrue("Restaurant A must have menu items", menuA.isNotEmpty())

        val menuB = container.menuRepository.getMenuItems(idB).first()
        assertEquals("Restaurant B must NOT have Restaurant A's items", 0, menuB.size)
    }
}
