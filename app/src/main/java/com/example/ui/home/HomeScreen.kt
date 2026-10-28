package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.R
import com.example.SmartRestaurantApp
import com.example.data.inventory.ExpiryTrackerService
import com.example.data.notification.ExpiryNotificationManager
import com.example.model.DailyRevenuePoint
import com.example.model.ExpiryStats
import com.example.model.ExpiryStatus
import com.example.model.FoodDemandPrediction
import com.example.model.FoodSalesStat
import com.example.model.InventoryItem
import com.example.model.InventoryStats
import com.example.model.PurchaseRecommendation
import com.example.model.PurchaseStatus
import com.example.model.ReportPeriod
import com.example.model.SalesReportData
import com.example.model.WasteInsight
import com.example.model.WasteSummary
import com.example.worker.ExpiryCheckWorker
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Theme Color Palette directly matching Food Wise Executive Restaurant Design
private val PageBackground = Color(0xFFFBF9F4)
private val CardWhite = Color(0xFFFFFFFF)
private val ForestGreenDark = Color(0xFF1B3B2B)
private val ForestGreenMedium = Color(0xFF28543E)
private val SageGreenActive = Color(0xFFE2EFE7)
private val SageGreenText = Color(0xFF2A674A)
private val WarmGoldAccent = Color(0xFFD48A36)
private val WarmGoldLight = Color(0xFFFEF3C7)
private val AlertRed = Color(0xFFD32F2F)
private val AlertRedLight = Color(0xFFFEEBEA)
private val AlertOrange = Color(0xFFD96B27)
private val AlertOrangeLight = Color(0xFFFEF3E8)
private val AlertYellow = Color(0xFFCA8A04)
private val AlertYellowLight = Color(0xFFFEF9E6)
private val MutedText = Color(0xFF6B7280)
private val BorderSubtle = Color(0xFFEBE5DA)
private val LineChartGreen = Color(0xFF3B6B4C)
private val LineChartFill = Color(0xFFE4F0E8)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToMenu: () -> Unit,
    onNavigateToAddMenuItem: () -> Unit,
    onNavigateToBilling: () -> Unit,
    onNavigateToBillHistory: () -> Unit,
    onNavigateToSalesReports: () -> Unit,
    onNavigateToAIPrediction: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToExpiryTracking: () -> Unit,
    onNavigateToGroceryRequirement: () -> Unit,
    onNavigateToPurchaseRecommendations: () -> Unit,
    onNavigateToWasteManagement: () -> Unit = {},
    onNavigateToSuppliers: () -> Unit = {},
    onNavigateToAIAssistant: () -> Unit = {},
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val container = remember(context) { (context.applicationContext as SmartRestaurantApp).container }

    val activeRestaurant by viewModel.activeRestaurant.collectAsStateWithLifecycle()
    val todaySalesSummary by viewModel.todaySalesSummary.collectAsStateWithLifecycle()
    val isDemoLoading by viewModel.isDemoLoading.collectAsStateWithLifecycle()
    val demoSuccessMessage by viewModel.demoSuccessMessage.collectAsStateWithLifecycle()
    val demoErrorMessage by viewModel.demoErrorMessage.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    var isSafeDemoLoading by remember { mutableStateOf(false) }
    var safeDemoSuccessMessage by remember { mutableStateOf<String?>(null) }
    var safeDemoErrorMessage by remember { mutableStateOf<String?>(null) }

    val restaurantId = activeRestaurant?.id ?: 0L
    val restaurantName = activeRestaurant?.restaurantName?.takeIf { it.isNotBlank() } ?: ""
    val managerName = activeRestaurant?.ownerName?.takeIf { it.isNotBlank() } ?: ""

    val triggerSafeLoadDemoData: () -> Unit = {
        if (restaurantId > 0L) {
            coroutineScope.launch {
                isSafeDemoLoading = true
                safeDemoErrorMessage = null
                safeDemoSuccessMessage = null
                try {
                    // CRITICAL: updateProfile = false guarantees the restaurant's authenticated name and owner are NEVER overwritten
                    val result = container.demoDataRepository.loadDemoData(
                        restaurantId = restaurantId,
                        updateProfile = false,
                        profileId = "royal_spice"
                    )
                    if (result.isSuccess) {
                        safeDemoSuccessMessage = "Sample restaurant dataset successfully loaded for $restaurantName!"
                    } else {
                        safeDemoErrorMessage = result.exceptionOrNull()?.message ?: "Failed to load demo data."
                    }
                } catch (e: Exception) {
                    safeDemoErrorMessage = e.message ?: "An unexpected error occurred loading demo data."
                } finally {
                    isSafeDemoLoading = false
                }
            }
        }
    }

    // Dynamic Formatted Dates
    val todayFormatted = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date()) }
    val todayDayOfWeek = remember { SimpleDateFormat("EEEE,", Locale.getDefault()).format(Date()) }
    val todayDateMonthYear = remember { SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date()) }
    val todayDateDb = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val tomorrowDateDb = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    // Connect to Real Isolated Repositories
    val inventoryStats by remember(restaurantId) {
        if (restaurantId > 0) container.inventoryRepository.getInventoryStats(restaurantId)
        else kotlinx.coroutines.flow.flowOf(InventoryStats(totalItems = 0, sufficientCount = 0, lowStockCount = 0, outOfStockCount = 0))
    }.collectAsStateWithLifecycle(initialValue = InventoryStats(totalItems = 0, sufficientCount = 0, lowStockCount = 0, outOfStockCount = 0))

    val expiryStats by remember(restaurantId, todayDateDb) {
        if (restaurantId > 0) container.inventoryRepository.getExpiryStats(restaurantId, todayDateDb)
        else kotlinx.coroutines.flow.flowOf(ExpiryStats(totalTracked = 0, safeCount = 0, expiringSoonCount = 0, expiringTodayCount = 0, expiredCount = 0, noExpiryCount = 0))
    }.collectAsStateWithLifecycle(initialValue = ExpiryStats(totalTracked = 0, safeCount = 0, expiringSoonCount = 0, expiringTodayCount = 0, expiredCount = 0, noExpiryCount = 0))

    val inventoryItems by remember(restaurantId) {
        if (restaurantId > 0) container.inventoryRepository.getInventoryItems(restaurantId)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val wasteSummary by remember(restaurantId, todayDateDb) {
        if (restaurantId > 0) container.wasteRepository.getWasteSummary(restaurantId, todayDateDb)
        else kotlinx.coroutines.flow.flowOf(WasteSummary(todayWasteKg = 0.0, last7DaysWasteKg = 0.0, thisMonthWasteKg = 0.0, totalRecordsCount = 0, totalQuantityKgEquivalent = 0.0, degradableQuantityKg = 0.0, nonDegradableQuantityKg = 0.0))
    }.collectAsStateWithLifecycle(initialValue = WasteSummary(todayWasteKg = 0.0, last7DaysWasteKg = 0.0, thisMonthWasteKg = 0.0, totalRecordsCount = 0, totalQuantityKgEquivalent = 0.0, degradableQuantityKg = 0.0, nonDegradableQuantityKg = 0.0))

    val wasteInsights by remember(restaurantId) {
        if (restaurantId > 0) container.wasteRepository.getWasteInsights(restaurantId)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedChartTimeframe by remember { mutableStateOf("Weekly") }
    val currentReportPeriod = when (selectedChartTimeframe) {
        "Daily", "Today" -> ReportPeriod.TODAY
        "Monthly" -> ReportPeriod.THIS_MONTH
        else -> ReportPeriod.THIS_WEEK
    }

    val salesReportData by remember(restaurantId, currentReportPeriod) {
        if (restaurantId > 0) container.billingRepository.getSalesReport(restaurantId, currentReportPeriod, null, null)
        else kotlinx.coroutines.flow.flowOf(SalesReportData(period = currentReportPeriod, startDateFormatted = "", endDateFormatted = "", startTimestamp = 0, endTimestamp = 0, totalRevenue = 0.0, totalOrders = 0, totalItemsSold = 0, averageOrderValue = 0.0, topSellingFood = null, leastSellingFood = null, bestSalesDay = null, foodSales = emptyList(), categorySales = emptyList(), dailyTrend = emptyList()))
    }.collectAsStateWithLifecycle(initialValue = SalesReportData(period = currentReportPeriod, startDateFormatted = "", endDateFormatted = "", startTimestamp = 0, endTimestamp = 0, totalRevenue = 0.0, totalOrders = 0, totalItemsSold = 0, averageOrderValue = 0.0, topSellingFood = null, leastSellingFood = null, bestSalesDay = null, foodSales = emptyList(), categorySales = emptyList(), dailyTrend = emptyList()))

    val predictions by remember(restaurantId, tomorrowDateDb) {
        if (restaurantId > 0) container.predictionRepository.getPredictionsByDate(restaurantId, tomorrowDateDb)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val recommendations by remember(restaurantId, todayDateDb) {
        if (restaurantId > 0) container.purchaseRecommendationRepository.getRecommendations(restaurantId, todayDateDb)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    // Notification Permission Launcher (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && restaurantId > 0 && inventoryItems.isNotEmpty()) {
            ExpiryNotificationManager.createNotificationChannel(context)
            ExpiryNotificationManager.checkAndNotifyExpiringItems(
                context = context,
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                items = inventoryItems,
                todayDateStr = todayDateDb
            )
        }
    }

    // Trigger Notification Check and Schedule Background Worker
    LaunchedEffect(restaurantId, inventoryItems) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (restaurantId > 0 && inventoryItems.isNotEmpty()) {
            ExpiryNotificationManager.createNotificationChannel(context)
            ExpiryNotificationManager.checkAndNotifyExpiringItems(
                context = context,
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                items = inventoryItems,
                todayDateStr = todayDateDb
            )
        }
        if (restaurantId > 0) {
            ExpiryCheckWorker.schedulePeriodicCheck(context)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedNavIndex by remember { mutableStateOf(0) }
    var showProfileMenu by remember { mutableStateOf(false) }
    var showChartDropdown by remember { mutableStateOf(false) }
    var showNotificationCenter by remember { mutableStateOf(false) }

    val mainScrollState = rememberScrollState()
    val sidebarScrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .testTag("home_screen_root")
    ) {
        val isWideScreen = maxWidth >= 768.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // =========================================================================
            // 1. TOP HEADER (BRANDING, SEARCH, NOTIFICATION, RESTAURANT / USER PROFILE)
            // =========================================================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PageBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Brand: Uncropped Food Wise Circular Logo + Title + Subtitle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_food_wise_logo),
                            contentDescription = "Food Wise Logo",
                            modifier = Modifier.size(42.dp),
                            contentScale = ContentScale.Fit
                        )

                        Column {
                            Text(
                                text = "FOOD WISE",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = "SMART RESTAURANT MANAGEMENT",
                                fontSize = 9.sp,
                                color = ForestGreenMedium,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Center / Right Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Search Box (Visible on mobile & wide screen)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CardWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier
                                .width(if (isWideScreen) 180.dp else 125.dp)
                                .height(36.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search",
                                    tint = MutedText,
                                    modifier = Modifier.size(14.dp)
                                )
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 12.sp, color = ForestGreenDark),
                                    cursorBrush = SolidColor(ForestGreenMedium),
                                    decorationBox = { innerTextField ->
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = "Search...",
                                                fontSize = 11.sp,
                                                color = MutedText
                                            )
                                        }
                                        innerTextField()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Notification Bell with Actionable Notification Center Dialog
                        val criticalBellCount = remember(inventoryItems, todayDateDb) {
                            inventoryItems.count { item ->
                                val status = ExpiryTrackerService.calculateExpiryStatus(item.expiryDate, item.isPerishable, todayDateDb)
                                status == ExpiryStatus.EXPIRED || status == ExpiryStatus.EXPIRING_TODAY
                            }
                        }
                        val expiringSoonBellCount = remember(inventoryItems, todayDateDb) {
                            inventoryItems.count { item ->
                                val status = ExpiryTrackerService.calculateExpiryStatus(item.expiryDate, item.isPerishable, todayDateDb)
                                status == ExpiryStatus.EXPIRING_SOON
                            }
                        }
                        val stockAlertBellCount = inventoryStats.lowStockCount + inventoryStats.outOfStockCount
                        val notificationCount = (criticalBellCount + expiringSoonBellCount + stockAlertBellCount + wasteInsights.size).coerceAtLeast(0)
                        Box(
                            modifier = Modifier
                                .clickable { showNotificationCenter = true }
                                .padding(2.dp)
                                .testTag("notification_bell_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CardWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Notifications,
                                        contentDescription = "Notifications",
                                        tint = ForestGreenDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            if (notificationCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .background(AlertOrange, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (notificationCount > 9) "9+" else notificationCount.toString(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Dedicated Direct Profile Navigation Button
                        IconButton(
                            onClick = onNavigateToProfile,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("top_bar_profile_button")
                                .testTag("dashboard_profile_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(ForestGreenMedium, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Profile & Settings",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 2. DESKTOP / TABLET TWO-COLUMN LAYOUT: (SIDEBAR + MAIN DASHBOARD)
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // -------------------------------------------------------------
                // LEFT SIDEBAR (STICKY ON DESKTOP/WIDE SCREEN)
                // -------------------------------------------------------------
                if (isWideScreen) {
                    Column(
                        modifier = Modifier
                            .width(220.dp)
                            .fillMaxHeight()
                            .background(PageBackground)
                            .border(width = 1.dp, color = BorderSubtle.copy(alpha = 0.6f))
                            .verticalScroll(sidebarScrollState)
                            .padding(horizontal = 14.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SidebarNavItem(
                                title = "Dashboard",
                                icon = Icons.Filled.Dashboard,
                                isSelected = selectedNavIndex == 0,
                                onClick = { selectedNavIndex = 0 }
                            )
                            SidebarNavItem(
                                title = "Menu",
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                isSelected = selectedNavIndex == 1,
                                onClick = {
                                    selectedNavIndex = 1
                                    onNavigateToMenu()
                                }
                            )
                            SidebarNavItem(
                                title = "Billing & POS",
                                icon = Icons.Filled.PointOfSale,
                                isSelected = selectedNavIndex == 2,
                                onClick = {
                                    selectedNavIndex = 2
                                    onNavigateToBilling()
                                }
                            )
                            SidebarNavItem(
                                title = "Sales Reports",
                                icon = Icons.AutoMirrored.Filled.TrendingUp,
                                isSelected = selectedNavIndex == 3,
                                onClick = {
                                    selectedNavIndex = 3
                                    onNavigateToSalesReports()
                                }
                            )
                            SidebarNavItem(
                                title = "AI Demand\nPrediction",
                                icon = Icons.Filled.Analytics,
                                isSelected = selectedNavIndex == 4,
                                onClick = {
                                    selectedNavIndex = 4
                                    onNavigateToAIPrediction()
                                }
                            )
                            SidebarNavItem(
                                title = "Inventory &\nExpiry",
                                icon = Icons.Filled.Inventory2,
                                isSelected = selectedNavIndex == 5,
                                onClick = {
                                    selectedNavIndex = 5
                                    onNavigateToInventory()
                                }
                            )
                            SidebarNavItem(
                                title = "Purchase\nRecommendations",
                                icon = Icons.Filled.Lightbulb,
                                isSelected = selectedNavIndex == 6,
                                onClick = {
                                    selectedNavIndex = 6
                                    onNavigateToPurchaseRecommendations()
                                }
                            )
                            SidebarNavItem(
                                title = "Waste\nManagement",
                                icon = Icons.Filled.DeleteOutline,
                                isSelected = selectedNavIndex == 7,
                                onClick = {
                                    selectedNavIndex = 7
                                    onNavigateToWasteManagement()
                                }
                            )
                            SidebarNavItem(
                                title = "Supplier\nManagement",
                                icon = Icons.Filled.PersonAdd,
                                isSelected = selectedNavIndex == 8,
                                onClick = {
                                    selectedNavIndex = 8
                                    onNavigateToSuppliers()
                                }
                            )
                            SidebarNavItem(
                                title = "AI Assistant",
                                icon = Icons.Filled.SmartToy,
                                isSelected = selectedNavIndex == 9,
                                onClick = {
                                    selectedNavIndex = 9
                                    onNavigateToAIAssistant()
                                }
                            )
                            SidebarNavItem(
                                title = "Settings",
                                icon = Icons.Filled.Settings,
                                isSelected = selectedNavIndex == 10,
                                onClick = {
                                    selectedNavIndex = 10
                                    onNavigateToProfile()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Logout Button at bottom of sidebar
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFDF3EB),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3D8C3)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.logout(onLogout) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Logout",
                                        tint = AlertOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Logout",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AlertOrange
                                    )
                                }
                            }

                            // Decorative Cozy Dining Table Illustration
                            CozyRestaurantDiningGraphic(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // MAIN DASHBOARD CONTENT AREA
                // -------------------------------------------------------------
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(mainScrollState)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // =========================================================================
                    // 1. SHORTCUT NAVIGATION ROW
                    // =========================================================================
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .testTag("dashboard_shortcuts_row"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ShortcutPill(
                            icon = Icons.Filled.PointOfSale,
                            label = "Billing",
                            testTag = "shortcut_billing",
                            onClick = onNavigateToBilling
                        )
                        ShortcutPill(
                            icon = Icons.AutoMirrored.Filled.ReceiptLong,
                            label = "Orders",
                            testTag = "shortcut_orders",
                            onClick = onNavigateToBillHistory
                        )
                        ShortcutPill(
                            icon = Icons.Filled.Inventory2,
                            label = "Inventory",
                            testTag = "shortcut_inventory",
                            onClick = onNavigateToInventory
                        )
                        ShortcutPill(
                            icon = Icons.Filled.DeleteOutline,
                            label = "Expiry",
                            testTag = "shortcut_expiry",
                            onClick = onNavigateToExpiryTracking
                        )
                    }

                    // =========================================================================
                    // 2. LOAD DEMO / ONBOARDING CARD
                    // =========================================================================
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("empty_state_demo_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmGoldLight),
                        border = BorderStroke(1.dp, WarmGoldAccent.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Welcome to Food Wise! 🍽️",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                                Text(
                                    text = "Get started immediately by loading sample dishes, ingredient stocks, expiry alerts, and sales analytics.",
                                    fontSize = 12.sp,
                                    color = ForestGreenDark.copy(alpha = 0.8f),
                                    lineHeight = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = { triggerSafeLoadDemoData() },
                                enabled = !isDemoLoading && !isSafeDemoLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = WarmGoldAccent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("dashboard_load_demo_data_button")
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Load Demo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    // =========================================================================
                    // 3. FEEDBACK ALERTS & DEMO DATA LOADING STATUS
                    // =========================================================================
                    AnimatedVisibility(visible = isDemoLoading || isSafeDemoLoading) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WarmGoldLight),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = WarmGoldAccent)
                                Text("Loading sample datasets for $restaurantName...", fontSize = 13.sp, color = ForestGreenDark, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    (safeDemoSuccessMessage ?: demoSuccessMessage)?.let { msg ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SageGreenActive),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SageGreenText)
                                    Text(text = msg, fontSize = 13.sp, color = ForestGreenDark)
                                }
                                IconButton(
                                    onClick = {
                                        safeDemoSuccessMessage = null
                                        viewModel.clearDemoNotification()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    (safeDemoErrorMessage ?: demoErrorMessage)?.let { err ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AlertRedLight),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = AlertRed)
                                    Text(text = err, fontSize = 13.sp, color = AlertRed)
                                }
                                IconButton(
                                    onClick = {
                                        safeDemoErrorMessage = null
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // =========================================================================
                    // 4. WELCOME + DATE ROW
                    // =========================================================================
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("welcome_card"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Welcome ...",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Here's your restaurant overview...",
                                fontSize = 11.sp,
                                color = MutedText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CardWhite,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.testTag("dashboard_date_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = "Date",
                                    tint = ForestGreenDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$todayDayOfWeek, $todayDateMonthYear",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                            }
                        }
                    }

                    // =========================================================================
                    // 5. 8 MAIN RESTAURANT MODULE CARDS (2-COLUMN GRID)
                    // =========================================================================
                    DashboardModuleCardsSection(
                        onNavigateToMenu = onNavigateToMenu,
                        onNavigateToBilling = onNavigateToBilling,
                        onNavigateToBillHistory = onNavigateToBillHistory,
                        onNavigateToInventory = onNavigateToInventory,
                        onNavigateToExpiryTracking = onNavigateToExpiryTracking,
                        onNavigateToSalesReports = onNavigateToSalesReports,
                        onNavigateToAIPrediction = onNavigateToAIPrediction,
                        onNavigateToAIAssistant = onNavigateToAIAssistant,
                        isWideScreen = isWideScreen
                    )

                    // =========================================================================
                    // 4. 4 SUMMARY STATISTIC CARDS IN A ROW
                    // =========================================================================
                    val totalRev = if (todaySalesSummary.totalRevenue > 0) todaySalesSummary.totalRevenue else salesReportData.totalRevenue
                    val displayRevenue = "₹ " + NumberFormat.getNumberInstance(Locale("en", "IN")).format(totalRev.toLong())

                    val totalOrd = if (todaySalesSummary.totalBills > 0) todaySalesSummary.totalBills else salesReportData.totalOrders
                    val displayOrders = totalOrd.toString()

                    val displayLowStock = inventoryStats.lowStockCount.toString()
                    val displayWaste = "₹ " + NumberFormat.getNumberInstance(Locale("en", "IN")).format((wasteSummary.todayWasteKg * 120).toLong())

                    if (isWideScreen) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            FoodWiseStatCard(
                                title = "Today's Sales",
                                value = displayRevenue,
                                trendText = "12.5% vs yesterday",
                                isPositiveTrend = true,
                                icon = Icons.Filled.ShoppingBag,
                                iconBg = Color(0xFFE5F3EB),
                                iconTint = Color(0xFF286445),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToSalesReports() }
                            )
                            FoodWiseStatCard(
                                title = "Total Orders",
                                value = displayOrders,
                                trendText = "8.3% vs yesterday",
                                isPositiveTrend = true,
                                icon = Icons.Filled.ReceiptLong,
                                iconBg = Color(0xFFFDF0E6),
                                iconTint = Color(0xFFD96B27),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToBillHistory() }
                            )
                            FoodWiseStatCard(
                                title = "Low Stock Items",
                                value = displayLowStock,
                                trendText = "${inventoryStats.outOfStockCount} out of stock",
                                isPositiveTrend = false,
                                icon = Icons.Filled.Inventory2,
                                iconBg = Color(0xFFFEF7E6),
                                iconTint = Color(0xFFD98A1E),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToInventory() }
                            )
                            FoodWiseStatCard(
                                title = "Today's Waste",
                                value = displayWaste,
                                trendText = "${wasteSummary.todayWasteKg} kg logged",
                                isPositiveTrend = false,
                                icon = Icons.Filled.DeleteOutline,
                                iconBg = Color(0xFFE7F4EE),
                                iconTint = Color(0xFF2E7D52),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToWasteManagement() }
                            )
                        }
                    } else {
                        // 2x2 grid on narrower screens
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FoodWiseStatCard(
                                    title = "Today's Sales",
                                    value = displayRevenue,
                                    trendText = "12.5% vs yesterday",
                                    isPositiveTrend = true,
                                    icon = Icons.Filled.ShoppingBag,
                                    iconBg = Color(0xFFE5F3EB),
                                    iconTint = Color(0xFF286445),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToSalesReports() }
                                )
                                FoodWiseStatCard(
                                    title = "Total Orders",
                                    value = displayOrders,
                                    trendText = "8.3% vs yesterday",
                                    isPositiveTrend = true,
                                    icon = Icons.Filled.ReceiptLong,
                                    iconBg = Color(0xFFFDF0E6),
                                    iconTint = Color(0xFFD96B27),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToBillHistory() }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FoodWiseStatCard(
                                    title = "Low Stock Items",
                                    value = displayLowStock,
                                    trendText = "${inventoryStats.outOfStockCount} out of stock",
                                    isPositiveTrend = false,
                                    icon = Icons.Filled.Inventory2,
                                    iconBg = Color(0xFFFEF7E6),
                                    iconTint = Color(0xFFD98A1E),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToInventory() }
                                )
                                FoodWiseStatCard(
                                    title = "Today's Waste",
                                    value = displayWaste,
                                    trendText = "${wasteSummary.todayWasteKg} kg logged",
                                    isPositiveTrend = false,
                                    icon = Icons.Filled.DeleteOutline,
                                    iconBg = Color(0xFFE7F4EE),
                                    iconTint = Color(0xFF2E7D52),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToWasteManagement() }
                                )
                            }
                        }
                    }

                    // =========================================================================
                    // 5 & 6. ROW 1: SALES OVERVIEW (LEFT) + TOP SELLING ITEMS (RIGHT)
                    // =========================================================================
                    if (isWideScreen) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left: Sales Overview Card
                            Card(
                                modifier = Modifier.weight(1.25f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                SalesOverviewContent(
                                    selectedTimeframe = selectedChartTimeframe,
                                    showDropdown = showChartDropdown,
                                    trendPoints = salesReportData.dailyTrend,
                                    onToggleDropdown = { showChartDropdown = !showChartDropdown },
                                    onSelectTimeframe = {
                                        selectedChartTimeframe = it
                                        showChartDropdown = false
                                    }
                                )
                            }

                            // Right: Top Selling Items Card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                TopSellingItemsContent(
                                    foodSales = salesReportData.foodSales,
                                    onNavigateToMenu = onNavigateToMenu
                                )
                            }
                        }
                    } else {
                        // Stacked on narrow screens
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            SalesOverviewContent(
                                selectedTimeframe = selectedChartTimeframe,
                                showDropdown = showChartDropdown,
                                trendPoints = salesReportData.dailyTrend,
                                onToggleDropdown = { showChartDropdown = !showChartDropdown },
                                onSelectTimeframe = {
                                    selectedChartTimeframe = it
                                    showChartDropdown = false
                                }
                            )
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            TopSellingItemsContent(
                                foodSales = salesReportData.foodSales,
                                onNavigateToMenu = onNavigateToMenu
                            )
                        }
                    }

                    // =========================================================================
                    // 7 & 8. ROW 2: INVENTORY SUMMARY (LEFT) + EXPIRY ALERTS & FEFO (RIGHT)
                    // =========================================================================
                    if (isWideScreen) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left: Inventory Summary Card
                            Card(
                                modifier = Modifier.weight(1.25f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                InventorySummaryContent(
                                    stats = inventoryStats,
                                    onNavigateToInventory = onNavigateToInventory
                                )
                            }

                            // Right: Expiry Alerts & FEFO Card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                ExpiryAndFEFOContent(
                                    stats = expiryStats,
                                    items = inventoryItems,
                                    todayDateDb = todayDateDb,
                                    onNavigateToExpiryTracking = onNavigateToExpiryTracking
                                )
                            }
                        }
                    } else {
                        // Stacked on narrow screens
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            InventorySummaryContent(
                                stats = inventoryStats,
                                onNavigateToInventory = onNavigateToInventory
                            )
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            ExpiryAndFEFOContent(
                                stats = expiryStats,
                                items = inventoryItems,
                                todayDateDb = todayDateDb,
                                onNavigateToExpiryTracking = onNavigateToExpiryTracking
                            )
                        }
                    }

                    // =========================================================================
                    // 9 & 10. ROW 3: AI DEMAND PREDICTION + SMART PURCHASE RECOMMENDATIONS
                    // =========================================================================
                    if (isWideScreen) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left: AI Demand Prediction Card
                            Card(
                                modifier = Modifier.weight(1.25f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                AIDemandPredictionContent(
                                    predictions = predictions,
                                    onNavigateToAIPrediction = onNavigateToAIPrediction
                                )
                            }

                            // Right: Smart Purchase Recommendations Card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                PurchaseRecommendationsContent(
                                    recommendations = recommendations,
                                    onNavigateToPurchaseRecommendations = onNavigateToPurchaseRecommendations
                                )
                            }
                        }
                    } else {
                        // Stacked on narrow screens
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            AIDemandPredictionContent(
                                predictions = predictions,
                                onNavigateToAIPrediction = onNavigateToAIPrediction
                            )
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            PurchaseRecommendationsContent(
                                recommendations = recommendations,
                                onNavigateToPurchaseRecommendations = onNavigateToPurchaseRecommendations
                            )
                        }
                    }

                    // =========================================================================
                    // 11. AI ASSISTANT CARD (INTELLIGENT RESTAURANT MANAGEMENT BOT)
                    // =========================================================================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        AIAssistantContent(
                            managerName = managerName,
                            onNavigateToAIAssistant = onNavigateToAIAssistant
                        )
                    }

                    // =========================================================================
                    // 12. QUICK ACTIONS SECTION (5 ATTRACTIVE BUTTONS IN A ROW)
                    // =========================================================================
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Quick Actions",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )

                        if (isWideScreen) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                QuickActionButton(
                                    title = "Add Menu Item",
                                    icon = Icons.Filled.Add,
                                    iconBg = Color(0xFF387B52),
                                    onClick = onNavigateToAddMenuItem,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    title = "Create Bill",
                                    icon = Icons.Filled.ReceiptLong,
                                    iconBg = Color(0xFFE06D2D),
                                    onClick = onNavigateToBilling,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    title = "Add Stock",
                                    icon = Icons.Filled.Inventory2,
                                    iconBg = Color(0xFFE5A124),
                                    onClick = onNavigateToInventory,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    title = "Add Supplier",
                                    icon = Icons.Filled.PersonAdd,
                                    iconBg = Color(0xFF4A8B63),
                                    onClick = onNavigateToSuppliers,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    title = "Generate Report",
                                    icon = Icons.Filled.Description,
                                    iconBg = Color(0xFF5C7CBA),
                                    onClick = onNavigateToSalesReports,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                QuickActionButton(
                                    title = "Add Menu Item",
                                    icon = Icons.Filled.Add,
                                    iconBg = Color(0xFF387B52),
                                    onClick = onNavigateToAddMenuItem,
                                    modifier = Modifier.width(125.dp)
                                )
                                QuickActionButton(
                                    title = "Create Bill",
                                    icon = Icons.Filled.ReceiptLong,
                                    iconBg = Color(0xFFE06D2D),
                                    onClick = onNavigateToBilling,
                                    modifier = Modifier.width(125.dp)
                                )
                                QuickActionButton(
                                    title = "Add Stock",
                                    icon = Icons.Filled.Inventory2,
                                    iconBg = Color(0xFFE5A124),
                                    onClick = onNavigateToInventory,
                                    modifier = Modifier.width(125.dp)
                                )
                                QuickActionButton(
                                    title = "Add Supplier",
                                    icon = Icons.Filled.PersonAdd,
                                    iconBg = Color(0xFF4A8B63),
                                    onClick = onNavigateToSuppliers,
                                    modifier = Modifier.width(125.dp)
                                )
                                QuickActionButton(
                                    title = "Generate Report",
                                    icon = Icons.Filled.Description,
                                    iconBg = Color(0xFF5C7CBA),
                                    onClick = onNavigateToSalesReports,
                                    modifier = Modifier.width(125.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Actionable Notification Center Dialog
        if (showNotificationCenter) {
            ActionableNotificationCenterDialog(
                expiryStats = expiryStats,
                inventoryItems = inventoryItems,
                wasteInsights = wasteInsights,
                todayDateDb = todayDateDb,
                tomorrowDateDb = tomorrowDateDb,
                onDismiss = { showNotificationCenter = false },
                onNavigateToExpiryTracking = {
                    showNotificationCenter = false
                    onNavigateToExpiryTracking()
                },
                onNavigateToPurchaseRecommendations = {
                    showNotificationCenter = false
                    onNavigateToPurchaseRecommendations()
                },
                onNavigateToWasteManagement = {
                    showNotificationCenter = false
                    onNavigateToWasteManagement()
                },
                onNavigateToSuppliers = {
                    showNotificationCenter = false
                    onNavigateToSuppliers()
                },
                onNavigateToInventory = {
                    showNotificationCenter = false
                    onNavigateToInventory()
                }
            )
        }
    }
}

// =========================================================================
// SUBCOMPONENTS
// =========================================================================

@Composable
private fun SidebarNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SageGreenActive else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) ForestGreenDark else Color(0xFF334B3E),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ForestGreenDark else Color(0xFF334B3E),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun MobileNavChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = ForestGreenMedium, modifier = Modifier.size(16.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = ForestGreenDark)
        }
    }
}

