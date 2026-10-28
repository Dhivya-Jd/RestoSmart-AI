package com.example

import android.Manifest
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.data.AppContainer
import com.example.data.inventory.ExpiryTrackerService
import com.example.data.notification.ExpiryNotificationManager
import com.example.data.waste.WasteInsightEngine
import com.example.model.ExpiryStatus
import com.example.model.InventoryItem
import com.example.model.WasteCategory
import com.example.model.WasteItem
import com.example.model.WasteReason
import com.example.worker.ExpiryCheckWorker
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
class FefoAndNotificationIntegrationTest {

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

    private suspend fun registerRestaurant(name: String): Long {
        val email = "test_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        val reg = container.authRepository.register(
            email = email,
            password = "Password@123",
            restaurantName = name,
            ownerName = "Owner",
            phone = "9876543210",
            address = "Street 1"
        ).getOrThrow()
        return reg.id
    }

    // A. Expired item exclusion from usable FEFO
    @Test
    fun testA_ExpiredItemExclusionFromUsableFEFO() = runBlocking {
        val restId = registerRestaurant("FEFO Exclude Expired")
        val todayStr = dateFormat.format(Date())
        val yesterdayStr = addDays(-1)
        val tomorrowStr = addDays(1)

        container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Spoiled Cream",
            category = "Dairy",
            quantity = 2.0,
            unit = "L",
            minimumStockLevel = 1.0,
            expiryDate = yesterdayStr,
            isPerishable = true
        )
        container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Fresh Milk",
            category = "Dairy",
            quantity = 5.0,
            unit = "L",
            minimumStockLevel = 2.0,
            expiryDate = tomorrowStr,
            isPerishable = true
        )

        val items = container.inventoryRepository.getInventoryItems(restId).first()
        val usableFefo = items.filter { item ->
            val expiry = item.expiryDate ?: ""
            if (!item.isPerishable || expiry.isBlank()) false
            else {
                val status = ExpiryTrackerService.calculateExpiryStatus(expiry, item.isPerishable, todayStr)
                status != ExpiryStatus.EXPIRED
            }
        }.sortedWith(compareBy<InventoryItem> { it.expiryDate ?: "" }.thenBy { it.id })

        assertEquals("Only non-expired item should be in usable FEFO", 1, usableFefo.size)
        assertEquals("Fresh Milk", usableFefo[0].ingredientName)
    }

    // B. Batch expiry sorting (1 day vs 5 days)
    @Test
    fun testB_BatchExpirySorting() = runBlocking {
        val restId = registerRestaurant("Batch Sorting")
        val todayStr = dateFormat.format(Date())
        val in1Day = addDays(1)
        val in5Days = addDays(5)
        val in3Days = addDays(3)

        container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Batch In 5 Days",
            category = "Vegetables",
            quantity = 10.0,
            unit = "kg",
            minimumStockLevel = 2.0,
            expiryDate = in5Days,
            isPerishable = true
        )
        container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Batch In 1 Day",
            category = "Dairy",
            quantity = 4.0,
            unit = "L",
            minimumStockLevel = 1.0,
            expiryDate = in1Day,
            isPerishable = true
        )
        container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Batch In 3 Days",
            category = "Meat",
            quantity = 6.0,
            unit = "kg",
            minimumStockLevel = 2.0,
            expiryDate = in3Days,
            isPerishable = true
        )

        val items = container.inventoryRepository.getInventoryItems(restId).first()
        val sortedFefo = items.filter { item ->
            val expiry = item.expiryDate ?: ""
            if (!item.isPerishable || expiry.isBlank()) false
            else {
                val status = ExpiryTrackerService.calculateExpiryStatus(expiry, item.isPerishable, todayStr)
                status != ExpiryStatus.EXPIRED
            }
        }.sortedWith(compareBy<InventoryItem> { it.expiryDate ?: "" }.thenBy { it.id })

        assertEquals(3, sortedFefo.size)
        assertEquals("Batch In 1 Day", sortedFefo[0].ingredientName)
        assertEquals("Batch In 3 Days", sortedFefo[1].ingredientName)
        assertEquals("Batch In 5 Days", sortedFefo[2].ingredientName)
    }

    // C. Expiring today = Critical
    @Test
    fun testC_ExpiringTodayIsCritical() {
        val todayStr = dateFormat.format(Date())
        val status = ExpiryTrackerService.calculateExpiryStatus(todayStr, true, todayStr)
        assertEquals(ExpiryStatus.EXPIRING_TODAY, status)
    }

    // D. 1-7 days = Expiring Soon
    @Test
    fun testD_OneToSevenDaysIsExpiringSoon() {
        val todayStr = dateFormat.format(Date())
        val in3Days = addDays(3)
        val in7Days = addDays(7)

        val status3 = ExpiryTrackerService.calculateExpiryStatus(in3Days, true, todayStr)
        val status7 = ExpiryTrackerService.calculateExpiryStatus(in7Days, true, todayStr)

        assertEquals(ExpiryStatus.EXPIRING_SOON, status3)
        assertEquals(ExpiryStatus.EXPIRING_SOON, status7)
    }

    // E. 30 days = Not Expiring Soon (Safe)
    @Test
    fun testE_ThirtyDaysIsSafe() {
        val todayStr = dateFormat.format(Date())
        val in30Days = addDays(30)

        val status30 = ExpiryTrackerService.calculateExpiryStatus(in30Days, true, todayStr)
        assertEquals(ExpiryStatus.SAFE, status30)
    }

    // F. Notification deduplication
    @Test
    fun testF_NotificationDeduplication() = runBlocking {
        val restId = registerRestaurant("Notification Dedup")
        val todayStr = dateFormat.format(Date())
        val tomorrowStr = addDays(1)

        container.inventoryRepository.addInventoryItem(
            restaurantId = restId,
            ingredientName = "Paneer Cheese",
            category = "Dairy",
            quantity = 5.0,
            unit = "kg",
            minimumStockLevel = 1.0,
            expiryDate = tomorrowStr,
            isPerishable = true
        )
        val items = container.inventoryRepository.getInventoryItems(restId).first()

        ExpiryNotificationManager.clearNotificationHistory(app)

        // First notification check
        val sentCount1 = ExpiryNotificationManager.checkAndNotifyExpiringItems(
            context = app,
            restaurantId = restId,
            restaurantName = "Dedup Bistro",
            items = items,
            todayDateStr = todayStr
        )
        assertTrue("First run should dispatch notifications", sentCount1 >= 1)

        // Second notification check on the same day with same items
        val sentCount2 = ExpiryNotificationManager.checkAndNotifyExpiringItems(
            context = app,
            restaurantId = restId,
            restaurantName = "Dedup Bistro",
            items = items,
            todayDateStr = todayStr
        )
        assertEquals("Second run must be deduplicated (0 new notifications)", 0, sentCount2)
    }

    // G. Background worker invocation
    @Test
    fun testG_BackgroundWorkerExecution() = runBlocking {
        val email = "worker_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        container.authRepository.register(
            email = email,
            password = "Password@123",
            restaurantName = "Worker Test Restaurant",
            ownerName = "Chef Worker",
            phone = "9876500000",
            address = "Street 10"
        )

        val worker = TestListenableWorkerBuilder<ExpiryCheckWorker>(app).build()
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    // H. Multi-tenant isolation for background checks
    @Test
    fun testH_MultiTenantIsolation() = runBlocking {
        val rest1Id = registerRestaurant("Tenant One")
        val rest2Id = registerRestaurant("Tenant Two")
        val todayStr = dateFormat.format(Date())
        val tomorrowStr = addDays(1)

        container.inventoryRepository.addInventoryItem(
            restaurantId = rest1Id,
            ingredientName = "Tenant 1 Butter",
            category = "Dairy",
            quantity = 2.0,
            unit = "kg",
            minimumStockLevel = 1.0,
            expiryDate = tomorrowStr,
            isPerishable = true
        )
        container.inventoryRepository.addInventoryItem(
            restaurantId = rest2Id,
            ingredientName = "Tenant 2 Butter",
            category = "Dairy",
            quantity = 10.0,
            unit = "kg",
            minimumStockLevel = 1.0,
            expiryDate = addDays(20),
            isPerishable = true
        )

        val items1 = container.inventoryRepository.getInventoryItems(rest1Id).first()
        val items2 = container.inventoryRepository.getInventoryItems(rest2Id).first()

        assertEquals(1, items1.size)
        assertEquals("Tenant 1 Butter", items1[0].ingredientName)
        assertEquals(1, items2.size)
        assertEquals("Tenant 2 Butter", items2[0].ingredientName)
    }

    // I. Waste Risk tab logic
    @Test
    fun testI_WasteRiskInsightsEngine() = runBlocking {
        val restId = registerRestaurant("Waste Risk Insights")
        val todayStr = dateFormat.format(Date())

        val wasteItems = listOf(
            WasteItem(
                id = 1L,
                restaurantId = restId,
                itemName = "Expired Tomato Gravy",
                quantity = 4.5,
                unit = "kg",
                category = WasteCategory.DEGRADABLE,
                reason = WasteReason.EXPIRED,
                wasteDate = todayStr,
                classification = "Kitchen Waste",
                classificationExplanation = "Organic food waste",
                confidence = "HIGH",
                notes = "Expired batch",
                inventoryItemId = null,
                createdAt = System.currentTimeMillis()
            )
        )

        val insights = WasteInsightEngine.generateInsights(wasteItems, emptyList())
        assertNotNull(insights)
        assertTrue("Insights list should not be empty when waste exists", insights.isNotEmpty())
    }

    // J. WorkManager scheduling configuration, uniqueness, and periodic enqueue verification
    @Test
    fun testJ_WorkManagerSchedulingConfiguration() {
        WorkManagerTestInitHelper.initializeTestWorkManager(app)

        // Schedule periodic check via ExpiryCheckWorker
        ExpiryCheckWorker.schedulePeriodicCheck(app)

        val workManager = WorkManager.getInstance(app)
        val workInfos = workManager.getWorkInfosForUniqueWork(ExpiryCheckWorker.WORK_NAME).get()

        assertNotNull("Work infos must not be null", workInfos)
        assertEquals("Exactly one unique periodic work request must be enqueued", 1, workInfos.size)

        val workInfo = workInfos[0]
        assertTrue(
            "Work state must be active (ENQUEUED or RUNNING)",
            workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
        )
        assertTrue(
            "Work must be tagged with ExpiryCheckWorker class name",
            workInfo.tags.contains(ExpiryCheckWorker::class.java.name)
        )

        // Verify duplicate scheduling prevention using ExistingPeriodicWorkPolicy.KEEP
        ExpiryCheckWorker.schedulePeriodicCheck(app)
        val workInfosAfterDuplicate = workManager.getWorkInfosForUniqueWork(ExpiryCheckWorker.WORK_NAME).get()
        assertEquals("Duplicate periodic jobs must be prevented (remains 1)", 1, workInfosAfterDuplicate.size)
        assertEquals("Existing work ID must be preserved", workInfo.id, workInfosAfterDuplicate[0].id)
    }
}
