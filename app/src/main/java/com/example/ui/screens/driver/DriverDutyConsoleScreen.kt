package com.example.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.DriverEntity
import com.example.ui.components.DutyStatusBadge
import com.example.ui.theme.*

@Composable
fun DriverDutyConsoleScreen(
    drivers: List<DriverEntity>,
    selectedDriverId: String,
    onDriverSelected: (String) -> Unit,
    bookings: List<BookingEntity>,
    onAcceptDuty: (String) -> Unit,
    onRejectDuty: (String) -> Unit,
    onEnroute: (String) -> Unit,
    onArrived: (String) -> Unit,
    onStartDuty: (String, String, Double) -> Unit,
    onCompleteDuty: (String, Double, Double, Double, Float, String?) -> Unit,
    onAddExpense: (driverId: String, vehicleReg: String, category: String, amount: Double, note: String, bookingId: String?) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val currentDriver = drivers.firstOrNull { it.id == selectedDriverId } ?: drivers.firstOrNull()
    val driverBookings = bookings.filter { it.assignedDriverId == currentDriver?.id }
    val activeDuty = driverBookings.firstOrNull {
        it.status in listOf("ALLOTTED", "DRIVER_ACCEPTED", "DISPATCHED", "ENROUTE", "ARRIVED", "STARTED", "IN_PROGRESS")
    }

    var showStartDutyModal by remember { mutableStateOf(false) }
    var showEndDutyModal by remember { mutableStateOf(false) }
    var showAddExpenseModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Driver Profile & Selector Header
        Card(
            colors = CardDefaults.cardColors(containerColor = FleetNavy800),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FleetTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = FleetNavy900, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = currentDriver?.name ?: "Chauffeur",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentDriver?.assignedVehicleReg} • ${currentDriver?.assignedBranch}",
                                color = FleetCyanAccent,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Chauffeur switch dropdown
                    var driverMenuExpanded by remember { mutableStateOf(false) }
                    Box {
                        TextButton(
                            onClick = { driverMenuExpanded = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("Switch Chauffeur", fontSize = 11.sp, color = FleetCyanAccent)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = FleetCyanAccent)
                        }
                        DropdownMenu(
                            expanded = driverMenuExpanded,
                            onDismissRequest = { driverMenuExpanded = false }
                        ) {
                            drivers.forEach { drv ->
                                DropdownMenuItem(
                                    text = { Text("${drv.name} (${drv.assignedVehicleReg})") },
                                    onClick = {
                                        onDriverSelected(drv.id)
                                        driverMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⭐ Rating: ${currentDriver?.rating ?: 4.8f}★",
                        color = Color(0xFFFDE68A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Completed Trips: ${currentDriver?.totalTrips ?: 300}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Battery: ${currentDriver?.batteryPct ?: 90}%",
                        color = Color(0xFFA7F3D0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Duty Section
        if (activeDuty != null) {
            Text(
                text = "CURRENT ACTIVE DUTY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeDuty.id,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        DutyStatusBadge(status = activeDuty.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Passenger: ${activeDuty.passengerName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Client: ${activeDuty.customerName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Route details
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TripOrigin, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(activeDuty.pickupAddress, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(activeDuty.dropAddress, fontSize = 12.sp)
                    }

                    if (activeDuty.flightOrTrainNumber.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Flight: ${activeDuty.flightOrTrainNumber} (Terminal ${activeDuty.airportTerminal})",
                            fontSize = 12.sp,
                            color = FleetBluePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Communication buttons: Call & Navigate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onShowMessage("Calling Passenger: ${activeDuty.passengerPhone}") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { onShowMessage("Google Maps Navigation launched for: ${activeDuty.dropAddress}") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Navigate", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showAddExpenseModal = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Expense", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Strict State Action Buttons
                    when (activeDuty.status) {
                        "ALLOTTED" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onAcceptDuty(activeDuty.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ACCEPT DUTY", fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { onRejectDuty(activeDuty.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusAlert),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("DECLINE", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        "DRIVER_ACCEPTED", "DISPATCHED" -> {
                            Button(
                                onClick = { onEnroute(activeDuty.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusEnroute),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("START ENROUTE TO PICKUP", fontWeight = FontWeight.Bold)
                            }
                        }
                        "ENROUTE" -> {
                            Button(
                                onClick = { onArrived(activeDuty.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusArrived),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("I HAVE ARRIVED AT PICKUP", fontWeight = FontWeight.Bold)
                            }
                        }
                        "ARRIVED" -> {
                            Button(
                                onClick = { showStartDutyModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("START DUTY (VERIFY OTP & ODO)", fontWeight = FontWeight.Bold)
                            }
                        }
                        "STARTED", "IN_PROGRESS" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Trip In Progress • Start Odo: ${activeDuty.startKm.toInt()} km", color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text("OTP Verified ✓", color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Button(
                                    onClick = { showEndDutyModal = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusAlert),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("END TRIP / COMPLETE DUTY", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Active Duty Assigned", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("You are currently available on the dispatch board.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upcoming / Completed Duties
        Text(
            text = "DUTY HISTORY FOR THIS CHAUFFEUR",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(driverBookings) { b ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(b.id, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(b.passengerName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Text(b.dropAddress, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                        DutyStatusBadge(status = b.status)
                    }
                }
            }
        }
    }

    // Modal: Start Duty (Prompt for Start KM & Passenger OTP)
    if (showStartDutyModal && activeDuty != null) {
        var startKmInput by remember { mutableStateOf(activeDuty.startKm.takeIf { it > 0 }?.toString() ?: "18210") }
        var otpInput by remember { mutableStateOf(activeDuty.passengerOtp) } // Pre-fill with demo OTP for instant convenience

        Dialog(onDismissRequest = { showStartDutyModal = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Start Duty - OTP Verification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Passenger must provide the 4-digit security OTP", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { otpInput = it },
                        label = { Text("Passenger OTP (4 digits)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = startKmInput,
                        onValueChange = { startKmInput = it },
                        label = { Text("Start Odometer (KM)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showStartDutyModal = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val km = startKmInput.toDoubleOrNull() ?: 18210.0
                                onStartDuty(activeDuty.id, otpInput, km)
                                showStartDutyModal = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                        ) {
                            Text("Verify & Start Trip", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: End Duty (Prompt for End KM, Toll, Parking, Passenger Rating)
    if (showEndDutyModal && activeDuty != null) {
        var endKmInput by remember { mutableStateOf((activeDuty.startKm + 38.0).toInt().toString()) }
        var tollInput by remember { mutableStateOf("115") }
        var parkingInput by remember { mutableStateOf("0") }
        var feedbackInput by remember { mutableStateOf("Trip completed smoothly on time.") }

        Dialog(onDismissRequest = { showEndDutyModal = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Complete Duty & Close Trip", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Submit end odometer reading & extra expenses", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = endKmInput,
                        onValueChange = { endKmInput = it },
                        label = { Text("End Odometer Reading (KM)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = tollInput,
                            onValueChange = { tollInput = it },
                            label = { Text("Toll Charges (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = parkingInput,
                            onValueChange = { parkingInput = it },
                            label = { Text("Parking (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = feedbackInput,
                        onValueChange = { feedbackInput = it },
                        label = { Text("Passenger Feedback") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showEndDutyModal = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val endKm = endKmInput.toDoubleOrNull() ?: (activeDuty.startKm + 38.0)
                                val toll = tollInput.toDoubleOrNull() ?: 0.0
                                val parking = parkingInput.toDoubleOrNull() ?: 0.0
                                onCompleteDuty(activeDuty.id, endKm, toll, parking, 5.0f, feedbackInput)
                                showEndDutyModal = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusAlert)
                        ) {
                            Text("Submit & End Trip", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: Add Chauffeur Expense
    if (showAddExpenseModal && currentDriver != null) {
        var category by remember { mutableStateOf("Toll") }
        var amount by remember { mutableStateOf("150") }
        var note by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddExpenseModal = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Add Trip Expense", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Submit receipt for tolls, parking, fuel or food", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Toll", "Parking", "Fuel", "Food").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Receipt note / Plaza name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddExpenseModal = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amt = amount.toDoubleOrNull() ?: 100.0
                                onAddExpense(
                                    currentDriver.id,
                                    currentDriver.assignedVehicleReg,
                                    category,
                                    amt,
                                    note.ifBlank { "$category expense" },
                                    activeDuty?.id
                                )
                                showAddExpenseModal = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FleetBluePrimary)
                        ) {
                            Text("Save Expense", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
