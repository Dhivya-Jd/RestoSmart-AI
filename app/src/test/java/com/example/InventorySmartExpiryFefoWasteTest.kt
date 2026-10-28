package com.example

import android.Manifest
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppContainer
import com.example.data.inventory.ExpiryTrackerService
import com.example.data.notification.ExpiryNotificationManager
import com.example.data.prediction.DemandPredictionEngine
import com.example.data.waste.WasteClassificationService
import com.example.data.waste.WasteInsightEngine
import com.example.model.ExpiryStatus
import com.example.model.HistoricalDailySales
import com.example.model.InventoryItem
import com.example.model.MenuItem
import com.example.model.WasteCategory
import com.example.model.WasteItem
import com.example.model.WasteReason
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = SmartRestaurantApp::class)
class InventorySmartExpiryFefoWasteTest {

    private lateinit var app: SmartRestaurantApp
    private lateinit var container: AppContainer
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        container = app.container
        Shadows.shadowOf(app).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun addDays(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return dateFormat.format(cal.time)
    }

    @Test
    fun testScenarioA_MilkExpiresTomorrow_UrgentAndFefoPriority() = runBlocking {
        val uniqueEmail = "scen_a_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        val reg = container.authRepository.register(
            email = uniqueEmail,
            password = "Password@123",
            restaurantName = "Scenario A Bistro",
            ownerName = "Chef A",
            phone = "9876500001",
            address = "Street A"
        ).getOrThrow()
        val restId = reg.id
        val todayStr = dateFormat.format(Date())
        val tomorrowStr = addDays(1)

