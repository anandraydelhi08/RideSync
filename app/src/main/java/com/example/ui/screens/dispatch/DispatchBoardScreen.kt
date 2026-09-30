package com.example.ui.screens.dispatch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BookingEntity
import com.example.data.model.DutyStatus
import com.example.ui.components.DutyStatusBadge
import com.example.ui.theme.*

enum class DispatchLane(val title: String, val statuses: List<String>) {
    UNASSIGNED("Unassigned", listOf("BOOKED", "CONFIRMED")),
    ALLOTTED("Allotted / Accepted", listOf("ALLOTTED", "DRIVER_ACCEPTED")),
    ENROUTE("Enroute / Arrived", listOf("DISPATCHED", "ENROUTE", "ARRIVED")),
    IN_PROGRESS("In Progress", listOf("STARTED", "IN_PROGRESS")),
    COMPLETED("Completed", listOf("COMPLETED", "CLOSED"))
}

@Composable
fun DispatchBoardScreen(
    bookings: List<BookingEntity>,
    onSelectBooking: (String) -> Unit,
    onAssignDriverClick: (BookingEntity) -> Unit,
    onTransitionStatus: (String, DutyStatus) -> Unit,
    onGenerateInvoice: (String) -> Unit
) {
    var selectedLane by remember { mutableStateOf(DispatchLane.UNASSIGNED) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Live Dispatch Board",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Manage duty allocations, driver acceptances, and trip progressions",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Lane Tabs with counters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DispatchLane.entries) { lane ->
                val count = bookings.count { it.status in lane.statuses }
                val isSelected = selectedLane == lane

                FilterChip(
                    selected = isSelected,
                    onClick = { selectedLane = lane },
                    label = {
                        Text(
                            text = "${lane.title} ($count)",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val laneBookings = bookings.filter { it.status in selectedLane.statuses }

        if (laneBookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No duties in ${selectedLane.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(laneBookings) { booking ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = booking.vehicleCategory,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                DutyStatusBadge(status = booking.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = booking.customerName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Passenger: ${booking.passengerName} • ${booking.passengerPhone}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Route
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TripOrigin, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(booking.pickupAddress, fontSize = 12.sp, maxLines = 1)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(booking.dropAddress, fontSize = 12.sp, maxLines = 1)
                            }

                            // Chauffeur / Vehicle info
                            if (booking.assignedDriverName != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Chauffeur: ${booking.assignedDriverName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = booking.assignedVehicleReg ?: "",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Operational Contextual Action Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (booking.status) {
                                    "BOOKED", "CONFIRMED" -> {
                                        Button(
                                            onClick = { onAssignDriverClick(booking) },
                                            colors = ButtonDefaults.buttonColors(containerColor = FleetBluePrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Assign Chauffeur", fontSize = 12.sp)
                                        }
                                    }
                                    "ALLOTTED" -> {
                                        OutlinedButton(
                                            onClick = { onTransitionStatus(booking.id, DutyStatus.DRIVER_ACCEPTED) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Force Accept", fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { onTransitionStatus(booking.id, DutyStatus.DISPATCHED) },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusDispatched),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Dispatch", fontSize = 12.sp)
                                        }
                                    }
                                    "DRIVER_ACCEPTED" -> {
                                        Button(
                                            onClick = { onTransitionStatus(booking.id, DutyStatus.ENROUTE) },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusEnroute),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Send Enroute", fontSize = 12.sp)
                                        }
                                    }
                                    "ENROUTE" -> {
                                        Button(
                                            onClick = { onTransitionStatus(booking.id, DutyStatus.ARRIVED) },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusArrived),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Mark Arrived", fontSize = 12.sp)
                                        }
                                    }
                                    "COMPLETED" -> {
                                        if (booking.invoiceStatus == "UNBILLED") {
                                            Button(
                                                onClick = { onGenerateInvoice(booking.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Generate Tax Invoice", fontSize = 12.sp)
                                            }
                                        } else {
                                            Text(
                                                text = "Invoice ${booking.invoiceId ?: "Generated"}",
                                                color = Color(0xFF059669),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    else -> {
                                        OutlinedButton(
                                            onClick = { onSelectBooking(booking.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Duty Details", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