@Composable
private fun FoodWiseStatCard(
    title: String,
    value: String,
    trendText: String,
    isPositiveTrend: Boolean,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Rounded Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenDark
                )
            }

            // Trend Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isPositiveTrend) "↑" else "↓",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositiveTrend) Color(0xFF286445) else Color(0xFFD32F2F)
                )
                Text(
                    text = trendText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isPositiveTrend) Color(0xFF286445) else Color(0xFFD32F2F)
                )
            }
        }
    }
}

@Composable
private fun SalesOverviewContent(
    selectedTimeframe: String,
    showDropdown: Boolean,
    trendPoints: List<DailyRevenuePoint>,
    onToggleDropdown: () -> Unit,
    onSelectTimeframe: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sales Overview",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenDark
            )

            Box {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PageBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.clickable { onToggleDropdown() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = selectedTimeframe,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ForestGreenDark
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = ForestGreenMedium,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = onToggleDropdown
                ) {
                    DropdownMenuItem(text = { Text("Daily") }, onClick = { onSelectTimeframe("Daily") })
                    DropdownMenuItem(text = { Text("Weekly") }, onClick = { onSelectTimeframe("Weekly") })
                    DropdownMenuItem(text = { Text("Monthly") }, onClick = { onSelectTimeframe("Monthly") })
                }
            }
        }

        // Custom High-Precision Line Graph matching reference image
        SalesLineChart(
            trendPoints = trendPoints,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
    }
}

