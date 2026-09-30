package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AlertEntity
import com.example.data.local.entity.BookingEntity
import com.example.ui.components.DutyStatusBadge
import com.example.ui.components.MetricCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScreenTab

@Composable
fun DashboardScreen(
    bookings: List<BookingEntity>,
    alerts: List<AlertEntity>,
    isSimulationActive: Boolean,
    onToggleSimulation: () -> Unit,
    onNavigateTab: (ScreenTab) -> Unit,
    onSelectBooking: (String) -> Unit,
    onCreateBookingClick: () -> Unit,
    onResolveAlert: (String) -> Unit
) {
    // KPI metrics calculation
    val todayBookings = bookings.size
    val inProgress = bookings.count { it.status == "IN_PROGRESS" || it.status == "STARTED" }
    val enrouteOrArrived = bookings.count { it.status == "ENROUTE" || it.status == "ARRIVED" }
    val unassigned = bookings.count { it.status == "BOOKED" || it.status == "CONFIRMED" }
    val completed = bookings.count { it.status == "COMPLETED" || it.status == "CLOSED" }
    val totalRevenue = bookings.sumOf { it.totalCustomerAmount }
    val totalSupplierCost = bookings.sumOf { it.supplierCost }
    val totalProfit = bookings.sumOf { it.dutyProfit }
    val activeAlerts = alerts.filter { !it.isResolved }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Operational Header & Live Status
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = FleetNavy800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bangalore Central Hub",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Fleet Operations Control Center",
                                color = FleetCyanAccent,
                                fontSize = 12.sp
                            )
                        }

                        // Simulation / Real GPS badge
                        Surface(
                            onClick = onToggleSimulation,
                            color = if (isSimulationActive) Color(0xFF064E3B) else Color(0xFF451A03),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isSimulationActive) Color(0xFF10B981) else Color(0xFFF59E0B))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSimulationActive) "LIVE GPS (SIM)" else "GPS PAUSED",
                                    color = if (isSimulationActive) Color(0xFFA7F3D0) else Color(0xFFFDE68A),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onCreateBookingClick,
                            colors = ButtonDefaults.buttonColors(containerColor = FleetBluePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Booking", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { onNavigateTab(ScreenTab.DISPATCH) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ViewKanban, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispatch", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { onNavigateTab(ScreenTab.LIVE_TRACKING) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FleetCyanAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Radar", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Needs Attention Alert Banner
        if (activeAlerts.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = StatusAlert,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Needs Attention (${activeAlerts.size})",
                                color = StatusAlert,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        activeAlerts.take(2).forEach { alert ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alert.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF7F1D1D)
                                    )
                                    Text(
                                        text = alert.description,
                                        fontSize = 11.sp,
                                        color = Color(0xFF991B1B),
                                        maxLines = 1
                                    )
                                }
                                TextButton(
                                    onClick = { onResolveAlert(alert.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Resolve", fontSize = 11.sp, color = StatusAlert)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Financial KPIs
        item {
            Text(
                text = "FINANCIAL PERFORMANCE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Revenue",
                    value = "₹${(totalRevenue).toInt()}",
                    subtext = "${todayBookings} Bookings",
                    icon = Icons.Default.TrendingUp,
                    accentColor = FleetBluePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(ScreenTab.BILLING) }
                )
                MetricCard(
                    title = "Supplier Cost",
                    value = "₹${(totalSupplierCost).toInt()}",
                    subtext = "Fleet payouts",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = Color(0xFFD97706),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(ScreenTab.BILLING) }
                )
                MetricCard(
                    title = "Duty Profit",
                    value = "₹${(totalProfit).toInt()}",
                    subtext = "${if (totalRevenue > 0) ((totalProfit / totalRevenue) * 100).toInt() else 0}% margin",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = Color(0xFF059669),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(ScreenTab.REPORTS) }
                )
            }
        }

        // Operational Pipeline Cards
        item {
            Text(
                text = "LIVE FLEET STATUS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "On Trip",
                    value = inProgress.toString(),
                    subtext = "Live GPS Active",
                    icon = Icons.Default.Navigation,
                    accentColor = StatusInProgress,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(ScreenTab.LIVE_TRACKING) }
                )
                MetricCard(
                    title = "Enroute/At Pickup",
                    value = enrouteOrArrived.toString(),
                    subtext = "Approaching",
                    icon = Icons.Default.DirectionsCar,
                    accentColor = StatusEnroute,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(ScreenTab.DISPATCH) }
                )
                MetricCard(
                    title = "Needs Driver",
                    value = unassigned.toString(),
                    subtext = "Unassigned",
                    icon = Icons.Default.PersonSearch,
                    accentColor = if (unassigned > 0) StatusAlert else StatusClosed,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(ScreenTab.DISPATCH) }
                )
            }
        }

        // Active Duties List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE DUTIES & UPCOMING PICKUPS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { onNavigateTab(ScreenTab.BOOKINGS) }) {
                    Text("View All", fontSize = 12.sp)
                }
            }
        }

        items(bookings.take(5)) { booking ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectBooking(booking.id) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = booking.id,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${booking.tripType.replace("_", " ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DutyStatusBadge(status = booking.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = booking.customerName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Passenger: ${booking.passengerName} (${booking.passengerPhone})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Route details
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.TripOrigin,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = booking.pickupAddress,
                            fontSize = 12.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = booking.dropAddress,
                            fontSize = 12.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chauffeur / Vehicle badge & Amount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (booking.assignedDriverName != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${booking.assignedDriverName} (${booking.assignedVehicleReg ?: ""})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Text(
                                text = "⚠️ No driver assigned",
                                fontSize = 11.sp,
                                color = StatusAlert,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "₹${booking.totalCustomerAmount.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
