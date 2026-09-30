package com.example.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BookingEntity
import com.example.ui.components.MetricCard
import com.example.ui.theme.FleetBluePrimary

@Composable
fun ReportsScreen(
    bookings: List<BookingEntity>,
    onShowMessage: (String) -> Unit
) {
    val totalRevenue = bookings.sumOf { it.totalCustomerAmount }
    val totalCost = bookings.sumOf { it.supplierCost }
    val totalProfit = bookings.sumOf { it.dutyProfit }
    val marginPct = if (totalRevenue > 0) ((totalProfit / totalRevenue) * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Operational P&L & Reports",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Duty-level profitability, fleet yields, and margins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = { onShowMessage("Exported P&L Report to CSV (VeloFleet_Report_2026.csv)") },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // P&L Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Total Revenue",
                value = "₹${totalRevenue.toInt()}",
                subtext = "Billed amount",
                icon = Icons.Default.TrendingUp,
                accentColor = FleetBluePrimary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Total Cost",
                value = "₹${totalCost.toInt()}",
                subtext = "Supplier & Fleet",
                icon = Icons.Default.MonetizationOn,
                accentColor = Color(0xFFD97706),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Net Profit",
                value = "₹${totalProfit.toInt()}",
                subtext = "$marginPct% Gross Margin",
                icon = Icons.Default.MonetizationOn,
                accentColor = Color(0xFF059669),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DUTY-BY-DUTY PROFITABILITY LEDGER",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(bookings) { b ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(b.id, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(b.customerName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            }
                            Text(
                                text = "Fare: ₹${b.totalCustomerAmount.toInt()} | Supplier Cost: ₹${b.supplierCost.toInt()}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${b.dutyProfit.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (b.dutyProfit > 0) Color(0xFF059669) else Color(0xFFDC2626)
                            )
                            Text(
                                text = if (b.totalCustomerAmount > 0) "${((b.dutyProfit / b.totalCustomerAmount) * 100).toInt()}% margin" else "0%",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
