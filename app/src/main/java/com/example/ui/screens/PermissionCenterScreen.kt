package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.PermissionItem
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun PermissionCenterScreen(
    viewModel: MainViewModel,
    accentColor: Color = NeonCyan
) {
    val context = LocalContext.current
    val permissions by viewModel.permissionsList.collectAsState()

    var requestedPermissionId by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        viewModel.refreshPermissions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCosmicBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "SYSTEM PERMISSION MATRIX",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "15 Granular Android Capability Categories",
                    fontSize = 11.sp,
                    color = accentColor,
                    fontFamily = FontFamily.Monospace
                )
            }

            OutlinedButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
            ) {
                Text(text = "App Settings", fontSize = 11.sp, color = accentColor, fontFamily = FontFamily.Monospace)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Security Notice Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkNavyCard)
                .border(1.dp, DarkNavyBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "LONA AI enforces zero-trust, least-privilege security. Permissions are activated strictly on demand for user-initiated tasks. Private telemetry remains device-local.",
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Permission Items List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(permissions, key = { it.id }) { item ->
                PermissionCard(
                    item = item,
                    accentColor = accentColor,
                    onRequest = {
                        if (item.permissionManifest != null) {
                            requestedPermissionId = item.id
                            permissionLauncher.launch(item.permissionManifest)
                        } else if (item.settingsAction != null) {
                            val intent = Intent(item.settingsAction)
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                val fallback = Intent(Settings.ACTION_SETTINGS)
                                context.startActivity(fallback)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PermissionCard(
    item: PermissionItem,
    accentColor: Color,
    onRequest: () -> Unit
) {
    val icon = getPermissionIcon(item.id)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkNavySurface)
            .border(
                1.dp,
                if (item.isGranted) StatusActiveGreen.copy(alpha = 0.35f) else DarkNavyBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkNavyCard)
                            .border(1.dp, if (item.isGranted) StatusActiveGreen else DarkNavyBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = item.title,
                            tint = if (item.isGranted) StatusActiveGreen else accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (item.isGranted) "GRANTED" else if (item.isRestrictedOrSpecial) "SYSTEM SETTING" else "NOT GRANTED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (item.isGranted) StatusActiveGreen else StatusWarningAmber,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Grant / Configure Action Button
                if (!item.isGranted) {
                    Button(
                        onClick = onRequest,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.isRestrictedOrSpecial) NeonPurple else accentColor
                        ),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (item.isRestrictedOrSpecial) "Configure" else "Grant",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Granted",
                            tint = StatusActiveGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Active",
                            fontSize = 11.sp,
                            color = StatusActiveGreen,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 16.sp
            )
        }
    }
}

fun getPermissionIcon(id: String): ImageVector {
    return when (id) {
        "mic" -> Icons.Default.Mic
        "camera" -> Icons.Default.CameraAlt
        "location" -> Icons.Default.MyLocation
        "contacts" -> Icons.Default.Contacts
        "call_logs" -> Icons.Default.Call
        "phone" -> Icons.Default.Phone
        "notifications" -> Icons.Default.Notifications
        "sms" -> Icons.Default.Sms
        "audio" -> Icons.Default.Headphones
        "photos" -> Icons.Default.PhotoLibrary
        "nearby" -> Icons.Default.Bluetooth
        "files" -> Icons.Default.Folder
        "accessibility" -> Icons.Default.Accessibility
        "default_assistant" -> Icons.Default.Assistant
        "background_service" -> Icons.Default.BatteryAlert
        else -> Icons.Default.Warning
    }
}
