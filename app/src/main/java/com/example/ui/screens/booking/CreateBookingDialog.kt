package com.example.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.CustomerEntity
import com.example.data.model.TripType
import com.example.data.model.VehicleCategory
import com.example.ui.theme.FleetBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingDialog(
    customers: List<CustomerEntity>,
    onDismiss: () -> Unit,
    onSubmit: (
        customerId: String,
        customerName: String,
        bookerName: String,
        bookerPhone: String,
        bookerEmail: String,
        passengerName: String,
        passengerPhone: String,
        tripType: String,
        vehicleCategory: String,
        pickupCity: String,
        pickupAddress: String,
        dropCity: String,
        dropAddress: String,
        pickupDateTime: Long,
        flightOrTrain: String,
        remarks: String,
        baseFare: Double
    ) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
    var passengerName by remember { mutableStateOf("") }
    var passengerPhone by remember { mutableStateOf("") }
    var bookerName by remember { mutableStateOf("") }
    var bookerPhone by remember { mutableStateOf("") }
    var selectedTripType by remember { mutableStateOf(TripType.AIRPORT_DROP) }
    var selectedCategory by remember { mutableStateOf(VehicleCategory.SEDAN) }
    var pickupCity by remember { mutableStateOf("Bangalore") }
    var pickupAddress by remember { mutableStateOf("") }
    var dropCity by remember { mutableStateOf("Bangalore") }
    var dropAddress by remember { mutableStateOf("") }
    var flightOrTrain by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val calculatedFare = selectedCategory.basePriceRate + when (selectedTripType) {
        TripType.AIRPORT_PICKUP, TripType.AIRPORT_DROP -> 600.0
        TripType.OUTSTATION_ONE_WAY, TripType.OUTSTATION_ROUND_TRIP -> 3200.0
        TripType.FULL_DAY -> 1400.0
        else -> 0.0
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Create New Booking",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Fleet reservation & dispatch engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Customer Selector
                    Text("Select Corporate Customer", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    var customerMenuExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = customerMenuExpanded,
                        onExpandedChange = { customerMenuExpanded = !customerMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCustomer?.companyName ?: "Select Customer",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = customerMenuExpanded,
                            onDismissRequest = { customerMenuExpanded = false }
                        ) {
                            customers.forEach { customer ->
                                DropdownMenuItem(
                                    text = { Text(customer.companyName) },
                                    onClick = {
                                        selectedCustomer = customer
                                        bookerName = customer.contactPerson
                                        bookerPhone = customer.mobile
                                        customerMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Passenger Details
                    Text("Passenger Details", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = passengerName,
                            onValueChange = { passengerName = it },
                            label = { Text("Passenger Name") },
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = passengerPhone,
                            onValueChange = { passengerPhone = it },
                            label = { Text("Mobile (+91)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Trip Type & Vehicle Category
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Trip Type", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            var tripTypeExpanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { tripTypeExpanded = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(selectedTripType.label, fontSize = 12.sp, maxLines = 1)
                                }
                                DropdownMenu(expanded = tripTypeExpanded, onDismissRequest = { tripTypeExpanded = false }) {
                                    TripType.entries.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type.label) },
                                            onClick = {
                                                selectedTripType = type
                                                tripTypeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Vehicle Group", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            var categoryExpanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { categoryExpanded = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(selectedCategory.label, fontSize = 12.sp, maxLines = 1)
                                }
                                DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                                    VehicleCategory.entries.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat.label) },
                                            onClick = {
                                                selectedCategory = cat
                                                categoryExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Pickup Address & Drop Address
                    Text("Pickup & Drop Locations", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    OutlinedTextField(
                        value = pickupAddress,
                        onValueChange = { pickupAddress = it },
                        label = { Text("Pickup Address (e.g. Hotel / Office / Terminal)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dropAddress,
                        onValueChange = { dropAddress = it },
                        label = { Text("Drop Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Optional Flight Number & Remarks
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = flightOrTrain,
                            onValueChange = { flightOrTrain = it },
                            label = { Text("Flight / Train (Optional)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Instructions") },
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    // Estimated Pricing Summary Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Calculated Base Fare", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${calculatedFare.toInt()} (+ 5% GST)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                text = "Auto Rate Card",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val cust = selectedCustomer ?: customers.first()
                            onSubmit(
                                cust.id,
                                cust.companyName,
                                bookerName.ifBlank { cust.contactPerson },
                                bookerPhone.ifBlank { cust.mobile },
                                cust.email,
                                passengerName.ifBlank { "Executive Passenger" },
                                passengerPhone.ifBlank { "+91 98000 00000" },
                                selectedTripType.name,
                                selectedCategory.label,
                                pickupCity,
                                pickupAddress.ifBlank { "Pickup Point, Bangalore" },
                                dropCity,
                                dropAddress.ifBlank { "Kempegowda Int'l Airport" },
                                System.currentTimeMillis() + (2 * 3600 * 1000),
                                flightOrTrain,
                                remarks,
                                calculatedFare
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FleetBluePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirm & Create Duty", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
