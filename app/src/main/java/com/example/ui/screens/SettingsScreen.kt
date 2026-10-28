package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    accentColor: Color = NeonCyan
) {
    val context = LocalContext.current
    val apiKey by viewModel.prefs.apiKey.collectAsState()
    val selectedModel by viewModel.prefs.selectedModel.collectAsState()
    val voicePersona by viewModel.prefs.voicePersona.collectAsState()
    val voicePitch by viewModel.prefs.voicePitch.collectAsState()
    val voiceSpeed by viewModel.prefs.voiceSpeed.collectAsState()
    val selectedLanguage by viewModel.prefs.selectedLanguage.collectAsState()
    val themeAccent by viewModel.prefs.themeAccent.collectAsState()
    val edgeLighting by viewModel.prefs.edgeLighting.collectAsState()
    val username by viewModel.prefs.username.collectAsState()

    val apiStatusMessage by viewModel.apiStatusMessage.collectAsState()
    val isTestingApi by viewModel.isTestingApi.collectAsState()
    val apiLatency by viewModel.apiLatency.collectAsState()

    var inputKey by remember(apiKey) { mutableStateOf(apiKey) }
    var showPassword by remember { mutableStateOf(false) }
    var showModelDropdown by remember { mutableStateOf(false) }

    val models = listOf(
        "gemini-2.5-flash",
        "gemini-2.5-pro",
        "gemini-2.0-flash",
        "gemini-1.5-flash"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCosmicBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Section: Title
        Text(
            text = "LONA CONTROL CENTER",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Autonomous AI Settings, Neural Keys & Device Actions",
            fontSize = 11.sp,
            color = accentColor,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. GEMINI NEURAL GATEWAY (NO FIREBASE)
        SettingsSection(title = "GEMINI API CONFIGURATION", icon = Icons.Default.Key, accentColor = accentColor) {
            Column {
                Text(
                    text = "LONA AI connects directly to Google Generative Language REST APIs with zero Firebase mediation. Keys are encrypted in local device storage.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Official API Key Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0C1635))
                        .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/apikey"))
                            context.startActivity(browserIntent)
                        }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Obtain Gemini API Key", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text(text = "https://aistudio.google.com/apikey", fontSize = 10.sp, color = NeonCyan)
                    }
                    Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = "Open Link", tint = NeonCyan)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // API Key Input
                OutlinedTextField(
                    value = inputKey,
                    onValueChange = { inputKey = it },
                    label = { Text("Gemini API Key", fontSize = 12.sp) },
                    placeholder = { Text("AIzaSy…", color = TextDim) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Visibility",
                                tint = TextMuted
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = DarkNavyBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkNavyCard,
                        unfocusedContainerColor = DarkNavyCard
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Model Selector
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, DarkNavyBorder, RoundedCornerShape(8.dp))
                                .background(DarkNavyCard)
                                .clickable { showModelDropdown = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Model: $selectedModel",
                                fontSize = 11.sp,
                                color = accentColor,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        DropdownMenu(
                            expanded = showModelDropdown,
                            onDismissRequest = { showModelDropdown = false },
                            modifier = Modifier.background(DarkNavySurface)
                        ) {
                            models.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(text = m, color = if (selectedModel == m) accentColor else TextWhite, fontSize = 12.sp) },
                                    onClick = {
                                        viewModel.prefs.setSelectedModel(m)
                                        showModelDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Save Key Button
                    Button(
                        onClick = {
                            viewModel.prefs.setApiKey(inputKey)
                            Toast.makeText(context, "API Key saved locally", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Save Key", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ping / Connection Verification Test
                Button(
                    onClick = {
                        viewModel.prefs.setApiKey(inputKey)
                        viewModel.testGeminiConnection()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkNavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isTestingApi) "Pinging Neural Link…" else "⚡ Verify Gemini API Connection",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (apiStatusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkNavyCard)
                            .border(1.dp, if (apiLatency != null) StatusActiveGreen else StatusWarningAmber, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = apiStatusMessage ?: "",
                            fontSize = 11.sp,
                            color = if (apiLatency != null) StatusActiveGreen else StatusWarningAmber,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. VOICE & NEURAL PERSONA
        SettingsSection(title = "VOICE & NEURAL PERSONA", icon = Icons.Default.RecordVoiceOver, accentColor = accentColor) {
            Column {
                Text(text = "Persona Archetype", fontSize = 11.sp, color = TextMuted)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val personas = listOf("Female", "Male", "Korean Cyber")
                    personas.forEach { p ->
                        val isSel = voicePersona == p
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) DarkNavyCard else Color.Transparent)
                                .border(1.dp, if (isSel) accentColor else DarkNavyBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.prefs.setVoicePersona(p) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) accentColor else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pitch Slider
                Text(text = "Voice Pitch: ${String.format("%.1f", voicePitch)}x", fontSize = 11.sp, color = TextMuted)
                Slider(
                    value = voicePitch,
                    onValueChange = { viewModel.prefs.setVoicePitch(it) },
                    valueRange = 0.5f..1.8f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                // Speech Rate Slider
                Text(text = "Voice Speed: ${String.format("%.1f", voiceSpeed)}x", fontSize = 11.sp, color = TextMuted)
                Slider(
                    value = voiceSpeed,
                    onValueChange = { viewModel.prefs.setVoiceSpeed(it) },
                    valueRange = 0.6f..1.6f,
                    colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Test Speech Button
                Button(
                    onClick = {
                        viewModel.voiceManager.speak(
                            "Neural synthesizer calibrated. I am LONA AI, operating with optimal voice acoustics.",
                            pitch = voicePitch,
                            speed = voiceSpeed,
                            language = selectedLanguage
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkNavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "▶ Preview LONA Voice", fontSize = 12.sp, color = TextWhite)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. APPEARANCE & HOLOGRAPHIC THEME
        SettingsSection(title = "HOLOGRAPHIC AESTHETICS", icon = Icons.Default.Palette, accentColor = accentColor) {
            Column {
                Text(text = "Neon Cyberpunk Accent", fontSize = 11.sp, color = TextMuted)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val colors = listOf(
                        "cyan" to NeonCyan,
                        "purple" to NeonPurple,
                        "pink" to ElectricPink,
                        "blue" to ElectricBlue
                    )
                    colors.forEach { (name, color) ->
                        val isSel = themeAccent == name
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(2.dp, if (isSel) Color.White else Color.Transparent, CircleShape)
                                .clickable { viewModel.prefs.setThemeAccent(name) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSel) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Neon Edge Lighting", fontSize = 12.sp, color = TextWhite)
                        Text(text = "Displays subtle glowing border around screen", fontSize = 10.sp, color = TextDim)
                    }
                    Switch(
                        checked = edgeLighting,
                        onCheckedChange = { viewModel.prefs.setEdgeLighting(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = accentColor, checkedTrackColor = DarkNavyCard)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. PHONE ACTIONS
        SettingsSection(title = "DEVICE & PHONE ACTIONS", icon = Icons.Default.Call, accentColor = accentColor) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Dialer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkNavyCard)
                        .border(1.dp, DarkNavyBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL)
                            context.startActivity(intent)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = accentColor)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Launch System Phone Dialer", fontSize = 12.sp, color = TextWhite)
                        Text(text = "Open phone interface with user confirmation", fontSize = 10.sp, color = TextDim)
                    }
                }

                // SMS Composer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkNavyCard)
                        .border(1.dp, DarkNavyBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:")
                            }
                            context.startActivity(intent)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = NeonPurple)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Compose System SMS Message", fontSize = 12.sp, color = TextWhite)
                        Text(text = "Draft message in default messaging app", fontSize = 10.sp, color = TextDim)
                    }
                }

                // Android Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkNavyCard)
                        .border(1.dp, DarkNavyBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            context.startActivity(intent)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = ElectricPink)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Open Android System Settings", fontSize = 12.sp, color = TextWhite)
                        Text(text = "Configure connectivity, voice assistants, and roles", fontSize = 10.sp, color = TextDim)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. ABOUT LONA AI
        SettingsSection(title = "ABOUT LONA AI", icon = Icons.Default.Info, accentColor = accentColor) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "LONA AI — Futuristic Neural Assistant", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(text = "Version: 1.0.0 (Build 1)", fontSize = 11.sp, color = accentColor, fontFamily = FontFamily.Monospace)
                Text(text = "Package: com.lona.ai", fontSize = 11.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                Text(text = "Cloud Architecture: Zero Firebase (Self-Sovereign Device Local Storage & Direct Gemini REST)", fontSize = 11.sp, color = StatusActiveGreen)
                Text(text = "Created with Kotlin, Jetpack Compose, Material 3, and Android Modern Toolchain.", fontSize = 11.sp, color = TextDim)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkNavySurface)
            .border(1.dp, DarkNavyBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
