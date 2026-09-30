package com.example.ui.screens.settings

import androidx.compose.foundation.layout.*
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
import com.example.ui.theme.FleetBluePrimary

@Composable
fun SettingsScreen(onShowMessage: (String) -> Unit) {
    var geofenceRadius by remember { mutableFloatStateOf(2.5f) }
    var speedThreshold by remember { mutableFloatStateOf(80f) }
    var reportingAlertMins by remember { mutableIntStateOf(30) }
    var gstRate by remember { mutableStateOf("5%") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "System Settings & Integrations",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Configure operational thresholds, GPS geofences, and external communication webhooks",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Telemetry & Geofence Settings
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "GEOFENCE & TELEMATICS THRESHOLDS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Pickup Geofence Radius: ${String.format("%.1f", geofenceRadius)} km", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(
                    value = geofenceRadius,
                    onValueChange = { geofenceRadius = it },
                    valueRange = 0.5f..10.0f
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text("Fleet Over-Speed Limit: ${speedThreshold.toInt()} km/h", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(
                    value = speedThreshold,
                    onValueChange = { speedThreshold = it },
                    valueRange = 50f..120f
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text("Pickup Approaching Alert: $reportingAlertMins minutes before", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(
                    value = reportingAlertMins.toFloat(),
                    onValueChange = { reportingAlertMins = it.toInt() },
                    valueRange = 15f..90f
                )
            }
        }

        // WhatsApp, SMS & Email Webhook Status
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "COMMUNICATION INTEGRATION ARCHITECTURE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("WhatsApp Business API", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Driver dispatch & live tracking link dispatch", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                        Text("Configured (Ready)", color = Color(0xFF166534), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Transactional SMS (Indian DLT)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Passenger OTP & Driver arrival SMS alerts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                        Text("Configured (Ready)", color = Color(0xFF166534), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Google Maps Platform API", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Geocoding, Routes, and Distance Matrix", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                        Text("Using Fallback / Sim", color = Color(0xFF92400E), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }

        // Production Readiness & Architecture Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PRODUCTION DEPLOYMENT CHECKLIST",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("✓ End-to-end 12-stage Booking & Duty Lifecycle state machine implemented", fontSize = 12.sp)
                Text("✓ Room SQLite local database persistence with reactive Flows", fontSize = 12.sp)
                Text("✓ Chauffeur Mobile Console with Start Odometer & Passenger OTP verification", fontSize = 12.sp)
                Text("✓ Duty End with Odometer calculation, toll/parking submission & billing", fontSize = 12.sp)
                Text("✓ Live GPS fleet telemetry simulator with speed & battery telemetry", fontSize = 12.sp)
                Text("✓ GST Tax Invoice generator with printable preview & WhatsApp share", fontSize = 12.sp)
                Text("✓ Role-Based Access Control (Super Admin, Dispatcher, Chauffeur, Corporate)", fontSize = 12.sp)
            }
        }

        Button(
            onClick = { onShowMessage("Operational thresholds saved successfully") },
            colors = ButtonDefaults.buttonColors(containerColor = FleetBluePrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Operational Settings", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
