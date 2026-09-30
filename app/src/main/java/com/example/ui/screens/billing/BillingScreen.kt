package com.example.ui.screens.billing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.InvoiceEntity
import com.example.data.local.entity.SupplierEntity
import com.example.ui.theme.FleetBluePrimary

@Composable
fun BillingScreen(
    invoices: List<InvoiceEntity>,
    expenses: List<ExpenseEntity>,
    suppliers: List<SupplierEntity>,
    onShowMessage: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedInvoiceForPreview by remember { mutableStateOf<InvoiceEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Billing & Accounts Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Tax invoices, GST compliance, supplier settlements, and chauffer expense audits",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Customer Invoices (${invoices.size})", fontSize = 12.sp, modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Supplier Payables", fontSize = 12.sp, modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("Trip Expenses (${expenses.size})", fontSize = 12.sp, modifier = Modifier.padding(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                // Invoices List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(invoices) { inv ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedInvoiceForPreview = inv }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = inv.id,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Ref: ${inv.bookingId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Surface(
                                        color = if (inv.paymentStatus == "PAID") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = inv.paymentStatus,
                                            color = if (inv.paymentStatus == "PAID") Color(0xFF166534) else Color(0xFF92400E),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(inv.customerName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("GSTIN: ${inv.gstin}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Taxable: ₹${inv.taxableAmount.toInt()} | GST (5%): ₹${(inv.cgst + inv.sgst).toInt()}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Total: ₹${inv.totalAmount.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Supplier Payables
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(suppliers) { sup ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(sup.companyName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Fleet: ${sup.activeFleetCount} Cabs", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Text("Contact: ${sup.contactPerson} • ${sup.phone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Bank: ${sup.bankAccount} (${sup.ifscCode})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Pending Payable", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("₹${sup.totalPayable.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFD97706))
                                    }
                                    Button(
                                        onClick = { onShowMessage("Disbursing ₹${sup.totalPayable.toInt()} via Direct Corporate NEFT to ${sup.companyName}") },
                                        colors = ButtonDefaults.buttonColors(containerColor = FleetBluePrimary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Process Payout", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Trip Expenses
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(expenses) { exp ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(exp.category, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        if (exp.bookingId != null) {
                                            Text("(${exp.bookingId})", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Text(exp.receiptNote, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (exp.vehicleReg != null) {
                                        Text("Vehicle: ${exp.vehicleReg}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("₹${exp.amount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF059669))
                                    Surface(
                                        color = if (exp.isApproved) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (exp.isApproved) "APPROVED" else "AUDIT PENDING",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (exp.isApproved) Color(0xFF166534) else Color(0xFF92400E),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

    // Modal: Full GST Tax Invoice Preview with WhatsApp Share & Print simulation
    if (selectedInvoiceForPreview != null) {
        val inv = selectedInvoiceForPreview!!
        Dialog(onDismissRequest = { selectedInvoiceForPreview = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .fillMaxHeight(0.88f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TAX INVOICE", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = FleetBluePrimary)
                            Text(inv.id, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        IconButton(onClick = { selectedInvoiceForPreview = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Company & Client Header
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("VeloFleet Logistics & Mobility Pvt Ltd", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("GSTIN: 29AABCV1029P1Z4 • SAC: 996601", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Central Hub: MG Road / Bangalore Airport Corridor", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("BILLED TO:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(inv.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("GSTIN: ${inv.gstin}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Booking Reference: ${inv.bookingId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Line Items Table
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("SERVICES BREAKDOWN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Chauffeur Drive Services (Passenger Transfer)", fontSize = 12.sp)
                                    Text("₹${inv.taxableAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("CGST (2.5%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${inv.cgst.toInt()}", fontSize = 11.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("SGST (2.5%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${inv.sgst.toInt()}", fontSize = 11.sp)
                                }
                                Divider(modifier = Modifier.padding(vertical = 6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("INVOICE TOTAL (INR)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("₹${inv.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FleetBluePrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons: WhatsApp / Print / Download
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onShowMessage("WhatsApp message template prepared for ${inv.customerName}")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onShowMessage("Invoice PDF rendered for ${inv.id}")
                                selectedInvoiceForPreview = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FleetBluePrimary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Print PDF", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
