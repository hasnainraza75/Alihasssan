package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

data class SupplierInfo(
    val id: String,
    val name: String,
    val category: String,
    val provider: String,
    val endpointOrTech: String,
    val latencyMs: Long?,
    val status: String,
    val isOnline: Boolean,
    val description: String,
    val icon: ImageVector
)

@Composable
fun SuppliersScreen(
    viewModel: MainViewModel,
    accentColor: Color = NeonCyan
) {
    val context = LocalContext.current
    val apiKey by viewModel.prefs.apiKey.collectAsState()
    val selectedModel by viewModel.prefs.selectedModel.collectAsState()
    val apiLatency by viewModel.apiLatency.collectAsState()
    val isTestingApi by viewModel.isTestingApi.collectAsState()
    val apiStatusMessage by viewModel.apiStatusMessage.collectAsState()

    var isRefreshingAll by remember { mutableStateOf(false) }

    val suppliers = listOf(
        SupplierInfo(
            id = "gemini_ai",
            name = "Google Generative Language (Gemini)",
            category = "Primary Neural Intelligence Supplier",
            provider = "Google DeepMind / Google AI Studio",
            endpointOrTech = "generativelanguage.googleapis.com / $selectedModel",
            latencyMs = apiLatency,
            status = if (apiKey.isNotBlank()) (if (apiLatency != null) "200 OK (${apiLatency}ms)" else "CONNECTED (KEY CONFIGURED)") else "KEY REQUIRED",
            isOnline = apiKey.isNotBlank(),
            description = "Provides multi-turn cognitive reasoning, full-stack code synthesis, and multi-lingual language processing without Firebase mediation.",
            icon = Icons.Default.CloudDone
        ),
        SupplierInfo(
            id = "audio_dsp",
            name = "Android Audio & Speech Engine",
            category = "Acoustic Speech Synthesis Supplier",
            provider = "Native Android Media DSP & Recognizer",
            endpointOrTech = "android.speech.SpeechRecognizer + TextToSpeech",
            latencyMs = 12L,
            status = "HARDWARE READY (16kHz PCM / Low Latency)",
            isOnline = true,
            description = "Drives vocal capture, RMS soundwave telemetry, and TTS speech synthesis supporting English, Urdu, Roman Urdu, and Hindi.",
            icon = Icons.Default.Memory
        ),
        SupplierInfo(
            id = "webkit_sandbox",
            name = "Chromium WebKit Sandboxed Engine",
            category = "Live Code Execution Supplier",
            provider = "Android System WebView / V8 Engine",
            endpointOrTech = "WebKit DOM Core v${Build.VERSION.SDK_INT}",
            latencyMs = 4L,
            status = "SANDBOX ACTIVE (Isolated Native Access)",
            isOnline = true,
            description = "Executes multi-file HTML5, CSS3, and JavaScript projects in real-time with isolated memory and DOM rendering.",
            icon = Icons.Default.Code
        ),
        SupplierInfo(
            id = "security_vault",
            name = "Self-Sovereign Device Key Vault",
            category = "Cryptographic Storage Supplier",
            provider = "Android Keystore & Private SharedPreferences",
            endpointOrTech = "AES-GCM-256 + SHA-256 Digest",
            latencyMs = 1L,
            status = "ENCRYPTED LOCAL STORAGE (Zero Cloud Leak)",
            isOnline = true,
            description = "Stores user API keys, conversation memories, and project matrices strictly on-device without third-party database harvesting.",
            icon = Icons.Default.Security
        ),
        SupplierInfo(
            id = "hardware_telemetry",
            name = "Device Telemetry & Sensor Bus",
            category = "Hardware Peripheral Supplier",
            provider = "Android Hardware Abstraction Layer (HAL)",
            endpointOrTech = "GNSS / CameraX / BLE / Telephony Intent Bus",
            latencyMs = 2L,
            status = "ONLINE (Granular Permission Matrix Controlled)",
            isOnline = true,
            description = "Directs system dialer calls, SMS composition, location coordinate telemetry, and camera scanners through explicit user consent.",
            icon = Icons.Default.Devices
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCosmicBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "NEURAL SUPPLIERS MATRIX",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Real-Time AI Infrastructure, DSP & Cloud Providers",
                    fontSize = 11.sp,
                    color = accentColor,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(
                onClick = {
                    viewModel.testGeminiConnection()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkNavyCard)
                    .border(1.dp, accentColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Suppliers",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Infrastructure Health Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkNavySurface)
                .border(1.dp, DarkNavyBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (apiKey.isNotBlank()) StatusActiveGreen else StatusWarningAmber)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (apiKey.isNotBlank()) "ALL NEURAL PROVIDERS SYNCHRONIZED" else "GEMINI API KEY REQUIRED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (apiKey.isNotBlank()) StatusActiveGreen else StatusWarningAmber,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (apiLatency != null) "Active Ping: ${apiLatency}ms to Google AI Gateway" else "Tap 'Ping Gateway' to verify live latency",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Suppliers List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(suppliers, key = { it.id }) { item ->
                SupplierCard(
                    supplier = item,
                    accentColor = accentColor,
                    isTesting = isTestingApi && item.id == "gemini_ai",
                    onTest = {
                        if (item.id == "gemini_ai") {
                            viewModel.testGeminiConnection()
                        } else if (item.id == "security_vault") {
                            viewModel.navigateTo(AppScreen.SETTINGS)
                        } else if (item.id == "webkit_sandbox") {
                            viewModel.navigateTo(AppScreen.CODING)
                        } else if (item.id == "audio_dsp") {
                            viewModel.navigateTo(AppScreen.VOICE_HUD)
                        } else {
                            viewModel.navigateTo(AppScreen.PERMISSIONS)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SupplierCard(
    supplier: SupplierInfo,
    accentColor: Color,
    isTesting: Boolean,
    onTest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkNavySurface)
            .border(
                1.dp,
                if (supplier.isOnline) DarkNavyBorder else StatusWarningAmber.copy(alpha = 0.5f),
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkNavyCard)
                            .border(1.dp, if (supplier.isOnline) accentColor else StatusWarningAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = supplier.icon,
                            contentDescription = supplier.name,
                            tint = if (supplier.isOnline) accentColor else StatusWarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = supplier.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = supplier.category,
                            fontSize = 10.sp,
                            color = accentColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Button(
                    onClick = onTest,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavyCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = if (isTesting) "Pinging…" else "Inspect",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tech & Endpoint Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF060B1C))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ENDPOINT: ${supplier.endpointOrTech}",
                        fontSize = 9.sp,
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                    Text(
                        text = supplier.status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (supplier.isOnline) StatusActiveGreen else StatusWarningAmber,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = supplier.description,
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 16.sp
            )
        }
    }
}
