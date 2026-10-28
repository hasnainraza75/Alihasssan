package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VoiceState
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.GlowingMicButton
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VoiceHudModal(
    viewModel: MainViewModel,
    accentColor: Color = NeonCyan
) {
    val voiceState by viewModel.voiceManager.voiceState.collectAsState()
    val spokenText by viewModel.voiceManager.spokenText.collectAsState()
    val audioRms by viewModel.voiceManager.audioRms.collectAsState()
    val errorMessage by viewModel.voiceManager.errorMessage.collectAsState()
    val voicePersona by viewModel.prefs.voicePersona.collectAsState()
    val selectedLanguage by viewModel.prefs.selectedLanguage.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "HudRings")
    val rotAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rot"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCosmicBlack.copy(alpha = 0.96f))
    ) {
        // Holographic Cybernetic Background HUD
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            val baseRadius = size.width * 0.38f

            // Concentric Glowing Tech Circles
            drawCircle(
                color = accentColor.copy(alpha = 0.15f),
                radius = baseRadius * 1.3f,
                center = center,
                style = Stroke(width = 1.2f)
            )
            drawCircle(
                color = NeonPurple.copy(alpha = 0.25f),
                radius = baseRadius,
                center = center,
                style = Stroke(width = 2f)
            )

            // Orbital Tick Marks
            val rad = Math.toRadians(rotAngle.toDouble())
            val ticks = 24
            for (i in 0 until ticks) {
                val angle = (i * (2 * Math.PI / ticks)) + rad
                val r1 = baseRadius - 10f
                val r2 = baseRadius + 10f
                val p1 = Offset(center.x + (r1 * cos(angle)).toFloat(), center.y + (r1 * sin(angle)).toFloat())
                val p2 = Offset(center.x + (r2 * cos(angle)).toFloat(), center.y + (r2 * sin(angle)).toFloat())
                drawLine(
                    color = accentColor.copy(alpha = 0.4f),
                    start = p1,
                    end = p2,
                    strokeWidth = 1.8f
                )
            }

            // Real-time Soundwave Amplitude Bars
            if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) {
                val barCount = 18
                for (b in 0 until barCount) {
                    val barAngle = (b * (2 * Math.PI / barCount)) + (rad * 0.5)
                    val heightAmp = (audioRms * 8f) + (Math.sin(b * 0.8 + rad * 3) * 15f).toFloat()
                    val outerR = baseRadius * 1.15f + heightAmp.coerceAtLeast(0f)
                    val pStart = Offset(center.x + (baseRadius * 1.12f * cos(barAngle)).toFloat(), center.y + (baseRadius * 1.12f * sin(barAngle)).toFloat())
                    val pEnd = Offset(center.x + (outerR * cos(barAngle)).toFloat(), center.y + (outerR * sin(barAngle)).toFloat())
                    drawLine(
                        color = if (voiceState == VoiceState.LISTENING) NeonCyan else ElectricPink,
                        start = pStart,
                        end = pEnd,
                        strokeWidth = 3f
                    )
                }
            }
        }

        // Top Bar Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LONA NEURAL HUD",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    letterSpacing = 3.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "PERSONA: ${voicePersona.uppercase()} // ${selectedLanguage.uppercase()}",
                    fontSize = 10.sp,
                    color = accentColor,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkNavySurface)
                    .border(1.dp, DarkNavyBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close HUD",
                    tint = TextWhite
                )
            }
        }

        // Center Voice Controller
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Live Spoken Transcription Display
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 30.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkNavyCard.copy(alpha = 0.85f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val hudStatus = when (voiceState) {
                        VoiceState.LISTENING -> "LIVE SPEECH CAPTURE"
                        VoiceState.THINKING -> "QUANTUM REASONING…"
                        VoiceState.SPEAKING -> "TRANSMITTING RESPONSE"
                        VoiceState.ERROR -> "SYSTEM ERROR"
                        VoiceState.IDLE -> "TAP MICROPHONE TO SPEAK"
                    }
                    Text(
                        text = "● $hudStatus",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = when {
                            errorMessage != null -> "Notice: $errorMessage"
                            spokenText.isNotBlank() -> "\"$spokenText\""
                            voiceState == VoiceState.LISTENING -> "Listening to your frequency in ${selectedLanguage.uppercase()}…"
                            else -> "Ask anything in English, Urdu, Roman Urdu, or Hindi."
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (errorMessage != null) StatusErrorRed else TextWhite,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Persona Quick Selector Pills
            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val personas = listOf("Female", "Male", "Korean Cyber")
                personas.forEach { persona ->
                    val isSelected = voicePersona == persona
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, if (isSelected) accentColor else DarkNavyBorder, RoundedCornerShape(12.dp))
                            .background(if (isSelected) DarkNavyCard else Color.Transparent)
                            .clickable { viewModel.prefs.setVoicePersona(persona) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = persona,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) accentColor else TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Big Glowing Mic
            GlowingMicButton(
                voiceState = voiceState,
                audioRms = audioRms,
                accentColor = accentColor,
                onClick = { viewModel.toggleVoiceListening() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stop Control
            if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, StatusErrorRed, RoundedCornerShape(20.dp))
                        .background(StatusErrorRed.copy(alpha = 0.15f))
                        .clickable { viewModel.stopVoice() }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "■ TERMINATE AUDIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusErrorRed,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
