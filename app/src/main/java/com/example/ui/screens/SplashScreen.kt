package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.FuturisticBackground
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    accentColor: Color = NeonCyan,
    onEnterHome: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0.1f) }
    var bootText by remember { mutableStateOf("INITIALIZING QUANTUM CORE...") }

    val infiniteTransition = rememberInfiniteTransition(label = "LogoPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    LaunchedEffect(Unit) {
        delay(600)
        progress = 0.35f
        bootText = "CALIBRATING NEURAL SYNAPSE..."
        delay(700)
        progress = 0.70f
        bootText = "CONNECTING GEMINI NEURAL GATEWAY..."
        delay(700)
        progress = 1.0f
        bootText = "LONA AI QUANTUM ENGINE ONLINE"
        delay(600)
        onEnterHome()
    }

    FuturisticBackground(accentColor = accentColor) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Glowing Circular LONA AI Logo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale)
                ) {
                    // Outer Glow Halo
                    Box(
                        modifier = Modifier
                            .size(156.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        accentColor.copy(alpha = 0.35f),
                                        NeonPurple.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Logo Circular Image
                    Image(
                        painter = painterResource(id = R.drawable.lona_logo),
                        contentDescription = "LONA AI Logo",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(2.dp, accentColor, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = "LONA AI",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp,
                    color = TextWhite,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "FUTURISTIC NEURAL ASSISTANT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.sp,
                    color = accentColor,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Loading bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(DarkNavyCard)
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = accentColor,
                        trackColor = DarkNavySurface,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Boot log typewriter text
                Text(
                    text = bootText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Skip / Launch Core Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .background(Color(0x3300F5D4))
                        .clickable { onEnterHome() }
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "LAUNCH CORE ➔",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
