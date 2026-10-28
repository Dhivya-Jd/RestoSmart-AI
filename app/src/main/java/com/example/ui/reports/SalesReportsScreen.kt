package com.example.ui.reports

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.SmartRestaurantApp
import com.example.model.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// =========================================================================
// THEME COLOR PALETTE (Food Wise Identity)
// =========================================================================
private val PageBackground = Color(0xFFFBF9F4)
private val ForestGreenDark = Color(0xFF1B3B2B)
private val ForestGreenMedium = Color(0xFF2D5A3D)
private val SageGreen = Color(0xFF4A7C59)
private val CardWhite = Color(0xFFFFFFFF)
private val CardBackgroundSubtle = Color(0xFFF4F0E8)
private val BorderSubtle = Color(0xFFE5E0D8)
private val MutedText = Color(0xFF7A8B7B)
private val SubtleGold = Color(0xFFD4A373)
private val AlertOrange = Color(0xFFE76F51)
private val SuccessGreen = Color(0xFF2A9D8F)
private val DangerRed = Color(0xFFD32F2F)

enum class ReportTab(val label: String, val icon: ImageVector) {
    SALES("Sales", Icons.Filled.MonetizationOn),
    INVENTORY("Inventory", Icons.Filled.Inventory2),
    EXPIRY("Expiry & FEFO", Icons.Filled.HourglassBottom),
    WASTE("Food Waste", Icons.Filled.DeleteOutline)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesReportsScreen(
    viewModel: SalesReportsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val container = remember(context) { (context.applicationContext as SmartRestaurantApp).container }

    val activeRestaurant by viewModel.activeRestaurant.collectAsStateWithLifecycle()
    val reportDataNullable by viewModel.reportData.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val restaurantId = activeRestaurant?.id ?: 0L
    val restaurantName = activeRestaurant?.restaurantName?.takeIf { it.isNotBlank() } ?: ""

    val todayDateDb = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // Fallback or Active Sales Report Data
    val reportData = reportDataNullable ?: SalesReportData(
        period = ReportPeriod.THIS_WEEK,
        startDateFormatted = "",
        endDateFormatted = "",
        startTimestamp = 0L,
        endTimestamp = 0L,
        totalRevenue = 0.0,
        totalOrders = 0,
        totalItemsSold = 0,
        averageOrderValue = 0.0,
        topSellingFood = null,
        leastSellingFood = null,
        bestSalesDay = null,
        foodSales = emptyList(),
        categorySales = emptyList(),
        dailyTrend = emptyList()
    )

    // Inventory Data
    val inventoryStats by remember(restaurantId) {
        if (restaurantId > 0) container.inventoryRepository.getInventoryStats(restaurantId)
        else kotlinx.coroutines.flow.flowOf(InventoryStats(totalItems = 0, sufficientCount = 0, lowStockCount = 0, outOfStockCount = 0))
    }.collectAsStateWithLifecycle(initialValue = InventoryStats(totalItems = 0, sufficientCount = 0, lowStockCount = 0, outOfStockCount = 0))

    val inventoryItems by remember(restaurantId) {
        if (restaurantId > 0) container.inventoryRepository.getInventoryItems(restaurantId)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    // Expiry Data
    val expiryStats by remember(restaurantId, todayDateDb) {
        if (restaurantId > 0) container.inventoryRepository.getExpiryStats(restaurantId, todayDateDb)
        else kotlinx.coroutines.flow.flowOf(ExpiryStats(totalTracked = 0, safeCount = 0, expiringSoonCount = 0, expiringTodayCount = 0, expiredCount = 0, noExpiryCount = 0))
    }.collectAsStateWithLifecycle(initialValue = ExpiryStats(totalTracked = 0, safeCount = 0, expiringSoonCount = 0, expiringTodayCount = 0, expiredCount = 0, noExpiryCount = 0))

    val perishableItems by remember(restaurantId) {
        if (restaurantId > 0) container.inventoryRepository.getPerishableItems(restaurantId)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    // Waste Data
    val wasteSummary by remember(restaurantId, todayDateDb) {
        if (restaurantId > 0) container.wasteRepository.getWasteSummary(restaurantId, todayDateDb)
        else kotlinx.coroutines.flow.flowOf(WasteSummary(todayWasteKg = 0.0, last7DaysWasteKg = 0.0, thisMonthWasteKg = 0.0, totalRecordsCount = 0, totalQuantityKgEquivalent = 0.0, degradableQuantityKg = 0.0, nonDegradableQuantityKg = 0.0))
    }.collectAsStateWithLifecycle(initialValue = WasteSummary(todayWasteKg = 0.0, last7DaysWasteKg = 0.0, thisMonthWasteKg = 0.0, totalRecordsCount = 0, totalQuantityKgEquivalent = 0.0, degradableQuantityKg = 0.0, nonDegradableQuantityKg = 0.0))

    val foodWiseWaste by remember(restaurantId) {
        if (restaurantId > 0) container.wasteRepository.getFoodWiseWaste(restaurantId)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val reasonWiseWaste by remember(restaurantId) {
        if (restaurantId > 0) container.wasteRepository.getReasonWiseWaste(restaurantId)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val wasteInsights by remember(restaurantId) {
        if (restaurantId > 0) container.wasteRepository.getWasteInsights(restaurantId)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedTab by remember { mutableStateOf(ReportTab.SALES) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Analytics & Reports",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark,
                            fontFamily = FontFamily.Serif
                        )
                        if (restaurantName.isNotBlank()) {
                            Text(
                                text = restaurantName,
                                fontSize = 12.sp,
                                color = SageGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("reports_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ForestGreenDark
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToHome,
                        modifier = Modifier.testTag("reports_home_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home",
                            tint = ForestGreenDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PageBackground,
                    titleContentColor = ForestGreenDark
                )
            )
        },
        containerColor = PageBackground,
        modifier = Modifier.testTag("reports_screen_root")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector Row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ReportTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        val bgAnim by animateColorAsState(
                            targetValue = if (isSelected) ForestGreenDark else Color.Transparent,
                            label = "tab_bg"
                        )
                        val textAnim by animateColorAsState(
                            targetValue = if (isSelected) Color.White else ForestGreenDark,
                            label = "tab_text"
                        )

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable { selectedTab = tab }
                                .testTag("report_tab_${tab.name.lowercase(Locale.ROOT)}"),
                            shape = RoundedCornerShape(12.dp),
                            color = bgAnim
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    tint = textAnim,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = textAnim,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Tab Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    ReportTab.SALES -> SalesTabContent(
                        reportData = reportData,
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    ReportTab.INVENTORY -> InventoryTabContent(
                        inventoryStats = inventoryStats,
                        inventoryItems = inventoryItems
                    )
                    ReportTab.EXPIRY -> ExpiryTabContent(
                        expiryStats = expiryStats,
                        perishableItems = perishableItems,
                        todayDateDb = todayDateDb
                    )
                    ReportTab.WASTE -> WasteTabContent(
                        wasteSummary = wasteSummary,
                        foodWiseWaste = foodWiseWaste,
                        reasonWiseWaste = reasonWiseWaste,
                        wasteInsights = wasteInsights
                    )
                }
            }
        }
    }
}

// =========================================================================
// 1. SALES TAB CONTENT
// =========================================================================
@Composable
private fun SalesTabContent(
    reportData: SalesReportData,
    uiState: SalesReportsUiState,
    viewModel: SalesReportsViewModel
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val periods: List<Pair<ReportPeriod, String>> = listOf(
        ReportPeriod.TODAY to "Daily",
        ReportPeriod.THIS_WEEK to "Weekly",
        ReportPeriod.THIS_MONTH to "Monthly",
        ReportPeriod.CUSTOM to "Yearly"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Period Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEach { (period, label) ->
                    val isSelected = reportData.period == period
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (period == ReportPeriod.CUSTOM) {
                                val cal = Calendar.getInstance()
                                val year = cal.get(Calendar.YEAR)
                                cal.set(year, Calendar.JANUARY, 1, 0, 0, 0)
                                cal.set(Calendar.MILLISECOND, 0)
                                val startOfYear = cal.timeInMillis
                                cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59)
                                cal.set(Calendar.MILLISECOND, 999)
                                val endOfYear = cal.timeInMillis
                                viewModel.applyCustomDateRange(startOfYear, endOfYear)
                            } else {
                                viewModel.setPeriod(period)
                            }
                        },
                        label = { Text(label, fontSize = 12.sp) },
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
                        ),
                        modifier = Modifier.testTag("period_filter_${label.lowercase(Locale.ROOT).replace(" ", "_")}")
                    )
                }

                // Custom Range Button
                AssistChip(
                    onClick = { viewModel.openCustomDateDialog() },
                    label = { Text("Custom Range", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Date Range",
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = CardWhite,
                        labelColor = ForestGreenDark,
                        leadingIconContentColor = ForestGreenDark
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = BorderSubtle
                    )
                )
            }
        }

