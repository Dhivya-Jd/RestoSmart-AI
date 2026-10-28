package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.data.AppContainer
import com.example.data.local.AppDatabase
import com.example.model.Bill
import com.example.model.BillItem
import com.example.model.Ingredient
import com.example.model.ReportPeriod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = SmartRestaurantApp::class)
class RestaurantWorkflowTest {

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
    fun testCompleteRestaurantOperationsFlow() = runBlocking {
        val now = System.currentTimeMillis()
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))
        val timeString = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(now))

        // Step 1 & 2: Register and authenticate a new restaurant manager
        val uniqueEmail = "manager_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        val registerResult = container.authRepository.register(
            email = uniqueEmail,
            password = "Password@123",
            restaurantName = "Royal Emerald Palace",
            ownerName = "Chef Sanjeev",
            phone = "9876543210",
            address = "100 Luxury Avenue"
        )
        assertTrue("Registration must succeed", registerResult.isSuccess)
        val restaurantA = registerResult.getOrThrow()
        val restaurantIdA = restaurantA.id
        assertTrue("Restaurant ID must be valid (> 0)", restaurantIdA > 0)
        assertEquals("Chef Sanjeev", restaurantA.ownerName)
        assertEquals("Royal Emerald Palace", restaurantA.restaurantName)

        // Verify active restaurant session in repository
        val activeRestId = container.authRepository.getActiveRestaurantId().first()
        assertEquals("Active session should match registered restaurant", restaurantIdA, activeRestId)

        // Step 3 & 4: Menu Operations - Add a new menu item
        val ingredients = listOf(
            Ingredient(id = 0, menuItemId = 0, name = "Paneer", quantityPerServing = 0.25, unit = "kg"),
            Ingredient(id = 0, menuItemId = 0, name = "Butter", quantityPerServing = 0.05, unit = "kg"),
            Ingredient(id = 0, menuItemId = 0, name = "Tomato Puree", quantityPerServing = 0.15, unit = "kg")
        )
        val addMenuResult = container.menuRepository.addMenuItem(
            restaurantId = restaurantIdA,
            name = "Paneer Butter Masala",
            category = "Main Course",
            price = 320.0,
            ingredients = ingredients
        )
        assertTrue("Add menu item must succeed", addMenuResult.isSuccess)
        val menuItemId = addMenuResult.getOrThrow()
        assertTrue("Menu item ID must be positive", menuItemId > 0)

        // Step 5 & 6: Edit Menu Item
        val updatedIngredients = listOf(
            Ingredient(id = 0, menuItemId = menuItemId, name = "Paneer", quantityPerServing = 0.30, unit = "kg"),
            Ingredient(id = 0, menuItemId = menuItemId, name = "Rich Butter", quantityPerServing = 0.08, unit = "kg")
        )
        val updateMenuResult = container.menuRepository.updateMenuItem(
            id = menuItemId,
            restaurantId = restaurantIdA,
            name = "Royal Paneer Butter Masala",
            category = "Main Course",
            price = 350.0,
            ingredients = updatedIngredients
        )
        assertTrue("Update menu item must succeed", updateMenuResult.isSuccess)

        // Verify item in Menu list
        val menuItemsA = container.menuRepository.getMenuItems(restaurantIdA).first()
        val savedItem = menuItemsA.find { it.id == menuItemId }
        assertNotNull("Saved menu item should exist in menu list", savedItem)
        assertEquals("Royal Paneer Butter Masala", savedItem?.name)
        assertEquals(350.0, savedItem?.price ?: 0.0, 0.001)
        assertEquals("Main Course", savedItem?.category)

        // Step 7, 8, 9, 10: Billing / POS Workflow - Build bill with quantity
        val billNumber = container.billingRepository.generateNextBillNumber(restaurantIdA)
        assertNotNull("Bill number must be generated", billNumber)
        assertTrue("Bill number should not be empty", billNumber.isNotBlank())

        val qty = 3
        val itemPrice = 350.0
        val subtotal = itemPrice * qty // 1050.0

        val billItem = BillItem(
            id = 0,
            billId = 0,
            restaurantId = restaurantIdA,
            menuItemId = menuItemId,
            foodName = "Royal Paneer Butter Masala",
            category = "Main Course",
            quantity = qty,
            unitPrice = itemPrice,
            totalPrice = subtotal
        )

        val bill = Bill(
            id = 0,
            restaurantId = restaurantIdA,
            billNumber = billNumber,
            timestamp = now,
            dateString = dateString,
            timeString = timeString,
            totalAmount = subtotal,
            totalItemCount = qty,
            paymentMethod = "UPI",
            notes = "Dine-In Table 4",
            items = listOf(billItem)
        )

        // Step 11 & 12: Checkout - Save bill and verify receipt data
        val saveBillResult = container.billingRepository.saveBill(bill)
        assertTrue("Save bill must succeed", saveBillResult.isSuccess)
        val savedBill = saveBillResult.getOrThrow()
        assertTrue("Saved bill must have valid ID", savedBill.id > 0)
        assertEquals(subtotal, savedBill.totalAmount, 0.001)
        assertEquals("UPI", savedBill.paymentMethod)
        assertEquals(1, savedBill.items.size)
        assertEquals(3, savedBill.items[0].quantity)

        // Step 13 & 14: Orders / Bill History Verification
        val ordersA = container.billingRepository.getBillsByRestaurant(restaurantIdA).first()
        assertTrue("Order should be recorded in bill history", ordersA.any { it.billNumber == billNumber })
        val todayBills = container.billingRepository.getTodayBills(restaurantIdA).first()
        assertTrue("Order should be present in today's bills", todayBills.any { it.billNumber == billNumber })

        // Step 15 & 16: Sales Integration Verification
        val todaySales = container.billingRepository.getTodaySalesSummary(restaurantIdA).first()
        assertTrue("Today's revenue should reflect bill total", todaySales.totalRevenue >= subtotal)
        assertTrue("Today's orders count should be at least 1", todaySales.totalBills >= 1)

        val salesReport = container.billingRepository.getSalesReport(restaurantIdA, ReportPeriod.TODAY).first()
        assertTrue("Sales report total should match or exceed bill total", salesReport.totalRevenue >= subtotal)
        assertTrue("Sales report bill count should be >= 1", salesReport.totalOrders >= 1)

        // Step 17: Verify Top Selling Items includes the dish
        val topItem = salesReport.foodSales.find { it.foodName == "Royal Paneer Butter Masala" }
        assertNotNull("Sold dish must appear in food sales stats", topItem)
        assertEquals(3, topItem?.quantitySold ?: 0)

        // Step 18: Strict Multi-Tenant Restaurant ID Isolation
        val uniqueEmailB = "manager_b_${UUID.randomUUID().toString().take(8)}@foodwise.com"
        val registerBResult = container.authRepository.register(
            email = uniqueEmailB,
            password = "Password@123",
            restaurantName = "Taj Grand Hotel",
            ownerName = "Chef Vikram",
            phone = "9123456780",
            address = "200 Ocean Boulevard"
        )
        assertTrue(registerBResult.isSuccess)
        val restaurantB = registerBResult.getOrThrow()
        val restaurantIdB = restaurantB.id

        // Check Restaurant B's menu, bills, and sales
        val menuItemsB = container.menuRepository.getMenuItems(restaurantIdB).first()
        assertEquals("Restaurant B should have 0 menu items initially", 0, menuItemsB.size)

        val ordersB = container.billingRepository.getBillsByRestaurant(restaurantIdB).first()
        assertEquals("Restaurant B should have 0 orders", 0, ordersB.size)

        val todaySalesB = container.billingRepository.getTodaySalesSummary(restaurantIdB).first()
        assertEquals("Restaurant B should have 0 sales", 0.0, todaySalesB.totalRevenue, 0.001)
        assertEquals(0, todaySalesB.totalBills)

        val salesReportB = container.billingRepository.getSalesReport(restaurantIdB, ReportPeriod.TODAY).first()
        assertEquals(0.0, salesReportB.totalRevenue, 0.001)
        assertEquals(0, salesReportB.totalOrders)

        // Step 19: AI Prediction and Demand Forecasting data access
        val predictions = container.predictionRepository.getPredictionsByDate(restaurantIdA, dateString).first()
        assertNotNull("Prediction repository query must succeed", predictions)
    }

    @Test
    fun testDynamicRestaurantNameAndReportIsolation() = runBlocking {
        val emailA = "rest_a_${UUID.randomUUID().toString().take(6)}@foodwise.com"
        val regA = container.authRepository.register(
            email = emailA,
            password = "Password@123",
            restaurantName = "Test Hotel A",
            ownerName = "Owner A",
            phone = "9876500001",
            address = "Street A"
        )
        assertTrue(regA.isSuccess)
        val restA = regA.getOrThrow()
        val restIdA = restA.id

        // Verify active restaurant is Test Hotel A
        val loadedRestA = container.restaurantRepository.getRestaurant(restIdA).first()
        assertNotNull(loadedRestA)
        assertEquals("Test Hotel A", loadedRestA?.restaurantName)

        val emailB = "rest_b_${UUID.randomUUID().toString().take(6)}@foodwise.com"
        val regB = container.authRepository.register(
            email = emailB,
            password = "Password@123",
            restaurantName = "Test Restaurant B",
            ownerName = "Owner B",
            phone = "9876500002",
            address = "Street B"
        )
        assertTrue(regB.isSuccess)
        val restB = regB.getOrThrow()
        val restIdB = restB.id

        // Verify active restaurant is Test Restaurant B
        val loadedRestB = container.restaurantRepository.getRestaurant(restIdB).first()
        assertNotNull(loadedRestB)
        assertEquals("Test Restaurant B", loadedRestB?.restaurantName)

        // Verify Restaurant B sees empty inventory, waste, sales without falling back to fake metrics
        val invStatsB = container.inventoryRepository.getInventoryStats(restIdB).first()
        assertEquals(0, invStatsB.totalItems)
        assertEquals(0, invStatsB.sufficientCount)
        assertEquals(0, invStatsB.lowStockCount)
        assertEquals(0, invStatsB.outOfStockCount)

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val wasteStatsB = container.wasteRepository.getWasteSummary(restIdB, todayDate).first()
        assertEquals(0.0, wasteStatsB.todayWasteKg, 0.001)
        assertEquals(0.0, wasteStatsB.last7DaysWasteKg, 0.001)
        assertEquals(0.0, wasteStatsB.thisMonthWasteKg, 0.001)

        val salesReportB = container.billingRepository.getSalesReport(restIdB, ReportPeriod.TODAY).first()
        assertEquals(0.0, salesReportB.totalRevenue, 0.001)
        assertEquals(0, salesReportB.totalOrders)
    }
}
