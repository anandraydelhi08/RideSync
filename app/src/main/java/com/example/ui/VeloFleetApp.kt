package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.BookingEntity
import com.example.data.model.DutyStatus
import com.example.ui.components.RoleSwitcherBar
import com.example.ui.screens.billing.BillingScreen
import com.example.ui.screens.booking.AssignDriverDialog
import com.example.ui.screens.booking.BookingDetailDialog
import com.example.ui.screens.booking.BookingListScreen
import com.example.ui.screens.booking.CreateBookingDialog
import com.example.ui.screens.corporate.CorporatePortalScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dispatch.DispatchBoardScreen
import com.example.ui.screens.driver.DriverDutyConsoleScreen
import com.example.ui.screens.driver.DriversMasterScreen
import com.example.ui.screens.fleet.FleetVehiclesScreen
import com.example.ui.screens.fleet.LiveTrackingMapScreen
import com.example.ui.screens.reports.ReportsScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.FleetViewModel
import com.example.ui.viewmodel.ScreenTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeloFleetApp(viewModel: FleetViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val drivers by viewModel.drivers.collectAsStateWithLifecycle()
    val vehicles by viewModel.vehicles.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Toast message observer
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = FleetNavy900,
                drawerContentColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Brand Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FleetBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VELOFLEET OPS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Car Rental & Cab Operations",
                                fontSize = 11.sp,
                                color = FleetCyanAccent
                            )
                        }
                    }
                }

                HorizontalDivider(color = FleetNavy700)
                Spacer(modifier = Modifier.height(8.dp))

                // Navigation Items
                val navItems = listOf(
                    Triple(ScreenTab.DASHBOARD, "Operations Center", Icons.Default.Dashboard),
                    Triple(ScreenTab.DISPATCH, "Live Dispatch Board", Icons.Default.ViewKanban),
                    Triple(ScreenTab.BOOKINGS, "Bookings Directory", Icons.Default.BookOnline),
                    Triple(ScreenTab.LIVE_TRACKING, "GPS Fleet Radar", Icons.Default.Map),
                    Triple(ScreenTab.DRIVER_CONSOLE, "Driver Duty App", Icons.Default.PhoneAndroid),
                    Triple(ScreenTab.FLEET, "Vehicles Master", Icons.Default.DirectionsCar),
                    Triple(ScreenTab.DRIVERS, "Chauffeurs Master", Icons.Default.People),
                    Triple(ScreenTab.BILLING, "Billing & Invoices", Icons.Default.Receipt),
                    Triple(ScreenTab.REPORTS, "P&L & Analytics", Icons.Default.BarChart),
                    Triple(ScreenTab.CORPORATE, "Corporate & Vendors", Icons.Default.Business),
                    Triple(ScreenTab.SETTINGS, "Settings & APIs", Icons.Default.Settings)
                )

                navItems.forEach { (tab, label, icon) ->
                    val isSelected = uiState.currentTab == tab
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) FleetCyanAccent else Color.White.copy(alpha = 0.7f)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            viewModel.setTab(tab)
                            coroutineScope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = FleetNavy700,
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "VeloFleet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = FleetCyanAccent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = uiState.currentTab.title.uppercase(),
                                        color = FleetCyanAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.setTab(ScreenTab.LIVE_TRACKING) }) {
                                Icon(Icons.Default.MyLocation, contentDescription = "GPS Radar", tint = FleetCyanAccent)
                            }
                            IconButton(onClick = { viewModel.openCreateBooking(true) }) {
                                Icon(Icons.Default.AddCircle, contentDescription = "New Booking", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = FleetNavy900,
                            titleContentColor = Color.White
                        )
                    )

                    // Role Switcher Sub-Header
                    RoleSwitcherBar(
                        currentRole = uiState.currentRole,
                        onRoleSelected = { viewModel.setRole(it) }
                    )
                }
            },
            bottomBar = {
                // High-ergonomics bottom navigation for primary mobile navigation
                NavigationBar(
                    containerColor = FleetNavy900,
                    contentColor = Color.White,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    val primaryTabs = listOf(
                        Triple(ScreenTab.DASHBOARD, "Ops", Icons.Default.Dashboard),
                        Triple(ScreenTab.DISPATCH, "Dispatch", Icons.Default.ViewKanban),
                        Triple(ScreenTab.BOOKINGS, "Bookings", Icons.Default.BookOnline),
                        Triple(ScreenTab.LIVE_TRACKING, "Radar", Icons.Default.Map),
                        Triple(ScreenTab.DRIVER_CONSOLE, "Driver App", Icons.Default.PhoneAndroid)
                    )

                    primaryTabs.forEach { (tab, label, icon) ->
                        val isSelected = uiState.currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setTab(tab) },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FleetCyanAccent,
                                selectedTextColor = FleetCyanAccent,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                indicatorColor = FleetNavy700
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (uiState.currentTab) {
                    ScreenTab.DASHBOARD -> DashboardScreen(
                        bookings = bookings,
                        alerts = alerts,
                        isSimulationActive = uiState.isSimulationActive,
                        onToggleSimulation = { viewModel.toggleSimulation() },
                        onNavigateTab = { viewModel.setTab(it) },
                        onSelectBooking = { viewModel.selectBooking(it) },
                        onCreateBookingClick = { viewModel.openCreateBooking(true) },
                        onResolveAlert = { viewModel.resolveAlert(it) }
                    )

                    ScreenTab.DISPATCH -> DispatchBoardScreen(
                        bookings = bookings,
                        onSelectBooking = { viewModel.selectBooking(it) },
                        onAssignDriverClick = { viewModel.openAssignModal(it) },
                        onTransitionStatus = { bId, next -> viewModel.transitionStatus(bId, next) },
                        onGenerateInvoice = { viewModel.generateInvoice(it) }
                    )

                    ScreenTab.BOOKINGS -> BookingListScreen(
                        bookings = bookings,
                        searchQuery = uiState.searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        statusFilter = uiState.statusFilter,
                        onStatusFilterChange = { viewModel.setStatusFilter(it) },
                        onSelectBooking = { viewModel.selectBooking(it) },
                        onCreateBookingClick = { viewModel.openCreateBooking(true) }
                    )

                    ScreenTab.LIVE_TRACKING -> LiveTrackingMapScreen(
                        drivers = drivers,
                        isSimulationActive = uiState.isSimulationActive,
                        onToggleSimulation = { viewModel.toggleSimulation() },
                        onSelectDriver = { viewModel.setDriverForConsole(it) }
                    )

                    ScreenTab.DRIVER_CONSOLE -> DriverDutyConsoleScreen(
                        drivers = drivers,
                        selectedDriverId = uiState.selectedDriverForConsoleId,
                        onDriverSelected = { viewModel.setDriverForConsole(it) },
                        bookings = bookings,
                        onAcceptDuty = { viewModel.driverAcceptDuty(it) },
                        onRejectDuty = { viewModel.driverRejectDuty(it) },
                        onEnroute = { viewModel.driverEnroute(it) },
                        onArrived = { viewModel.driverArrived(it) },
                        onStartDuty = { bId, otp, km -> viewModel.driverStartDuty(bId, otp, km) },
                        onCompleteDuty = { bId, endKm, toll, park, rat, fbk -> viewModel.driverCompleteDuty(bId, endKm, toll, park, rat, fbk) },
                        onAddExpense = { drv, veh, cat, amt, note, bId -> viewModel.addExpense(drv, veh, cat, amt, note, bId) },
                        onShowMessage = { viewModel.showToast(it) }
                    )

                    ScreenTab.FLEET -> FleetVehiclesScreen(vehicles = vehicles)

                    ScreenTab.DRIVERS -> DriversMasterScreen(drivers = drivers)

                    ScreenTab.BILLING -> BillingScreen(
                        invoices = invoices,
                        expenses = expenses,
                        suppliers = suppliers,
                        onShowMessage = { viewModel.showToast(it) }
                    )

                    ScreenTab.REPORTS -> ReportsScreen(
                        bookings = bookings,
                        onShowMessage = { viewModel.showToast(it) }
                    )

                    ScreenTab.CORPORATE -> CorporatePortalScreen(
                        customers = customers,
                        suppliers = suppliers,
                        onShowMessage = { viewModel.showToast(it) }
                    )

                    ScreenTab.SETTINGS -> SettingsScreen(
                        onShowMessage = { viewModel.showToast(it) }
                    )
                }

                // Modals
                if (uiState.isCreateBookingOpen) {
                    CreateBookingDialog(
                        customers = customers,
                        onDismiss = { viewModel.openCreateBooking(false) },
                        onSubmit = { cId, cName, bName, bPhone, bEmail, pName, pPhone, trip, cat, pCity, pAddr, dCity, dAddr, time, flt, rem, fare ->
                            viewModel.createNewBooking(cId, cName, bName, bPhone, bEmail, pName, pPhone, trip, cat, pCity, pAddr, dCity, dAddr, time, flt, rem, fare)
                        }
                    )
                }

                if (uiState.isAssignDriverOpen && uiState.assignBookingTarget != null) {
                    AssignDriverDialog(
                        booking = uiState.assignBookingTarget!!,
                        drivers = drivers,
                        vehicles = vehicles,
                        onDismiss = { viewModel.openAssignModal(null) },
                        onConfirm = { bId, dId, vId ->
                            viewModel.assignDriver(bId, dId, vId)
                        }
                    )
                }

                if (uiState.selectedBookingId != null) {
                    val selBooking = bookings.firstOrNull { it.id == uiState.selectedBookingId }
                    if (selBooking != null) {
                        BookingDetailDialog(
                            booking = selBooking,
                            onDismiss = { viewModel.selectBooking(null) },
                            onAssignDriverClick = {
                                viewModel.selectBooking(null)
                                viewModel.openAssignModal(it)
                            },
                            onGenerateInvoice = { viewModel.generateInvoice(it) }
                        )
                    }
                }
            }
        }
    }
}