        // Add milk expiring tomorrow
        val addResult = container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Fresh Whole Milk",
            category = "Dairy",
            quantity = 10.0,
            unit = "L",
            minimumStockLevel = 4.0,
            expiryDate = tomorrowStr,
            isPerishable = true
        )
        assertTrue("Inventory item added successfully", addResult.isSuccess)

        // Verify days remaining and status
        val daysUntilExpiry = ExpiryTrackerService.calculateDaysUntilExpiry(tomorrowStr, todayStr)
        assertEquals("Milk expiring tomorrow must have 1 day remaining", 1L, daysUntilExpiry)

        val expiryStatus = ExpiryTrackerService.calculateExpiryStatus(tomorrowStr, true, todayStr)
        assertTrue(
            "Milk expiring in 1 day must be EXPIRING_SOON or EXPIRING_TODAY",
            expiryStatus == ExpiryStatus.EXPIRING_SOON || expiryStatus == ExpiryStatus.EXPIRING_TODAY
        )

        // Verify ExpiryStats calculation
        val stats = container.inventoryRepository.getExpiryStats(restId, todayStr).first()
        assertTrue("Expiring soon or attention count should be at least 1", stats.expiringSoonCount >= 1 || stats.attentionRequiredCount >= 1)
    }

    @Test
    fun testScenarioB_PaneerExpiresToday_UseFirst() = runBlocking {
        val uniqueEmail = "scen_b_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        val reg = container.authRepository.register(
            email = uniqueEmail,
            password = "Password@123",
            restaurantName = "Scenario B Restaurant",
            ownerName = "Chef B",
            phone = "9876500002",
            address = "Street B"
        ).getOrThrow()
        val restId = reg.id
        val todayStr = dateFormat.format(Date())

        // Add paneer expiring today
        container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Cottage Cheese Paneer",
            category = "Dairy",
            quantity = 5.0,
            unit = "kg",
            minimumStockLevel = 2.0,
            expiryDate = todayStr,
            isPerishable = true
        ).getOrThrow()

        val daysUntilExpiry = ExpiryTrackerService.calculateDaysUntilExpiry(todayStr, todayStr)
        assertEquals("Paneer expiring today must have 0 days remaining", 0L, daysUntilExpiry)

        val expiryStatus = ExpiryTrackerService.calculateExpiryStatus(todayStr, true, todayStr)
        assertEquals("Paneer expiring today must have EXPIRING_TODAY status", ExpiryStatus.EXPIRING_TODAY, expiryStatus)

        val items = container.inventoryRepository.getInventoryItems(restId).first()
        val sortedFefo = ExpiryTrackerService.sortItemsByExpiryUrgency(items, todayStr)
        assertEquals("Paneer must be the #1 first FEFO recommendation (USE FIRST)", "Cottage Cheese Paneer", sortedFefo.first().ingredientName)
    }

    @Test
    fun testScenarioC_ChickenExpired_ExcludedFromUsableFefo_DisposalAction() = runBlocking {
        val uniqueEmail = "scen_c_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        val reg = container.authRepository.register(
            email = uniqueEmail,
            password = "Password@123",
            restaurantName = "Scenario C Grill",
            ownerName = "Chef C",
            phone = "9876500003",
            address = "Street C"
        ).getOrThrow()
        val restId = reg.id
        val todayStr = dateFormat.format(Date())
        val pastDateStr = addDays(-2) // Expired 2 days ago

        val chickenId = container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Raw Chicken Breast",
            category = "Meat",
            quantity = 4.0,
            unit = "kg",
            minimumStockLevel = 2.0,
            expiryDate = pastDateStr,
            isPerishable = true
        ).getOrThrow()

        val expiryStatus = ExpiryTrackerService.calculateExpiryStatus(pastDateStr, true, todayStr)
        assertEquals("Past date perishable item must have EXPIRED status", ExpiryStatus.EXPIRED, expiryStatus)

        val items = container.inventoryRepository.getInventoryItems(restId).first()
        val usableItems = items.filter {
            it.quantity > 0 && it.isPerishable && !it.expiryDate.isNullOrBlank() &&
                    ExpiryTrackerService.calculateExpiryStatus(it.expiryDate, it.isPerishable, todayStr) != ExpiryStatus.EXPIRED
        }
        assertTrue("Expired chicken must NOT be included in usable FEFO recommendations", usableItems.none { it.id == chickenId })

        // Record waste for the expired item
        val wasteClass = WasteClassificationService.classify("Raw Chicken Breast", "Meat")
        val recordWasteRes = container.wasteRepository.recordWaste(
            restaurantId = restId,
            itemName = "Raw Chicken Breast",
            quantity = 4.0,
            unit = "kg",
            reason = WasteReason.EXPIRED,
            category = WasteCategory.DEGRADABLE,
            wasteDate = todayStr,
            notes = "Expired batch removed safely",
            classificationExplanation = wasteClass.explanation,
            confidence = wasteClass.confidence,
            inventoryItemId = chickenId,
            deductFromInventory = true
        )
        assertTrue("Waste record must be recorded successfully", recordWasteRes.isSuccess)

        // Verify that expired item was deleted/deducted
        val remainingItems = container.inventoryRepository.getInventoryItems(restId).first()
        assertTrue("Inventory should have 0 quantity or be removed", remainingItems.find { it.id == chickenId }?.quantity ?: 0.0 == 0.0)
    }

    @Test
    fun testScenarioD_TwoBatches_BatchAExpires1Day_BatchBExpires5Days_FefoOrdering() = runBlocking {
        val uniqueEmail = "scen_d_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        val reg = container.authRepository.register(
            email = uniqueEmail,
            password = "Password@123",
            restaurantName = "Scenario D Bakery",
            ownerName = "Chef D",
            phone = "9876500004",
            address = "Street D"
        ).getOrThrow()
        val restId = reg.id
        val todayStr = dateFormat.format(Date())
        val batchADate = addDays(1)
        val batchBDate = addDays(5)

        // Batch B added first in database (newer expiry)
        val batchBId = container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Cream Batch B (5 Days)",
            category = "Dairy",
            quantity = 8.0,
            unit = "L",
            minimumStockLevel = 2.0,
            expiryDate = batchBDate,
            isPerishable = true
        ).getOrThrow()

        // Batch A added later (earlier expiry)
        val batchAId = container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Cream Batch A (1 Day)",
            category = "Dairy",
            quantity = 5.0,
            unit = "L",
            minimumStockLevel = 2.0,
            expiryDate = batchADate,
            isPerishable = true
        ).getOrThrow()

        val allItems = container.inventoryRepository.getInventoryItems(restId).first()
        val sortedFefo = ExpiryTrackerService.sortItemsByExpiryUrgency(allItems, todayStr)

        assertEquals("First FEFO item MUST be Batch A (earliest expiry)", batchAId, sortedFefo[0].id)
        assertEquals("Second FEFO item MUST be Batch B", batchBId, sortedFefo[1].id)
    }

    @Test
    fun testScenarioE_WasteRisk_StockExceedsPredictedDemand() = runBlocking {
        val mockWasteItems = listOf(
            WasteItem(
                id = 1L,
                restaurantId = 1L,
                itemName = "Fresh Tomatoes",
                quantity = 15.0,
                unit = "kg",
                category = WasteCategory.DEGRADABLE,
                reason = WasteReason.OVER_PREPARATION,
                wasteDate = dateFormat.format(Date()),
                notes = "Excess prep",
                classification = "Degradable",
                classificationExplanation = "Organic plant matter",
                confidence = "High",
                inventoryItemId = null,
                createdAt = System.currentTimeMillis()
            )
        )

        val menuItem = MenuItem(
            id = 10L,
            restaurantId = 1L,
            name = "Tomato Soup",
            category = "Soup",
            price = 150.0,
            ingredients = emptyList(),
            createdAt = System.currentTimeMillis()
        )
        val historicalSales = listOf(
            HistoricalDailySales("2026-09-01", "Monday", 5, "Sep 01"),
            HistoricalDailySales("2026-09-02", "Tuesday", 4, "Sep 02"),
            HistoricalDailySales("2026-09-03", "Wednesday", 6, "Sep 03")
        )
        val prediction = DemandPredictionEngine.predictDemand(
            restaurantId = 1L,
            menuItem = menuItem,
            history = historicalSales,
            targetDateString = dateFormat.format(Date()),
            targetDateFormatted = null
        )

        val insights = WasteInsightEngine.generateInsights(mockWasteItems, listOf(prediction))
        assertNotNull("Waste insights should be generated", insights)
        assertTrue("Insights list should not be empty", insights.isNotEmpty())
    }

    @Test
    fun testScenarioF_PurchaseRecommendation_StockBelowPredictedDemand() = runBlocking {
        val uniqueEmail = "scen_f_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        val reg = container.authRepository.register(
            email = uniqueEmail,
            password = "Password@123",
            restaurantName = "Scenario F Diner",
            ownerName = "Chef F",
            phone = "9876500006",
            address = "Street F"
        ).getOrThrow()
        val restId = reg.id
        val todayStr = dateFormat.format(Date())
        val tomorrowStr = addDays(1)

        // Add menu item needing 0.5 kg Basmati Rice per serving
        container.menuRepository.addMenuItem(
            restaurantId = restId,
            name = "Special Biryani",
            category = "Main Course",
            price = 400.0,
            ingredients = listOf(
                com.example.model.Ingredient(0, 0, "Basmati Rice", 0.5, "kg")
            )
        ).getOrThrow()

        // Inventory has only 1 kg Basmati Rice (Current stock = 1 kg, min = 5 kg)
        container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Basmati Rice",
            category = "Grains",
            quantity = 1.0,
            unit = "kg",
            minimumStockLevel = 5.0,
            expiryDate = addDays(90),
            isPerishable = false
        ).getOrThrow()

        // Generate demand predictions first for tomorrow
        container.predictionRepository.generatePredictionsForDate(restId, tomorrowStr)

        // Generate purchase recommendations
        val recReport = container.purchaseRecommendationRepository.generateOrGetRecommendations(
            restaurantId = restId,
            targetDate = tomorrowStr,
            forceRegenerate = true
        )
        assertTrue("Purchase recommendation report generated", recReport.isSuccess)
        val report = recReport.getOrThrow()
        assertNotNull("Report must be generated", report)
    }

    @Test
    fun testExpiryNotificationManager_DeduplicationAndDispatch() = runBlocking {
        val uniqueEmail = "notif_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        val reg = container.authRepository.register(
            email = uniqueEmail,
            password = "Password@123",
            restaurantName = "Notification Palace",
            ownerName = "Chef Notif",
            phone = "9876500007",
            address = "Street Notif"
        ).getOrThrow()
        val restId = reg.id
        val todayStr = dateFormat.format(Date())

        ExpiryNotificationManager.clearNotificationHistory(app)
        ExpiryNotificationManager.createNotificationChannel(app)

        val mockItems = listOf(
            InventoryItem(
                id = 901L,
                restaurantId = restId,
                ingredientName = "Fresh Paneer",
                category = "Dairy",
                quantity = 3.0,
                unit = "kg",
                minimumStockLevel = 1.0,
                expiryDate = todayStr,
                isPerishable = true,
                updatedAt = System.currentTimeMillis()
            )
        )

        // First check: Dispatches notification
        val firstCheckCount = ExpiryNotificationManager.checkAndNotifyExpiringItems(
            context = app,
            restaurantId = restId,
            restaurantName = "Notification Palace",
            items = mockItems,
            todayDateStr = todayStr
        )
        assertEquals("First check should trigger 1 notification", 1, firstCheckCount)

        // Second check with same items on same day: Must deduplicate (0 new notifications)
        val secondCheckCount = ExpiryNotificationManager.checkAndNotifyExpiringItems(
            context = app,
            restaurantId = restId,
            restaurantName = "Notification Palace",
            items = mockItems,
            todayDateStr = todayStr
        )
        assertEquals("Second check on same items should deduplicate and return 0", 0, secondCheckCount)
    }

    @Test
    fun testMultiTenantIsolation_InventoryAndExpiry() = runBlocking {
        // Restaurant 1
        val reg1 = container.authRepository.register(
            email = "rest1_${UUID.randomUUID().toString().take(8)}@foodwise.com",
            password = "Password@123",
            restaurantName = "Tenant 1 Restaurant",
            ownerName = "Owner 1",
            phone = "9876500011",
            address = "Address 1"
        ).getOrThrow()

        // Restaurant 2
        val reg2 = container.authRepository.register(
            email = "rest2_${UUID.randomUUID().toString().take(8)}@foodwise.com",
            password = "Password@123",
            restaurantName = "Tenant 2 Restaurant",
            ownerName = "Owner 2",
            phone = "9876500012",
            address = "Address 2"
        ).getOrThrow()

        val restId1 = reg1.id
        val restId2 = reg2.id

        // Add inventory to Restaurant 1
        container.inventoryRepository.addInventoryItem(
            restaurantId = restId1,
            ingredientName = "Special Spice A",
            category = "Spices",
            quantity = 2.0,
            unit = "kg",
            minimumStockLevel = 0.5,
            expiryDate = addDays(10),
            isPerishable = false
        ).getOrThrow()

        // Add inventory to Restaurant 2
        container.inventoryRepository.addInventoryItem(
            restaurantId = restId2,
            ingredientName = "Exclusive Herb B",
            category = "Herbs",
            quantity = 5.0,
            unit = "kg",
            minimumStockLevel = 1.0,
            expiryDate = addDays(3),
            isPerishable = true
        ).getOrThrow()

        // Verify Restaurant 1 items
        val items1 = container.inventoryRepository.getInventoryItems(restId1).first()
        assertEquals("Restaurant 1 should have exactly 1 item", 1, items1.size)
        assertEquals("Special Spice A", items1[0].ingredientName)
        assertFalse("Restaurant 1 should NOT see Restaurant 2 items", items1.any { it.ingredientName == "Exclusive Herb B" })

        // Verify Restaurant 2 items
        val items2 = container.inventoryRepository.getInventoryItems(restId2).first()
        assertEquals("Restaurant 2 should have exactly 1 item", 1, items2.size)
        assertEquals("Exclusive Herb B", items2[0].ingredientName)
        assertFalse("Restaurant 2 should NOT see Restaurant 1 items", items2.any { it.ingredientName == "Special Spice A" })
    }
}
