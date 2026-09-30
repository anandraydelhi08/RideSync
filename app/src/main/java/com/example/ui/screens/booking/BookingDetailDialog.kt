package com.example.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.BookingEntity
import com.example.data.model.DutyStatus
import com.example.ui.components.DutyStatusBadge
import com.example.ui.theme.*

@Composable
fun BookingDetailDialog(
    booking: BookingEntity,
    onDismiss: () -> Unit,
    onAssignDriverClick: (BookingEntity) -> Unit,
    onGenerateInvoice: (String) -> Unit
) {
    val activeSteps = DutyStatus.activeSteps
    val currentStepIndex = try {
        DutyStatus.valueOf(booking.status).stepIndex
    } catch (e: Exception) {
        0
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = booking.id,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DutyStatusBadge(status = booking.status)
                        }
                        Text(
                            text = booking.customerName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Duty Lifecycle Stepper Timeline
                Text(
                    text = "DUTY LIFECYCLE TIMELINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(activeSteps) { index, step ->
                        val isPassed = currentStepIndex >= step.stepIndex && currentStepIndex >= 0
                        val isCurrent = currentStepIndex == step.stepIndex

                        Surface(
                            color = when {
                                isCurrent -> FleetCyanAccent.copy(alpha = 0.2f)
                                isPassed -> Color(0xFFDCFCE7)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, FleetBluePrimary) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCurrent -> FleetBluePrimary
                                                isPassed -> Color(0xFF16A34A)
                                                else -> Color.Gray
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = step.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isCurrent -> FleetBluePrimary
                                        isPassed -> Color(0xFF166534)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Body Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Passenger & Booker Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "PASSENGER & BOOKER DETAILS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Passenger: ${booking.passengerName}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Contact: ${booking.passengerPhone} • ${booking.passengerEmail}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Booked By: ${booking.bookerName} (${booking.bookerPhone})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (booking.flightOrTrainNumber.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Flight/Train: ${booking.flightOrTrainNumber} • Terminal ${booking.airportTerminal}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = FleetBluePrimary
                                )
                            }
                        }
                    }

                    // Route Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "ROUTE & LOCATIONS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TripOrigin, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Pickup Point", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(booking.pickupAddress, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Drop Location", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(booking.dropAddress, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    // Chauffeur & Vehicle Allotment Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CHAUFFEUR & FLEET ASSET",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                                if (booking.assignedDriverId == null) {
                                    TextButton(onClick = { onAssignDriverClick(booking) }) {
                                        Text("Assign Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            if (booking.assignedDriverName != null) {
                                Text(
                                    text = "Driver: ${booking.assignedDriverName} (${booking.assignedDriverPhone ?: ""})",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Vehicle: ${booking.assignedVehicleReg} • ${booking.assignedVehicleModel}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "⚠️ Unassigned. Needs Chauffeur Allotment.",
                                    fontSize = 12.sp,
                                    color = StatusAlert,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Passenger OTP & Security Verification Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (booking.isOtpVerified) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PASSENGER OTP (SECURITY VERIFICATION)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (booking.isOtpVerified) Color(0xFF166534) else Color(0xFF92400E)
                                )
                                Text(
                                    text = "OTP: ${booking.passengerOtp}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    color = if (booking.isOtpVerified) Color(0xFF166534) else Color(0xFF92400E)
                                )
                            }
                            Surface(
                                color = if (booking.isOtpVerified) Color(0xFF16A34A) else Color(0xFFD97706),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (booking.isOtpVerified) "VERIFIED" else "PENDING START",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Rate Card & Profitability
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "COMMERCIALS & PROFITABILITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Customer Base Fare:", fontSize = 12.sp)
                                Text("₹${booking.baseFare.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Toll & Parking Charges:", fontSize = 12.sp)
                                Text("₹${(booking.tollCharges + booking.parkingCharges).toInt()}", fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("GST (5%):", fontSize = 12.sp)
                                Text("₹${booking.gstAmount.toInt()}", fontSize = 12.sp)
                            }
                            Divider(modifier = Modifier.padding(vertical = 6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Customer Amount:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("₹${booking.totalCustomerAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Supplier / Fleet Cost:", fontSize = 12.sp, color = Color(0xFFD97706))
                                Text("₹${booking.supplierCost.toInt()}", fontSize = 12.sp, color = Color(0xFFD97706))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Duty Gross Margin:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                Text("₹${booking.dutyProfit.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (booking.status == "COMPLETED" && booking.invoiceStatus == "UNBILLED") {
                        Button(
                            onClick = {
                                onGenerateInvoice(booking.id)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Tax Invoice", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = FleetBluePrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Close", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