@Composable
private fun SalesLineChart(
    trendPoints: List<DailyRevenuePoint>,
    modifier: Modifier = Modifier
) {
    val yLabels = listOf("50K", "40K", "30K", "20K", "10K", "0")
    val defaultXLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val maxRev = (trendPoints.maxOfOrNull { it.revenue } ?: 50000.0).coerceAtLeast(1000.0)

    Canvas(modifier = modifier) {
        val labelWidth = 36.dp.toPx()
        val bottomLabelHeight = 22.dp.toPx()
        val chartWidth = size.width - labelWidth
        val chartHeight = size.height - bottomLabelHeight

        // Horizontal Grid Lines
        val gridStep = chartHeight / (yLabels.size - 1)
        for (i in yLabels.indices) {
            val y = i * gridStep
            drawLine(
                color = Color(0xFFEDE9E0),
                start = Offset(labelWidth, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        if (trendPoints.size == 1) {
            // SINGLE DATA POINT (Daily / Today Sales)
            val pt = trendPoints[0]
            val normValue = (pt.revenue / maxRev).toFloat().coerceIn(0.15f, 0.85f)
            val y = chartHeight - (normValue * chartHeight)
            val centerX = labelWidth + chartWidth / 2f

            // Dashed horizontal line across chart width
            drawLine(
                color = LineChartGreen.copy(alpha = 0.6f),
                start = Offset(labelWidth, y),
                end = Offset(size.width, y),
                strokeWidth = 2.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // Shaded area under the line
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(LineChartFill.copy(alpha = 0.6f), LineChartFill.copy(alpha = 0.05f)),
                    startY = y,
                    endY = chartHeight
                ),
                topLeft = Offset(labelWidth, y),
                size = Size(chartWidth, chartHeight - y)
            )

            // Glowing center node
            drawCircle(
                color = LineChartGreen,
                radius = 6.5.dp.toPx(),
                center = Offset(centerX, y)
            )
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = Offset(centerX, y)
            )
        } else if (trendPoints.size > 1) {
            // MULTI DATA POINTS (Weekly, Monthly, Yearly)
            val stepX = chartWidth / (trendPoints.size - 1)
            val pixelPoints = trendPoints.mapIndexed { index, pt ->
                val normValue = (pt.revenue / maxRev).toFloat().coerceIn(0.05f, 0.95f)
                val x = labelWidth + index * stepX
                val y = chartHeight - (normValue * chartHeight)
                Offset(x, y)
            }

            val path = Path()
            val fillPath = Path()

            path.moveTo(pixelPoints[0].x, pixelPoints[0].y)
            fillPath.moveTo(pixelPoints[0].x, chartHeight)
            fillPath.lineTo(pixelPoints[0].x, pixelPoints[0].y)

            for (i in 0 until pixelPoints.size - 1) {
                val p0 = pixelPoints[i]
                val p1 = pixelPoints[i + 1]
                val controlX1 = p0.x + (p1.x - p0.x) / 2
                val controlY1 = p0.y
                val controlX2 = p0.x + (p1.x - p0.x) / 2
                val controlY2 = p1.y

                path.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
            }

            fillPath.lineTo(pixelPoints.last().x, chartHeight)
            fillPath.close()

            // Area Gradient Fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(LineChartFill.copy(alpha = 0.8f), LineChartFill.copy(alpha = 0.1f)),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            // Green Stroke Line
            drawPath(
                path = path,
                color = LineChartGreen,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Circular nodes
            pixelPoints.forEach { point ->
                drawCircle(
                    color = LineChartGreen,
                    radius = 4.5.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = point
                )
            }
        } else {
            // BASELINE FOR 0 SALES DATA
            drawLine(
                color = LineChartGreen.copy(alpha = 0.3f),
                start = Offset(labelWidth, chartHeight - 10f),
                end = Offset(size.width, chartHeight - 10f),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }

    // X-Axis Labels
    val xLabels = when {
        trendPoints.isNotEmpty() -> trendPoints.map { it.dayLabel.take(6) }
        else -> defaultXLabels
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 36.dp, top = 4.dp),
        horizontalArrangement = if (xLabels.size <= 1) Arrangement.Center else Arrangement.SpaceBetween
    ) {
        xLabels.forEach { day ->
            Text(text = day, fontSize = 11.sp, color = MutedText, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TopSellingItemsContent(
    foodSales: List<FoodSalesStat>,
    onNavigateToMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Top Selling Items",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenDark
            )
            Text(
                text = "View Menu",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenMedium,
                modifier = Modifier
                    .clickable { onNavigateToMenu() }
                    .padding(2.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (foodSales.isNotEmpty()) {
                foodSales.take(5).forEachIndexed { index, stat ->
                    val emoji = when (index % 5) {
                        0 -> "🍛"
                        1 -> "🥘"
                        2 -> "🍚"
                        3 -> "🥢"
                        else -> "🍗"
                    }
                    val bg = when (index % 4) {
                        0 -> Color(0xFFFEF3E8)
                        1 -> Color(0xFFFEF9E6)
                        2 -> Color(0xFFE8F4EC)
                        else -> Color(0xFFFDEEE8)
                    }
                    TopSellingItemRow(
                        name = stat.foodName,
                        price = "₹ " + NumberFormat.getNumberInstance(Locale("en", "IN")).format(stat.totalRevenue.toLong()),
                        quantityInfo = "${stat.quantitySold} orders",
                        emoji = emoji,
                        bg = bg
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No sales recorded yet for this period",
                        fontSize = 12.sp,
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TopSellingItemRow(
    name: String,
    price: String,
    quantityInfo: String = "",
    emoji: String,
    bg: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = bg,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 16.sp)
                }
            }
            Column {
                Text(
                    text = name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = ForestGreenDark
                )
                if (quantityInfo.isNotBlank()) {
                    Text(
                        text = quantityInfo,
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }
            }
        }

        Text(
            text = price,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenDark
        )
    }
}

@Composable
private fun InventorySummaryContent(
    stats: InventoryStats,
    onNavigateToInventory: () -> Unit
) {
    val total = stats.totalItems.coerceAtLeast(1).toFloat()
    val sufficientPercent = (stats.sufficientCount / total).coerceIn(0.1f, 0.9f)
    val lowStockPercent = (stats.lowStockCount / total).coerceIn(0.05f, 0.8f)
    val outOfStockPercent = (1f - sufficientPercent - lowStockPercent).coerceAtLeast(0.05f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Inventory Summary",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenDark
            )
            Text(
                text = "Manage Stock",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenMedium,
                modifier = Modifier
                    .clickable { onNavigateToInventory() }
                    .padding(2.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Donut Chart
            InventoryDonutChart(
                sufficient = sufficientPercent,
                lowStock = lowStockPercent,
                outOfStock = outOfStockPercent,
                modifier = Modifier.size(110.dp)
            )

            // Legend on right
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                InventoryLegendItem(
                    color = Color(0xFF4B7B58),
                    label = "Sufficient",
                    count = stats.sufficientCount.toString()
                )
                InventoryLegendItem(
                    color = Color(0xFFE5A93C),
                    label = "Low Stock",
                    count = stats.lowStockCount.toString()
                )
                InventoryLegendItem(
                    color = Color(0xFFD95338),
                    label = "Out of Stock",
                    count = stats.outOfStockCount.toString()
                )
            }
        }
    }
}

@Composable
private fun InventoryDonutChart(
    sufficient: Float,
    lowStock: Float,
    outOfStock: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 22.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
        val arcSize = Size(diameter, diameter)

        var startAngle = -90f

        // Sufficient (Green)
        val sweepSufficient = 360f * sufficient
        drawArc(
            color = Color(0xFF4B7B58),
            startAngle = startAngle,
            sweepAngle = sweepSufficient,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        startAngle += sweepSufficient

        // Low Stock (Yellow)
        val sweepLow = 360f * lowStock
        drawArc(
            color = Color(0xFFE5A93C),
            startAngle = startAngle,
            sweepAngle = sweepLow,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        startAngle += sweepLow

        // Out of Stock (Coral/Red)
        val sweepOut = 360f * outOfStock
        drawArc(
            color = Color(0xFFD95338),
            startAngle = startAngle,
            sweepAngle = sweepOut,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
    }
}

@Composable
private fun InventoryLegendItem(color: Color, label: String, count: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = ForestGreenDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(85.dp)
        )
        Text(
            text = "$count items",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenDark
        )
    }
}

@Composable
private fun ExpiryAndFEFOContent(
    stats: ExpiryStats,
    items: List<InventoryItem>,
    todayDateDb: String,
    onNavigateToExpiryTracking: () -> Unit
) {
    // Usable FEFO items (non-expired, perishable items sorted by earliest expiry date)
    val fefoItems = remember(items, todayDateDb) {
        items.filter { item ->
            val expiry = item.expiryDate ?: ""
            if (!item.isPerishable || expiry.isBlank()) false
            else {
                val status = ExpiryTrackerService.calculateExpiryStatus(expiry, item.isPerishable, todayDateDb)
                status != ExpiryStatus.EXPIRED
            }
        }.sortedWith(compareBy<InventoryItem> { it.expiryDate ?: "" }.thenBy { it.id })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Expiry & FEFO Action",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenDark
                )
                Text(
                    text = "First Expired, First Out",
                    fontSize = 11.sp,
                    color = SageGreenText,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "View All",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SageGreenText,
                modifier = Modifier
                    .clickable { onNavigateToExpiryTracking() }
                    .padding(2.dp)
            )
        }

        // Summary Status Badges Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatusMiniPill("Safe (${stats.safeCount})", Color(0xFFE5F3EB), Color(0xFF286445))
            StatusMiniPill("Expiring Soon (${stats.expiringSoonCount})", AlertYellowLight, AlertYellow)
            StatusMiniPill("Urgent (${stats.attentionRequiredCount})", AlertRedLight, AlertRed)
            StatusMiniPill("Expired (${stats.expiredCount})", Color(0xFFF3F4F6), MutedText)
        }

        // FEFO Priority List
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (fefoItems.isNotEmpty()) {
                fefoItems.take(3).forEachIndexed { index, item ->
                    val priorityTag = when (index) {
                        0 -> "USE FIRST"
                        1 -> "USE NEXT"
                        else -> "NORMAL"
                    }
                    val badgeBg = when (index) {
                        0 -> AlertRedLight
                        1 -> AlertOrangeLight
                        else -> AlertYellowLight
                    }
                    val badgeColor = when (index) {
                        0 -> AlertRed
                        1 -> AlertOrange
                        else -> AlertYellow
                    }
                    ExpiryAlertRow(
                        name = "${item.ingredientName} (${item.quantity} ${item.unit})",
                        expiryDate = "Expires: ${item.expiryDate}",
                        badgeText = priorityTag,
                        badgeBg = badgeBg,
                        badgeColor = badgeColor,
                        emoji = when {
                            item.ingredientName.contains("Chicken", ignoreCase = true) -> "🍗"
                            item.ingredientName.contains("Milk", ignoreCase = true) -> "🥛"
                            item.ingredientName.contains("Paneer", ignoreCase = true) -> "🧀"
                            else -> "📦"
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active perishable stock needing immediate FEFO rotation",
                        fontSize = 12.sp,
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusMiniPill(label: String, bg: Color, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ExpiryAlertRow(
    name: String,
    expiryDate: String,
    badgeText: String,
    badgeBg: Color,
    badgeColor: Color,
    emoji: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeBg,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 15.sp)
                }
            }
            Column {
                Text(
                    text = name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenDark
                )
                Text(
                    text = expiryDate,
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = badgeBg
        ) {
            Text(
                text = badgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = badgeColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun AIDemandPredictionContent(
    predictions: List<FoodDemandPrediction>,
    onNavigateToAIPrediction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI Demand Prediction",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenDark
                )
                Text(
                    text = "Tomorrow's Forecast & Waste Risk",
                    fontSize = 11.sp,
                    color = SageGreenText,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "View All",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenMedium,
                modifier = Modifier
                    .clickable { onNavigateToAIPrediction() }
                    .padding(2.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (predictions.isNotEmpty()) {
                predictions.take(2).forEach { pred ->
                    AIPredictionRow(
                        name = pred.foodName,
                        demand = "${pred.predictedQuantity} portions",
                        percentage = "↑ ${pred.confidence.name.lowercase().replaceFirstChar { it.uppercase() }} Confidence",
                        emoji = if (pred.foodName.contains("Chicken", true)) "🍗" else "🥘",
                        bg = Color(0xFFFEF9E6)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AI demand predictions will generate based on sales history",
                        fontSize = 12.sp,
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AIPredictionRow(
    name: String,
    demand: String,
    percentage: String,
    emoji: String,
    bg: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = bg,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 18.sp)
                }
            }
            Column {
                Text(
                    text = name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenDark
                )
                Text(
                    text = "Predicted Demand (Tomorrow)",
                    fontSize = 11.sp,
                    color = MutedText
                )
                Text(
                    text = demand,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenDark
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE5F3EB)
        ) {
            Text(
                text = percentage,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF286445),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun PurchaseRecommendationsContent(
    recommendations: List<PurchaseRecommendation>,
    onNavigateToPurchaseRecommendations: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Smart Purchase Insights",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenDark
                )
                Text(
                    text = "AI Stock & Procurement Advise",
                    fontSize = 11.sp,
                    color = SageGreenText,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "View All",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenMedium,
                modifier = Modifier
                    .clickable { onNavigateToPurchaseRecommendations() }
                    .padding(2.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (recommendations.isNotEmpty()) {
                recommendations.take(2).forEach { rec ->
                    val statusText = when (rec.status) {
                        PurchaseStatus.PURCHASE_REQUIRED -> "BUY MORE"
                        PurchaseStatus.SUFFICIENT -> "SUFFICIENT"
                        PurchaseStatus.PURCHASED -> "PURCHASED"
                    }
                    val statusColor = when (rec.status) {
                        PurchaseStatus.PURCHASE_REQUIRED -> AlertRed
                        PurchaseStatus.SUFFICIENT -> Color(0xFF286445)
                        PurchaseStatus.PURCHASED -> ForestGreenDark
                    }
                    val statusBg = when (rec.status) {
                        PurchaseStatus.PURCHASE_REQUIRED -> AlertRedLight
                        PurchaseStatus.SUFFICIENT -> Color(0xFFE5F3EB)
                        PurchaseStatus.PURCHASED -> SageGreenActive
                    }
                    PurchaseRecRow(
                        name = rec.ingredientName,
                        recommendation = if (rec.isPurchaseRequired) "Shortage: ${rec.shortageQuantity} ${rec.unit}" else "Stock: ${rec.availableQuantity} ${rec.unit}",
                        status = statusText,
                        badgeBg = statusBg,
                        badgeColor = statusColor
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "All required ingredients are currently in sufficient stock",
                        fontSize = 12.sp,
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PurchaseRecRow(
    name: String,
    recommendation: String,
    status: String,
    badgeBg: Color,
    badgeColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenDark
            )
            Text(
                text = recommendation,
                fontSize = 11.sp,
                color = MutedText
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = badgeBg
        ) {
            Text(
                text = status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = badgeColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun AIAssistantContent(
    managerName: String,
    onNavigateToAIAssistant: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "AI Assistant",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenDark
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cute Robot Graphic
            CuteRobotFace(modifier = Modifier.size(54.dp))

            // Speech Bubble
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFAF3EC),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Hi $managerName! How can I assist with restaurant inventory & billing today?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = ForestGreenDark,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }
        }

        // Ask something button
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF537D53),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToAIAssistant() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ask something...",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CuteRobotFace(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val robotGreen = Color(0xFF4C7B53)
        val darkEye = Color(0xFF1B3B2B)
        val eyeGlow = Color(0xFFFFFFFF)

        // Head Base
        drawRoundRect(
            color = robotGreen,
            topLeft = Offset(w * 0.15f, h * 0.20f),
            size = Size(w * 0.70f, h * 0.60f),
            cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        )

        // Antennas / Ears
        drawRoundRect(
            color = robotGreen,
            topLeft = Offset(w * 0.05f, h * 0.35f),
            size = Size(w * 0.12f, h * 0.30f),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
        drawRoundRect(
            color = robotGreen,
            topLeft = Offset(w * 0.83f, h * 0.35f),
            size = Size(w * 0.12f, h * 0.30f),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )

        // Top Antenna
        drawLine(
            color = robotGreen,
            start = Offset(w * 0.5f, h * 0.20f),
            end = Offset(w * 0.5f, h * 0.08f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(
            color = robotGreen,
            radius = 3.5.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.08f)
        )

        // Left Eye
        drawCircle(
            color = darkEye,
            radius = 6.dp.toPx(),
            center = Offset(w * 0.35f, h * 0.48f)
        )
        drawCircle(
            color = eyeGlow,
            radius = 2.dp.toPx(),
            center = Offset(w * 0.33f, h * 0.46f)
        )

        // Right Eye
        drawCircle(
            color = darkEye,
            radius = 6.dp.toPx(),
            center = Offset(w * 0.65f, h * 0.48f)
        )
        drawCircle(
            color = eyeGlow,
            radius = 2.dp.toPx(),
            center = Offset(w * 0.63f, h * 0.46f)
        )

        // Smile
        val mouthPath = Path().apply {
            moveTo(w * 0.42f, h * 0.65f)
            quadraticTo(w * 0.50f, h * 0.72f, w * 0.58f, h * 0.65f)
        }
        drawPath(
            path = mouthPath,
            color = darkEye,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ForestGreenDark,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CozyRestaurantDiningGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Floor Shadow
        drawOval(
            color = Color(0x125A401A),
            topLeft = Offset(w * 0.15f, h * 0.85f),
            size = Size(w * 0.70f, h * 0.12f)
        )

        // Hanging Lamp Cord
        drawLine(
            color = Color(0xFFC49A58),
            start = Offset(w * 0.50f, 0f),
            end = Offset(w * 0.50f, h * 0.25f),
            strokeWidth = 1.5.dp.toPx()
        )

        // Pendant Lamp Shade
        val lampPath = Path().apply {
            moveTo(w * 0.44f, h * 0.25f)
            lineTo(w * 0.56f, h * 0.25f)
            lineTo(w * 0.62f, h * 0.38f)
            lineTo(w * 0.38f, h * 0.38f)
            close()
        }
        drawPath(path = lampPath, color = Color(0xFFC49A58))

        // Lamp Light Glow
        drawCircle(
            color = Color(0x2BD4AF37),
            radius = 12.dp.toPx(),
            center = Offset(w * 0.50f, h * 0.40f)
        )

        // Left Chair Backrest
        drawRoundRect(
            color = Color(0xFFE4BC7A),
            topLeft = Offset(w * 0.12f, h * 0.52f),
            size = Size(w * 0.22f, h * 0.24f),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
        // Left Chair Legs
        drawLine(
            color = Color(0xFF8D622D),
            start = Offset(w * 0.15f, h * 0.76f),
            end = Offset(w * 0.12f, h * 0.90f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF8D622D),
            start = Offset(w * 0.30f, h * 0.76f),
            end = Offset(w * 0.33f, h * 0.90f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Right Chair Backrest
        drawRoundRect(
            color = Color(0xFFE4BC7A),
            topLeft = Offset(w * 0.66f, h * 0.52f),
            size = Size(w * 0.22f, h * 0.24f),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
        // Right Chair Legs
        drawLine(
            color = Color(0xFF8D622D),
            start = Offset(w * 0.70f, h * 0.76f),
            end = Offset(w * 0.67f, h * 0.90f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF8D622D),
            start = Offset(w * 0.85f, h * 0.76f),
            end = Offset(w * 0.88f, h * 0.90f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Dining Table Center Top
        drawOval(
            color = Color(0xFFFAF6EE),
            topLeft = Offset(w * 0.26f, h * 0.62f),
            size = Size(w * 0.48f, h * 0.18f)
        )
        drawOval(
            color = Color(0xFFEBE3D3),
            topLeft = Offset(w * 0.26f, h * 0.62f),
            size = Size(w * 0.48f, h * 0.18f),
            style = Stroke(width = 1.dp.toPx())
        )

        // Table Base Pedestal
        drawLine(
            color = Color(0xFFB09062),
            start = Offset(w * 0.50f, h * 0.75f),
            end = Offset(w * 0.50f, h * 0.88f),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = Color(0xFFB09062),
            start = Offset(w * 0.42f, h * 0.88f),
            end = Offset(w * 0.58f, h * 0.88f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Small Plant on Table
        drawCircle(
            color = Color(0xFF386B4C),
            radius = 4.dp.toPx(),
            center = Offset(w * 0.50f, h * 0.65f)
        )
        drawCircle(
            color = Color(0xFF5C946E),
            radius = 2.5.dp.toPx(),
            center = Offset(w * 0.48f, h * 0.63f)
        )
    }
}

// =========================================================================
// ACTIONABLE NOTIFICATION CENTER DIALOG
// =========================================================================

enum class NotificationFilter(val label: String) {
    ALL("All Alerts"),
    CRITICAL("Critical Expiry"),
    EXPIRING_SOON("Expiring Soon"),
    LOW_STOCK("Low Stock"),
    WASTE_RISK("Waste Risk")
}

@Composable
fun ActionableNotificationCenterDialog(
    expiryStats: ExpiryStats,
    inventoryItems: List<InventoryItem>,
    wasteInsights: List<WasteInsight> = emptyList(),
    todayDateDb: String,
    tomorrowDateDb: String,
    onDismiss: () -> Unit,
    onNavigateToExpiryTracking: () -> Unit,
    onNavigateToPurchaseRecommendations: () -> Unit,
    onNavigateToWasteManagement: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onNavigateToInventory: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(NotificationFilter.ALL) }

    // Critical Expiry (Expired or Expiring Today)
    val criticalExpiryItems = remember(inventoryItems, todayDateDb) {
        inventoryItems.filter { item ->
            val status = ExpiryTrackerService.calculateExpiryStatus(item.expiryDate, item.isPerishable, todayDateDb)
            status == ExpiryStatus.EXPIRED || status == ExpiryStatus.EXPIRING_TODAY
        }
    }

    // Expiring Soon (1-7 days)
    val expiringSoonItems = remember(inventoryItems, todayDateDb) {
        inventoryItems.filter { item ->
            val status = ExpiryTrackerService.calculateExpiryStatus(item.expiryDate, item.isPerishable, todayDateDb)
            status == ExpiryStatus.EXPIRING_SOON
        }
    }

    // Low / Out of Stock
    val stockAlertItems = remember(inventoryItems) {
        inventoryItems.filter { it.isLowStock || it.isOutOfStock }
    }

    // Real Waste Risk Insights from WasteInsightEngine
    val wasteRiskInsights = remember(wasteInsights) {
        wasteInsights
    }

    val totalAlerts = criticalExpiryItems.size + expiringSoonItems.size + stockAlertItems.size + wasteRiskInsights.size

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notification_center_dialog"),
        shape = RoundedCornerShape(20.dp),
        containerColor = PageBackground,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AlertOrange.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = "Alerts",
                                tint = AlertOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Notification Center",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )
                        Text(
                            text = "$totalAlerts actionable alerts require attention",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NotificationFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val count = when (filter) {
                            NotificationFilter.ALL -> totalAlerts
                            NotificationFilter.CRITICAL -> criticalExpiryItems.size
                            NotificationFilter.EXPIRING_SOON -> expiringSoonItems.size
                            NotificationFilter.LOW_STOCK -> stockAlertItems.size
                            NotificationFilter.WASTE_RISK -> wasteRiskInsights.size
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text("${filter.label} ($count)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreenDark,
                                selectedLabelColor = Color.White,
                                containerColor = CardWhite,
                                labelColor = ForestGreenDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = BorderSubtle,
                                selectedBorderColor = ForestGreenDark
                            )
                        )
                    }
                }

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                // Alert List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Critical Expiry Items
                    if (selectedFilter == NotificationFilter.ALL || selectedFilter == NotificationFilter.CRITICAL) {
                        items(criticalExpiryItems, key = { "crit_${it.id}" }) { item ->
                            val status = ExpiryTrackerService.calculateExpiryStatus(item.expiryDate, item.isPerishable, todayDateDb)
                            val isExpired = status == ExpiryStatus.EXPIRED
                            NotificationAlertCard(
                                title = item.ingredientName,
                                subtitle = if (isExpired) "Expired on ${item.expiryDate}" else "Expires Today (${item.expiryDate})",
                                quantityText = "${item.quantity} ${item.unit} at risk",
                                badgeText = if (isExpired) "EXPIRED" else "CRITICAL FEFO",
                                badgeColor = AlertRed,
                                primaryActionText = "Mark Waste",
                                primaryActionIcon = Icons.Filled.Delete,
                                onPrimaryAction = onNavigateToWasteManagement,
                                secondaryActionText = "FEFO View",
                                onSecondaryAction = onNavigateToExpiryTracking
                            )
                        }
                    }

                    // Expiring Soon Items
                    if (selectedFilter == NotificationFilter.ALL || selectedFilter == NotificationFilter.EXPIRING_SOON) {
                        items(expiringSoonItems, key = { "exp_${it.id}" }) { item ->
                            NotificationAlertCard(
                                title = item.ingredientName,
                                subtitle = "Expires on ${item.expiryDate} (Batch tracking)",
                                quantityText = "${item.quantity} ${item.unit} in stock",
                                badgeText = "EXPIRING SOON",
                                badgeColor = AlertOrange,
                                primaryActionText = "FEFO Priority",
                                primaryActionIcon = Icons.Filled.HourglassBottom,
                                onPrimaryAction = onNavigateToExpiryTracking,
                                secondaryActionText = null,
                                onSecondaryAction = null
                            )
                        }
                    }

                    // Low Stock Items
                    if (selectedFilter == NotificationFilter.ALL || selectedFilter == NotificationFilter.LOW_STOCK) {
                        items(stockAlertItems, key = { "stock_${it.id}" }) { item ->
                            NotificationAlertCard(
                                title = item.ingredientName,
                                subtitle = "Current: ${item.quantity} ${item.unit} • Minimum: ${item.minimumStockLevel} ${item.unit}",
                                quantityText = if (item.isOutOfStock) "Out of Stock" else "Low Stock",
                                badgeText = if (item.isOutOfStock) "OUT OF STOCK" else "REORDER",
                                badgeColor = if (item.isOutOfStock) AlertRed else AlertOrange,
                                primaryActionText = "Order Now",
                                primaryActionIcon = Icons.Filled.ShoppingCart,
                                onPrimaryAction = onNavigateToPurchaseRecommendations,
                                secondaryActionText = "Suppliers",
                                onSecondaryAction = onNavigateToSuppliers
                            )
                        }
                    }

                    // Real Waste Risk Insights
                    if (selectedFilter == NotificationFilter.ALL || selectedFilter == NotificationFilter.WASTE_RISK) {
                        items(wasteRiskInsights, key = { "waste_${it.id}" }) { insight ->
                            val badgeColor = when (insight.severity.uppercase()) {
                                "CRITICAL", "HIGH" -> AlertRed
                                "MEDIUM", "WARNING" -> AlertOrange
                                "LOW" -> AlertYellow
                                else -> Color(0xFF286445)
                            }
                            NotificationAlertCard(
                                title = insight.title,
                                subtitle = insight.description,
                                quantityText = insight.actionSuggestion.ifBlank { "Waste Risk Alert" },
                                badgeText = insight.severity.ifBlank { "WASTE RISK" },
                                badgeColor = badgeColor,
                                primaryActionText = "Manage Waste",
                                primaryActionIcon = Icons.Filled.Delete,
                                onPrimaryAction = onNavigateToWasteManagement,
                                secondaryActionText = null,
                                onSecondaryAction = null
                            )
                        }
                    }

                    if (totalAlerts == 0) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "All Clear",
                                        tint = ForestGreenMedium,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "All systems clear! No critical alerts.",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ForestGreenDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenDark),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("notification_center_done_button")
            ) {
                Text("Done", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onNavigateToExpiryTracking,
                colors = ButtonDefaults.textButtonColors(contentColor = ForestGreenDark)
            ) {
                Text("View All Expiry", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
private fun NotificationAlertCard(
    title: String,
    subtitle: String,
    quantityText: String,
    badgeText: String,
    badgeColor: Color,
    primaryActionText: String,
    primaryActionIcon: ImageVector,
    onPrimaryAction: () -> Unit,
    secondaryActionText: String?,
    onSecondaryAction: (() -> Unit)?
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenDark
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MutedText
                    )
                    Text(
                        text = quantityText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ForestGreenMedium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (secondaryActionText != null && onSecondaryAction != null) {
                    TextButton(
                        onClick = onSecondaryAction,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = secondaryActionText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SageGreenText
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Button(
                    onClick = onPrimaryAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (badgeColor == AlertRed) AlertRed else ForestGreenDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = primaryActionIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = primaryActionText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// =========================================================================
// DASHBOARD MODULE CARDS SECTION (8 CORE RESTAURANT MODULES)
// =========================================================================
@Composable
private fun DashboardModuleCardsSection(
    onNavigateToMenu: () -> Unit,
    onNavigateToBilling: () -> Unit,
    onNavigateToBillHistory: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToExpiryTracking: () -> Unit,
    onNavigateToSalesReports: () -> Unit,
    onNavigateToAIPrediction: () -> Unit,
    onNavigateToAIAssistant: () -> Unit,
    isWideScreen: Boolean
) {
    val modules = remember {
        listOf(
            DashboardModuleItem("Menu", "Dishes & Products", Icons.AutoMirrored.Filled.MenuBook, Color(0xFF1E523A), Color(0xFFE8F3EE), "dashboard_card_menu", onNavigateToMenu),
            DashboardModuleItem("Billing / POS", "New Bill & Payments", Icons.Filled.PointOfSale, Color(0xFF286445), Color(0xFFE5F3EB), "dashboard_card_billing", onNavigateToBilling),
            DashboardModuleItem("Orders", "Order History & Status", Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFFD96B27), Color(0xFFFDF0E6), "dashboard_card_orders", onNavigateToBillHistory),
            DashboardModuleItem("Inventory", "Stock & Ingredients", Icons.Filled.Inventory2, Color(0xFFD98A1E), Color(0xFFFEF7E6), "dashboard_card_inventory", onNavigateToInventory),
            DashboardModuleItem("Expiry / Waste", "Track & Reduce Waste", Icons.Filled.AccessTime, Color(0xFF2E7D52), Color(0xFFE7F4EE), "dashboard_card_expiry_waste", onNavigateToExpiryTracking),
            DashboardModuleItem("Reports", "Sales & Analytics", Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF1B5E20), Color(0xFFE8F5E9), "dashboard_card_reports", onNavigateToSalesReports),
            DashboardModuleItem("AI Forecast", "Demand Prediction", Icons.Filled.Analytics, Color(0xFF1565C0), Color(0xFFE3F2FD), "dashboard_card_ai_forecast", onNavigateToAIPrediction),
            DashboardModuleItem("AI Chat", "Get Smart Suggestions", Icons.Filled.SmartToy, Color(0xFF6A1B9A), Color(0xFFF3E5F5), "dashboard_card_ai_chat", onNavigateToAIAssistant)
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Restaurant Management Modules",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenDark
        )

        val columns = if (isWideScreen) 4 else 2
        val rows = modules.chunked(columns)

        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    DashboardModuleCard(
                        item = item,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private data class DashboardModuleItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBgColor: Color,
    val testTag: String,
    val onClick: () -> Unit
)

@Composable
private fun DashboardModuleCard(
    item: DashboardModuleItem,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, BorderSubtle),
        shadowElevation = 2.dp,
        modifier = modifier
            .testTag(item.testTag)
            .clickable { item.onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(item.iconBgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.subtitle,
                    fontSize = 10.sp,
                    color = MutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MutedText.copy(alpha = 0.5f),
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

@Composable
private fun ShortcutPill(
    icon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ForestGreenDark,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreenDark
            )
        }
    }
}

