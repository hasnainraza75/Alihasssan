package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.audio.VoiceState
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusErrorRed

@Composable
fun GlowingMicButton(
    voiceState: VoiceState,
    audioRms: Float = 0f,
    accentColor: Color = NeonCyan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MicHalo")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = when (voiceState) {
            VoiceState.LISTENING -> 1.35f + (audioRms * 0.05f).coerceAtMost(0.25f)
            VoiceState.SPEAKING -> 1.25f
            VoiceState.THINKING -> 1.15f
            else -> 1.08f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (voiceState) {
                    VoiceState.LISTENING -> 600
                    VoiceState.THINKING -> 800
                    else -> 1800
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloAlpha"
    )

    val activeColor = when (voiceState) {
        VoiceState.LISTENING -> NeonCyan
        VoiceState.THINKING -> NeonPurple
        VoiceState.SPEAKING -> ElectricPink
        VoiceState.ERROR -> StatusErrorRed
        VoiceState.IDLE -> accentColor
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(100.dp)
    ) {
        // Outer Animated Glow Waves
        Box(
            modifier = Modifier
                .size(92.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            activeColor.copy(alpha = haloAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        // Middle Pulsing Ring
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .border(2.dp, activeColor.copy(alpha = 0.6f), CircleShape)
        )

        // Center Button Core
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DarkNavySurface,
                            DeepCosmicBlack
                        )
                    )
                )
                .border(2.dp, activeColor, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        ) {
            val icon = when (voiceState) {
                VoiceState.LISTENING, VoiceState.SPEAKING -> Icons.Default.Stop
                VoiceState.ERROR -> Icons.Default.MicOff
                else -> Icons.Default.Mic
            }

            Icon(
                imageVector = icon,
                contentDescription = "Voice Interaction",
                tint = activeColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