        // Top KPI Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportKpiCard(
                        title = "Total Revenue",
                        value = currencyFormat.format(reportData.totalRevenue),
                        subtitle = "${reportData.period.name.replace("_", " ")}",
                        icon = Icons.Filled.CurrencyRupee,
                        tintColor = ForestGreenDark,
                        modifier = Modifier.weight(1f)
                    )
                    ReportKpiCard(
                        title = "Total Orders",
                        value = "${reportData.totalOrders}",
                        subtitle = "Billed Tickets",
                        icon = Icons.Filled.ReceiptLong,
                        tintColor = SageGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportKpiCard(
                        title = "Items Sold",
                        value = "${reportData.totalItemsSold}",
                        subtitle = "Portions served",
                        icon = Icons.Filled.Restaurant,
                        tintColor = SubtleGold,
                        modifier = Modifier.weight(1f)
                    )
                    ReportKpiCard(
                        title = "Avg Order Value",
                        value = currencyFormat.format(reportData.averageOrderValue),
                        subtitle = "Per ticket",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        tintColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Daily Revenue Trend Canvas Chart (Always rendered for all periods)
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sales Revenue Trend",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )
                        Text(
                            text = if (reportData.dailyTrend.isNotEmpty()) "${reportData.dailyTrend.size} Data Points" else "0 Data Points",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val points = reportData.dailyTrend
                    val maxRevenue = (points.maxOfOrNull { it.revenue } ?: 1000.0).coerceAtLeast(100.0)

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        val w = size.width
                        val h = size.height

                        // Horizontal background grid lines
                        val gridLines = 4
                        for (g in 0..gridLines) {
                            val gy = (h / gridLines) * g
                            drawLine(
                                color = BorderSubtle,
                                start = Offset(0f, gy),
                                end = Offset(w, gy),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        if (points.isEmpty()) {
                            // Baseline at bottom for 0 sales
                            drawLine(
                                color = ForestGreenMedium.copy(alpha = 0.4f),
                                start = Offset(0f, h - 10f),
                                end = Offset(w, h - 10f),
                                strokeWidth = 2.dp.toPx()
                            )
                        } else if (points.size == 1) {
                            // SINGLE DATA POINT (Daily / Today Sales)
                            val pt = points[0]
                            val normValue = (pt.revenue / maxRevenue).toFloat().coerceIn(0.15f, 0.85f)
                            val y = h - (normValue * h)
                            val centerX = w / 2f

                            // Dashed horizontal line across chart width
                            drawLine(
                                color = ForestGreenMedium.copy(alpha = 0.6f),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                            )

                            // Shaded area under the line
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        ForestGreenMedium.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                topLeft = Offset(0f, y),
                                size = Size(w, h - y)
                            )

                            // Center Glowing Node
                            drawCircle(
                                color = ForestGreenDark,
                                radius = 7.dp.toPx(),
                                center = Offset(centerX, y)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.dp.toPx(),
                                center = Offset(centerX, y)
                            )
                        } else {
                            // MULTI DATA POINTS (Weekly, Monthly, Yearly)
                            val spacing = w / (points.size - 1)
                            val path = Path()
                            val fillPath = Path()

                            val pixelPoints = points.mapIndexed { i, pt ->
                                val x = i * spacing
                                val normValue = (pt.revenue / maxRevenue).toFloat().coerceIn(0.05f, 0.95f)
                                val y = (h - (normValue * (h * 0.85f))).toFloat()
                                Offset(x, y)
                            }

                            path.moveTo(pixelPoints[0].x, pixelPoints[0].y)
                            fillPath.moveTo(pixelPoints[0].x, h)
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

                            fillPath.lineTo(pixelPoints.last().x, h)
                            fillPath.close()

                            // Fill
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        ForestGreenMedium.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )

                            // Line
                            drawPath(
                                path = path,
                                color = ForestGreenMedium,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Dots
                            pixelPoints.forEach { pt ->
                                drawCircle(
                                    color = ForestGreenDark,
                                    radius = 4.5.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // X Axis Labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (points.size <= 1) Arrangement.Center else Arrangement.SpaceBetween
                    ) {
                        if (points.isEmpty()) {
                            Text(
                                text = "No sales recorded yet for this period",
                                fontSize = 11.sp,
                                color = MutedText,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            points.forEach { pt ->
                                Text(
                                    text = pt.dayLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ForestGreenDark,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top Selling Dishes
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Top-Selling Dishes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (reportData.foodSales.isEmpty()) {
                        Text(
                            text = "No dish sales recorded for this period.",
                            fontSize = 12.sp,
                            color = MutedText,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        reportData.foodSales.take(5).forEachIndexed { index, food ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (index == 0) SubtleGold.copy(alpha = 0.2f) else CardBackgroundSubtle,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "#${index + 1}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (index == 0) ForestGreenDark else MutedText
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = food.foodName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ForestGreenDark
                                        )
                                        Text(
                                            text = "${food.quantitySold} sold • ${food.category}",
                                            fontSize = 11.sp,
                                            color = MutedText
                                        )
                                    }
                                }
                                Text(
                                    text = currencyFormat.format(food.totalRevenue),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                            }
                            if (index < 4 && index < reportData.foodSales.size - 1) {
                                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 2. INVENTORY TAB CONTENT
// =========================================================================
@Composable
private fun InventoryTabContent(
    inventoryStats: InventoryStats,
    inventoryItems: List<InventoryItem>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredItems = remember(inventoryItems, searchQuery, selectedFilter) {
        inventoryItems.filter { item ->
            val matchesSearch = item.ingredientName.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "Sufficient" -> !item.isLowStock && !item.isOutOfStock
                "Low Stock" -> item.isLowStock
                "Out of Stock" -> item.isOutOfStock
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Summary Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportKpiCard(
                        title = "Total Inventory",
                        value = "${inventoryStats.totalItems}",
                        subtitle = "Tracked ingredients",
                        icon = Icons.Filled.Inventory2,
                        tintColor = ForestGreenDark,
                        modifier = Modifier.weight(1f)
                    )
                    ReportKpiCard(
                        title = "Sufficient Stock",
                        value = "${inventoryStats.sufficientCount}",
                        subtitle = "Optimal level",
                        icon = Icons.Filled.CheckCircle,
                        tintColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportKpiCard(
                        title = "Low Stock Alert",
                        value = "${inventoryStats.lowStockCount}",
                        subtitle = "Below reorder level",
                        icon = Icons.Filled.Warning,
                        tintColor = AlertOrange,
                        modifier = Modifier.weight(1f)
                    )
                    ReportKpiCard(
                        title = "Out of Stock",
                        value = "${inventoryStats.outOfStockCount}",
                        subtitle = "Immediate purchase needed",
                        icon = Icons.Filled.Cancel,
                        tintColor = DangerRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Inventory Stock Bar
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Stock Health Ratio",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val total = inventoryStats.totalItems.coerceAtLeast(1)
                    val sufficientRatio = inventoryStats.sufficientCount.toFloat() / total
                    val lowRatio = inventoryStats.lowStockCount.toFloat() / total
                    val outRatio = inventoryStats.outOfStockCount.toFloat() / total

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        if (sufficientRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(sufficientRatio)
                                    .fillMaxHeight()
                                    .background(SuccessGreen)
                            )
                        }
                        if (lowRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(lowRatio)
                                    .fillMaxHeight()
                                    .background(AlertOrange)
                            )
                        }
                        if (outRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(outRatio)
                                    .fillMaxHeight()
                                    .background(DangerRed)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LegendItem("Sufficient (${inventoryStats.sufficientCount})", SuccessGreen)
                        LegendItem("Low (${inventoryStats.lowStockCount})", AlertOrange)
                        LegendItem("Out (${inventoryStats.outOfStockCount})", DangerRed)
                    }
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Sufficient", "Low Stock", "Out of Stock").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
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
        }

        // Inventory Items List
        items(filteredItems, key = { it.id }) { item ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.ingredientName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Category: ${item.category} • Min: ${item.minimumStockLevel} ${item.unit}",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${item.quantity} ${item.unit}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                item.isOutOfStock -> DangerRed
                                item.isLowStock -> AlertOrange
                                else -> ForestGreenDark
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusBadge(
                            label = when {
                                item.isOutOfStock -> "OUT OF STOCK"
                                item.isLowStock -> "LOW STOCK"
                                else -> "SUFFICIENT"
                            },
                            color = when {
                                item.isOutOfStock -> DangerRed
                                item.isLowStock -> AlertOrange
                                else -> SuccessGreen
                            }
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 3. EXPIRY & FEFO TAB CONTENT
// =========================================================================
@Composable
private fun ExpiryTabContent(
    expiryStats: ExpiryStats,
    perishableItems: List<InventoryItem>,
    todayDateDb: String
) {
    // Sort perishable items according to FEFO (First-Expired, First-Out)
    val sortedFefoItems = remember(perishableItems, todayDateDb) {
        perishableItems
            .filter { !it.expiryDate.isNullOrBlank() }
            .sortedBy { it.expiryDate ?: "" }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Expiry KPI Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportKpiCard(
                        title = "Safe Tracked",
                        value = "${expiryStats.safeCount}",
                        subtitle = "Fresh & verified",
                        icon = Icons.Filled.Verified,
                        tintColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    ReportKpiCard(
                        title = "Expiring Soon",
                        value = "${expiryStats.expiringSoonCount}",
                        subtitle = "Within 3-7 days",
                        icon = Icons.Filled.HourglassTop,
                        tintColor = AlertOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportKpiCard(
                        title = "Expires Today",
                        value = "${expiryStats.expiringTodayCount}",
                        subtitle = "Priority prep",
                        icon = Icons.Filled.AccessTime,
                        tintColor = DangerRed,
                        modifier = Modifier.weight(1f)
                    )
                    ReportKpiCard(
                        title = "Expired Items",
                        value = "${expiryStats.expiredCount}",
                        subtitle = "Mark for waste",
                        icon = Icons.Filled.WarningAmber,
                        tintColor = DangerRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // FEFO Section Header
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardBackgroundSubtle,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LowPriority,
                        contentDescription = "FEFO",
                        tint = ForestGreenDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "FEFO Kitchen Priority Queue",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )
                        Text(
                            text = "First-Expired, First-Out rule enforced for chef preparation.",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }
            }
        }

        // FEFO List
        if (sortedFefoItems.isEmpty()) {
            item {
                Text(
                    text = "No active expiry tracked items recorded.",
                    fontSize = 13.sp,
                    color = MutedText,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            itemsIndexed(sortedFefoItems, key = { _, item -> item.id }) { index, item ->
                val expiry = item.expiryDate ?: ""
                val isExpired = expiry.isNotBlank() && expiry < todayDateDb
                val isToday = expiry == todayDateDb

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    border = BorderStroke(
                        1.dp,
                        if (isExpired || isToday) AlertOrange.copy(alpha = 0.5f) else BorderSubtle
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isExpired || isToday) DangerRed.copy(alpha = 0.15f) else CardBackgroundSubtle,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isExpired || isToday) DangerRed else ForestGreenDark
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = item.ingredientName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                                Text(
                                    text = "Batch Qty: ${item.quantity} ${item.unit} • ${item.category}",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Exp: ${item.expiryDate ?: "N/A"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isExpired || isToday) DangerRed else ForestGreenDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusBadge(
                                label = when {
                                    isExpired -> "EXPIRED"
                                    isToday -> "TODAY"
                                    else -> "USE FIRST"
                                },
                                color = when {
                                    isExpired || isToday -> DangerRed
                                    else -> SageGreen
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 4. WASTE TAB CONTENT
// =========================================================================
@Composable
private fun WasteTabContent(
    wasteSummary: WasteSummary,
    foodWiseWaste: List<FoodWiseWaste>,
    reasonWiseWaste: List<ReasonWiseWaste>,
    wasteInsights: List<WasteInsight>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Summary KPIs
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportKpiCard(
                        title = "Today's Waste",
                        value = "${String.format(Locale.getDefault(), "%.2f", wasteSummary.todayWasteKg)} kg",
                        subtitle = "Daily log",
                        icon = Icons.Filled.Delete,
                        tintColor = DangerRed,
                        modifier = Modifier.weight(1f)
                    )
                    ReportKpiCard(
                        title = "Last 7 Days",
                        value = "${String.format(Locale.getDefault(), "%.2f", wasteSummary.last7DaysWasteKg)} kg",
                        subtitle = "Weekly total",
                        icon = Icons.Filled.CalendarViewWeek,
                        tintColor = AlertOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportKpiCard(
                        title = "This Month",
                        value = "${String.format(Locale.getDefault(), "%.2f", wasteSummary.thisMonthWasteKg)} kg",
                        subtitle = "Monthly footprint",
                        icon = Icons.Filled.Eco,
                        tintColor = SageGreen,
                        modifier = Modifier.weight(1f)
                    )
                    ReportKpiCard(
                        title = "Total Logged",
                        value = "${wasteSummary.totalRecordsCount} items",
                        subtitle = "${String.format(Locale.getDefault(), "%.1f", wasteSummary.totalQuantityKgEquivalent)} kg total",
                        icon = Icons.Filled.FormatListNumbered,
                        tintColor = ForestGreenDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Degradable vs Non-Degradable Bar
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Waste Classification Breakdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val degPct = wasteSummary.degradablePercentage.toFloat().coerceIn(0f, 100f)
                    val nonDegPct = wasteSummary.nonDegradablePercentage.toFloat().coerceIn(0f, 100f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        if (degPct > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(degPct.coerceAtLeast(1f))
                                    .fillMaxHeight()
                                    .background(SuccessGreen)
                            )
                        }
                        if (nonDegPct > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(nonDegPct.coerceAtLeast(1f))
                                    .fillMaxHeight()
                                    .background(AlertOrange)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LegendItem("Degradable Organic (${String.format(Locale.getDefault(), "%.1f", degPct)}%)", SuccessGreen)
                        LegendItem("Non-Degradable (${String.format(Locale.getDefault(), "%.1f", nonDegPct)}%)", AlertOrange)
                    }
                }
            }
        }

        // Reasons Breakdown
        if (reasonWiseWaste.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardWhite,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Waste Root Causes",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        reasonWiseWaste.forEach { reasonItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = reasonItem.reason.name.replace("_", " "),
                                    fontSize = 13.sp,
                                    color = ForestGreenDark,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${String.format(Locale.getDefault(), "%.2f", reasonItem.totalQuantityKg)} kg (${String.format(Locale.getDefault(), "%.1f", reasonItem.percentage)}%)",
                                    fontSize = 12.sp,
                                    color = MutedText,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                        }
                    }
                }
            }
        }

        // Top High Risk Food Items
        if (foodWiseWaste.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardWhite,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "High Waste Ingredients",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        foodWiseWaste.take(5).forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.itemName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ForestGreenDark
                                    )
                                    Text(
                                        text = "Top Reason: ${item.topReason.name.replace("_", " ")}",
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )
                                }
                                Text(
                                    text = "${item.totalQuantity} ${item.unit}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DangerRed
                                )
                            }
                            if (index < 4 && index < foodWiseWaste.size - 1) {
                                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }

        // AI Waste Insights
        if (wasteInsights.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "AI Waste Reduction Recommendations",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenDark
                    )

                    wasteInsights.forEach { insight ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardBackgroundSubtle,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Lightbulb,
                                        contentDescription = "Insight",
                                        tint = SubtleGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = insight.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreenDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = insight.description,
                                    fontSize = 12.sp,
                                    color = ForestGreenMedium
                                )
                                if (insight.actionSuggestion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Action: ${insight.actionSuggestion}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SageGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// REUSABLE COMPONENTS
// =========================================================================
@Composable
private fun ReportKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
                Surface(
                    shape = CircleShape,
                    color = tintColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = tintColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenDark
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MutedText
            )
        }
    }
}

@Composable
private fun StatusBadge(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MutedText
        )
    }
}
