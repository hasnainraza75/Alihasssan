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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RotatingGlobe(
    modifier: Modifier = Modifier,
    accentColor: Color = NeonCyan,
    isPulsing: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlobeRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Angle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPulsing) 1.08f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.minDimension / 2.3f) * pulseScale

            // Atmospheric Outer Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.28f),
                        NeonPurple.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.35f
                ),
                radius = radius * 1.35f,
                center = center
            )

            // Outer Sphere Ring
            drawCircle(
                color = accentColor.copy(alpha = 0.65f),
                radius = radius,
                center = center,
                style = Stroke(width = 2.5f)
            )

            // Inner Sphere Shading
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x00050814),
                        Color(0xAA070B19),
                        Color(0xFF070B19)
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            val radRot = Math.toRadians(rotationAngle.toDouble())

            // Draw Longitude Lines (Meridians) with 3D Y-axis rotation
            val meridians = 8
            for (i in 0 until meridians) {
                val baseLon = (i * (360.0 / meridians))
                val currentLon = Math.toRadians(baseLon) + radRot
                val widthFactor = cos(currentLon).toFloat()
                val isFront = sin(currentLon) >= 0

                val alpha = if (isFront) 0.55f else 0.15f
                val strokeWidth = if (isFront) 1.8f else 1.0f

                drawOval(
                    color = accentColor.copy(alpha = alpha),
                    topLeft = Offset(center.x - radius * Math.abs(widthFactor), center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2f * Math.abs(widthFactor), radius * 2f),
                    style = Stroke(width = strokeWidth)
                )
            }

            // Draw Latitude Parallels
            val latitudes = listOf(-0.6f, -0.3f, 0f, 0.3f, 0.6f)
            for (latFrac in latitudes) {
                val yOffset = radius * latFrac
                val latRadius = (radius * cos(Math.asin(latFrac.toDouble()))).toFloat()
                drawOval(
                    color = NeonPurple.copy(alpha = 0.35f),
                    topLeft = Offset(center.x - latRadius, center.y + yOffset - (latRadius * 0.2f)),
                    size = androidx.compose.ui.geometry.Size(latRadius * 2f, latRadius * 0.4f),
                    style = Stroke(width = 1.2f)
                )
            }

            // Draw Procedural Glowing Continent Nodes
            val pointLatLons = listOf(
                Pair(35.0, -100.0), Pair(40.0, -80.0), Pair(50.0, -110.0), // North America
                Pair(-15.0, -55.0), Pair(-25.0, -45.0), // South America
                Pair(50.0, 10.0), Pair(45.0, 25.0), Pair(55.0, 35.0), // Europe
                Pair(10.0, 20.0), Pair(-10.0, 30.0), Pair(25.0, 30.0), // Africa
                Pair(30.0, 70.0), Pair(35.0, 105.0), Pair(20.0, 80.0), Pair(40.0, 120.0), // Asia
                Pair(-25.0, 135.0), Pair(-30.0, 150.0), // Australia
                Pair(25.0, 45.0), Pair(60.0, 100.0), Pair(15.0, 100.0)
            )

            for ((latDeg, lonDeg) in pointLatLons) {
                val latRad = Math.toRadians(latDeg)
                val lonRad = Math.toRadians(lonDeg) + radRot

                val x3d = radius * cos(latRad) * sin(lonRad)
                val y3d = -radius * sin(latRad)
                val z3d = radius * cos(latRad) * cos(lonRad)

                // Only draw points facing forward in 3D
                if (z3d > 0) {
                    val ptCenter = Offset(center.x + x3d.toFloat(), center.y + y3d.toFloat())
                    val nodeAlpha = (z3d / radius).toFloat().coerceIn(0.2f, 1f)
                    drawCircle(
                        color = accentColor.copy(alpha = nodeAlpha),
                        radius = 3.5f * (z3d / radius).toFloat() + 1f,
                        center = ptCenter
                    )
                    drawCircle(
                        color = ElectricPink.copy(alpha = nodeAlpha * 0.5f),
                        radius = 7.0f * (z3d / radius).toFloat() + 2f,
                        center = ptCenter,
                        style = Stroke(width = 1f)
                    )
                }
            }

            // Orbital Ring with Satellites
            val orbitRadius = radius * 1.25f
            drawOval(
                color = ElectricPink.copy(alpha = 0.35f),
                topLeft = Offset(center.x - orbitRadius, center.y - (orbitRadius * 0.35f)),
                size = androidx.compose.ui.geometry.Size(orbitRadius * 2f, orbitRadius * 0.7f),
                style = Stroke(width = 1.2f)
            )

            // Orbiting Quantum Satellite
            val satAngle = radRot * 2.2
            val satX = center.x + (orbitRadius * cos(satAngle)).toFloat()
            val satY = center.y + ((orbitRadius * 0.35f) * sin(satAngle)).toFloat()
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = Offset(satX, satY)
            )
            drawCircle(
                color = ElectricPink,
                radius = 8f,
                center = Offset(satX, satY),
                style = Stroke(width = 1.5f)
            )
        }
    }
}
