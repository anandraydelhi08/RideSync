package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DutyStatus
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun DutyStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon) = when (status.uppercase()) {
        "BOOKED" -> Triple(StatusBooked.copy(alpha = 0.15f), StatusBooked, Icons.Default.Event)
        "CONFIRMED" -> Triple(StatusConfirmed.copy(alpha = 0.15f), StatusConfirmed, Icons.Default.CheckCircle)
        "ALLOTTED" -> Triple(StatusAllotted.copy(alpha = 0.15f), StatusAllotted, Icons.Default.PersonAdd)
        "DRIVER_ACCEPTED" -> Triple(StatusAccepted.copy(alpha = 0.15f), StatusAccepted, Icons.Default.ThumbUp)
        "DISPATCHED" -> Triple(StatusDispatched.copy(alpha = 0.15f), StatusDispatched, Icons.Default.Send)
        "ENROUTE" -> Triple(StatusEnroute.copy(alpha = 0.15f), StatusEnroute, Icons.Default.DirectionsCar)
        "ARRIVED" -> Triple(StatusArrived.copy(alpha = 0.15f), StatusArrived, Icons.Default.LocationOn)
        "STARTED", "IN_PROGRESS" -> Triple(StatusInProgress.copy(alpha = 0.15f), StatusInProgress, Icons.Default.Navigation)
        "COMPLETED" -> Triple(StatusCompleted.copy(alpha = 0.15f), StatusCompleted, Icons.Default.DoneAll)
        "CLOSED" -> Triple(StatusClosed.copy(alpha = 0.15f), StatusClosed, Icons.Default.Lock)
        "CANCELLED", "REJECTED" -> Triple(StatusAlert.copy(alpha = 0.15f), StatusAlert, Icons.Default.Cancel)
        "NO_SHOW" -> Triple(StatusWarning.copy(alpha = 0.15f), StatusWarning, Icons.Default.PersonOff)
        else -> Triple(Color.LightGray.copy(alpha = 0.2f), Color.DarkGray, Icons.Default.Info)
    }

    val displayLabel = try {
        DutyStatus.valueOf(status).label
    } catch (e: Exception) {
        status.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = displayLabel,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtext: String? = null,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtext != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun RoleSwitcherBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = FleetNavy900,
        contentColor = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(FleetCyanAccent)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ACTIVE ROLE:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FleetCyanAccent,
                    letterSpacing = 0.5.sp
                )
            }

            var expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

            Box {
                Surface(
                    onClick = { expanded.value = true },
                    shape = RoundedCornerShape(8.dp),
                    color = FleetNavy700,
                    modifier = Modifier.height(30.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentRole.displayName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    UserRole.entries.forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = role.displayName,
                                    fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal,
                                    color = if (role == currentRole) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onRoleSelected(role)
                                expanded.value = false
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(role.badgeColorHex))
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
