package com.example.ui.screens.fleet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DriverEntity
import com.example.ui.components.DutyStatusBadge
import com.example.ui.theme.*

@Composable
fun LiveTrackingMapScreen(
    drivers: List<DriverEntity>,
    isSimulationActive: Boolean,
    onToggleSimulation: () -> Unit,
    onSelectDriver: (String) -> Unit
) {
    val activeDrivers = drivers.filter { it.status == "On Duty" || it.status == "Assigned" }
    var selectedDriverId by remember { mutableStateOf(activeDrivers.firstOrNull()?.id ?: drivers.firstOrNull()?.id) }
    val selectedDriver = drivers.firstOrNull { it.id == selectedDriverId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Map Radar Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Live GPS Fleet Radar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${activeDrivers.size} active vehicles transmitting telemetry",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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
                        text = if (isSimulationActive) "SIMULATING GPS" else "GPS PAUSED",
                        color = if (isSimulationActive) Color(0xFFA7F3D0) else Color(0xFFFDE68A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Vehicle Filter Strip
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(activeDrivers) { drv ->
                val isSelected = drv.id == selectedDriverId
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedDriverId = drv.id },
                    label = {
                        Text(
                            text = "${drv.assignedVehicleReg} (${drv.name.split(" ").first()})",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (drv.currentSpeed > 0) Color(0xFF10B981) else Color(0xFF3B82F6))
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Map Canvas Area
        Card(
            colors = CardDefaults.cardColors(containerColor = FleetNavy900),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                // Cycle drivers when clicking on map
                                val nextDriver = activeDrivers.firstOrNull { it.id != selectedDriverId }
                                if (nextDriver != null) {
                                    selectedDriverId = nextDriver.id
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // Draw stylized city road network
                    val gridColor = Color(0xFF1B2A4A)
                    val highwayColor = Color(0xFF2E4069)
                    val activeRouteColor = FleetCyanAccent

                    // Arterial highway curves (e.g. Airport Expressway, Ring Road)
                    val highwayPath = Path().apply {
                        moveTo(w * 0.1f, h * 0.9f)
                        cubicTo(w * 0.35f, h * 0.75f, w * 0.6f, h * 0.4f, w * 0.85f, h * 0.15f)
                    }
                    drawPath(
                        path = highwayPath,
                        color = highwayColor,
                        style = Stroke(width = 12.dp.toPx())
                    )

                    // Secondary Outer Ring Road
                    val ringPath = Path().apply {
                        moveTo(w * 0.2f, h * 0.3f)
                        cubicTo(w * 0.5f, h * 0.55f, w * 0.8f, h * 0.5f, w * 0.95f, h * 0.8f)
                    }
                    drawPath(
                        path = ringPath,
                        color = gridColor,
                        style = Stroke(width = 6.dp.toPx())
                    )

                    // Draw city grid lines
                    for (i in 1..6) {
                        drawLine(
                            color = gridColor.copy(alpha = 0.4f),
                            start = Offset(0f, h * (i / 7f)),
                            end = Offset(w, h * (i / 7f)),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = gridColor.copy(alpha = 0.4f),
                            start = Offset(w * (i / 7f), 0f),
                            end = Offset(w * (i / 7f), h),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Active corridor route for selected driver
                    val corridorPath = Path().apply {
                        moveTo(w * 0.3f, h * 0.7f)
                        lineTo(w * 0.55f, h * 0.45f)
                        lineTo(w * 0.8f, h * 0.2f)
                    }
                    drawPath(
                        path = corridorPath,
                        color = activeRouteColor.copy(alpha = 0.6f),
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Pickup point marker (Green Pin)
                    drawCircle(
                        color = Color(0xFF10B981),
                        radius = 8.dp.toPx(),
                        center = Offset(w * 0.3f, h * 0.7f)
                    )
                    // Drop point marker (Red Pin)
                    drawCircle(
                        color = Color(0xFFEF4444),
                        radius = 8.dp.toPx(),
                        center = Offset(w * 0.8f, h * 0.2f)
                    )

                    // Render driver vehicle markers
                    activeDrivers.forEachIndexed { idx, drv ->
                        val isSelected = drv.id == selectedDriverId
                        val posX = when (idx % 3) {
                            0 -> w * 0.55f
                            1 -> w * 0.75f
                            else -> w * 0.4f
                        }
                        val posY = when (idx % 3) {
                            0 -> h * 0.45f
                            1 -> h * 0.25f
                            else -> h * 0.6f
                        }

                        // Pulse ring around selected driver
                        if (isSelected) {
                            drawCircle(
                                color = FleetCyanAccent.copy(alpha = 0.3f),
                                radius = 22.dp.toPx(),
                                center = Offset(posX, posY)
                            )
                        }

                        // Marker circle
                        drawCircle(
                            color = if (isSelected) FleetCyanAccent else Color.White,
                            radius = 12.dp.toPx(),
                            center = Offset(posX, posY)
                        )
                        drawCircle(
                            color = FleetNavy900,
                            radius = 9.dp.toPx(),
                            center = Offset(posX, posY)
                        )
                    }
                }

                // Map Legend Overlay
                Surface(
                    color = FleetNavy800.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pickup Origin", color = Color.White, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Airport Drop / Destination", color = Color.White, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(FleetCyanAccent))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chauffeur Telemetry Marker", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Driver Telemetry Telematics Bottom Card
        if (selectedDriver != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${selectedDriver.name} (${selectedDriver.id})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Vehicle: ${selectedDriver.assignedVehicleReg} • ${selectedDriver.assignedBranch}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DutyStatusBadge(status = selectedDriver.status)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Speed, Battery, Geofence telemetry row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("CURRENT SPEED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("${selectedDriver.currentSpeed.toInt()} km/h", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("BATTERY LEVEL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                Text("${selectedDriver.batteryPct}%", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("GEOFENCE STATUS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                Text("Inside Corridor", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
