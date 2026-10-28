package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.VoiceState
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.GlowingMicButton
import com.example.ui.components.RotatingGlobe
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    accentColor: Color = NeonCyan
) {
    val voiceState by viewModel.voiceManager.voiceState.collectAsState()
    val spokenText by viewModel.voiceManager.spokenText.collectAsState()
    val audioRms by viewModel.voiceManager.audioRms.collectAsState()
    val errorMessage by viewModel.voiceManager.errorMessage.collectAsState()
    val selectedLanguage by viewModel.prefs.selectedLanguage.collectAsState()
    val apiKey by viewModel.prefs.apiKey.collectAsState()

    var showLangMenu by remember { mutableStateOf(false) }

    val languages = listOf(
        "en" to "English (US)",
        "ur" to "Urdu (اردو)",
        "roman_ur" to "Roman Urdu",
        "hi" to "Hindi (हिंदी)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.lona_logo),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, accentColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "LONA AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (apiKey.isNotBlank()) StatusActiveGreen else StatusWarningAmber)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (apiKey.isNotBlank()) "NEURAL CORE ACTIVE" else "SET API KEY",
                            fontSize = 9.sp,
                            color = if (apiKey.isNotBlank()) StatusActiveGreen else StatusWarningAmber,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language Selector Chip
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, DarkNavyBorder, RoundedCornerShape(16.dp))
                            .background(DarkNavyCard)
                            .clickable { showLangMenu = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedLanguage.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    DropdownMenu(
                        expanded = showLangMenu,
                        onDismissRequest = { showLangMenu = false },
                        modifier = Modifier.background(DarkNavySurface)
                    ) {
                        languages.forEach { (code, label) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = label,
                                        color = if (selectedLanguage == code) accentColor else TextWhite,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    viewModel.prefs.setSelectedLanguage(code)
                                    showLangMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(onClick = { viewModel.navigateTo(AppScreen.SETTINGS) }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Central Holographic Rotating 3D Globe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentAlignment = Alignment.Center
        ) {
            RotatingGlobe(
                modifier = Modifier.fillMaxSize(),
                accentColor = accentColor,
                isPulsing = voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status Indicator Badge
        val statusText = when (voiceState) {
            VoiceState.LISTENING -> "● LISTENING TO SPEECH FREQUENCY…"
            VoiceState.THINKING -> "● QUANTUM CORE SYNAPSE PROCESSING…"
            VoiceState.SPEAKING -> "● LONA VOCAL SYNTHESIZER ACTIVE…"
            VoiceState.ERROR -> "● SYNAPSE INTERRUPTED"
            VoiceState.IDLE -> if (apiKey.isNotBlank()) "READY // TAP MIC TO TALK" else "WARNING // CONFIGURE GEMINI API KEY"
        }

        val statusColor = when (voiceState) {
            VoiceState.LISTENING -> NeonCyan
            VoiceState.THINKING -> NeonPurple
            VoiceState.SPEAKING -> ElectricPink
            VoiceState.ERROR -> StatusErrorRed
            VoiceState.IDLE -> if (apiKey.isNotBlank()) accentColor else StatusWarningAmber
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .background(DarkNavyCard.copy(alpha = 0.8f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Microphone Button & Stop Action
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            GlowingMicButton(
                voiceState = voiceState,
                audioRms = audioRms,
                accentColor = accentColor,
                onClick = { viewModel.toggleVoiceListening() }
            )

            AnimatedVisibility(
                visible = voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(StatusErrorRed.copy(alpha = 0.2f))
                        .border(1.5.dp, StatusErrorRed, CircleShape)
                        .clickable { viewModel.stopVoice() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = StatusErrorRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Real-time voice transcript / error display
        AnimatedVisibility(visible = spokenText.isNotBlank() || errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkNavyCard)
                    .border(1.dp, DarkNavyBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = if (errorMessage != null) "Notice: $errorMessage" else "\"$spokenText\"",
                    fontSize = 13.sp,
                    color = if (errorMessage != null) StatusErrorRed else TextWhite,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Suggestion Chips
        Text(
            text = "QUICK NEURAL DIRECTIVES",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val suggestions = listOf(
                "Suppliers & AI Ecosystem" to {
                    viewModel.navigateTo(AppScreen.SUPPLIERS)
                },
                "Build Cyberpunk Login Page" to {
                    viewModel.navigateTo(AppScreen.CODING)
                },
                "Open Voice Hologram HUD" to {
                    viewModel.navigateTo(AppScreen.VOICE_HUD)
                },
                "System Permission Matrix" to {
                    viewModel.navigateTo(AppScreen.PERMISSIONS)
                },
                "Ping Gemini Neural Link" to {
                    viewModel.testGeminiConnection()
                    viewModel.navigateTo(AppScreen.SETTINGS)
                },
                "Ask LONA Quantum Theory" to {
                    viewModel.navigateTo(AppScreen.CHAT)
                    viewModel.sendUserMessage("Explain Quantum Computing and its application to AI in 3 concise bullet points.")
                }
            )

            suggestions.forEach { (label, action) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, DarkNavyBorder, RoundedCornerShape(16.dp))
                        .background(DarkNavyCard)
                        .clickable { action() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "⚡ $label",
                        fontSize = 11.sp,
                        color = TextWhite,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
