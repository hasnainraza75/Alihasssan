package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

@Composable
fun FuturisticBackground(
    modifier: Modifier = Modifier,
    accentColor: Color = NeonCyan,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Dark Radial Space
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0C142E),
                        DeepCosmicBlack,
                        Color(0xFF03050D)
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.4f),
                    radius = size.maxDimension * 0.8f
                )
            )

            // Deep Space Ambient Stars / Particle Grid
            val starCount = 45
            for (i in 0 until starCount) {
                val seed = i * 1337
                val x = (Math.sin(seed.toDouble()) * 0.5 + 0.5).toFloat() * size.width
                val y = (Math.cos((seed * 3).toDouble()) * 0.5 + 0.5).toFloat() * size.height
                val starAlpha = (Math.sin((seed * 7).toDouble()) * 0.35 + 0.45).toFloat()
                drawCircle(
                    color = if (i % 3 == 0) accentColor.copy(alpha = starAlpha) else NeonPurple.copy(alpha = starAlpha * 0.8f),
                    radius = if (i % 5 == 0) 2.2f else 1.2f,
                    center = Offset(x, y)
                )
            }
        }
        content()
    }
}

@Composable
fun EdgeGlow(
    enabled: Boolean = true,
    accentColor: Color = NeonCyan,
    modifier: Modifier = Modifier
) {
    if (!enabled) return

    val infiniteTransition = rememberInfiniteTransition(label = "EdgeGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "EdgeAlpha"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    accentColor.copy(alpha = glowAlpha),
                    NeonPurple.copy(alpha = glowAlpha * 0.7f),
                    accentColor.copy(alpha = glowAlpha)
                )
            ),
            size = Size(size.width, size.height),
            style = Stroke(width = 3.5f)
        )
    }
}
