package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.EdgeGlow
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CodingWorkspaceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PermissionCenterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SuppliersScreen
import com.example.ui.screens.VoiceHudModal
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.LonaTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDim

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeAccentName by viewModel.prefs.themeAccent.collectAsState()
            val edgeLightingEnabled by viewModel.prefs.edgeLighting.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()

            val accentColor = when (themeAccentName) {
                "purple" -> NeonPurple
                "pink" -> ElectricPink
                "blue" -> ElectricBlue
                else -> NeonCyan
            }

            LonaTheme(accentColor = accentColor) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepCosmicBlack)
                ) {
                    if (currentScreen == AppScreen.SPLASH) {
                        SplashScreen(
                            accentColor = accentColor,
                            onEnterHome = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                    } else {
                        // Handle back navigation
                        BackHandler(enabled = currentScreen != AppScreen.HOME) {
                            viewModel.navigateTo(AppScreen.HOME)
                        }

                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = DeepCosmicBlack,
                            bottomBar = {
                                if (currentScreen != AppScreen.VOICE_HUD) {
                                    LonaBottomNavigation(
                                        currentScreen = currentScreen,
                                        accentColor = accentColor,
                                        onSelectScreen = { screen -> viewModel.navigateTo(screen) }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (currentScreen) {
                                    AppScreen.HOME -> HomeScreen(viewModel = viewModel, accentColor = accentColor)
                                    AppScreen.CHAT -> ChatScreen(viewModel = viewModel, accentColor = accentColor)
                                    AppScreen.VOICE_HUD -> VoiceHudModal(viewModel = viewModel, accentColor = accentColor)
                                    AppScreen.CODING -> CodingWorkspaceScreen(viewModel = viewModel, accentColor = accentColor)
                                    AppScreen.SUPPLIERS -> SuppliersScreen(viewModel = viewModel, accentColor = accentColor)
                                    AppScreen.PERMISSIONS -> PermissionCenterScreen(viewModel = viewModel, accentColor = accentColor)
                                    AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel, accentColor = accentColor)
                                    else -> HomeScreen(viewModel = viewModel, accentColor = accentColor)
                                }
                            }
                        }
                    }

                    // Optional Cyberpunk Edge Glow
                    if (edgeLightingEnabled && currentScreen != AppScreen.SPLASH) {
                        EdgeGlow(accentColor = accentColor)
                    }
                }
            }
        }
    }
}

@Composable
fun LonaBottomNavigation(
    currentScreen: AppScreen,
    accentColor: Color,
    onSelectScreen: (AppScreen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(64.dp),
        containerColor = DarkNavySurface,
        tonalElevation = 8.dp
    ) {
        // 1. Home
        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onSelectScreen(AppScreen.HOME) },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(22.dp)) },
            label = { Text("Home", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = accentColor,
                indicatorColor = accentColor,
                unselectedIconColor = TextDim,
                unselectedTextColor = TextDim
            )
        )

        // 2. Chat
        NavigationBarItem(
            selected = currentScreen == AppScreen.CHAT,
            onClick = { onSelectScreen(AppScreen.CHAT) },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", modifier = Modifier.size(22.dp)) },
            label = { Text("Chat", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = accentColor,
                indicatorColor = accentColor,
                unselectedIconColor = TextDim,
                unselectedTextColor = TextDim
            )
        )

        // 3. Central Glowing Microphone
        NavigationBarItem(
            selected = currentScreen == AppScreen.VOICE_HUD,
            onClick = { onSelectScreen(AppScreen.VOICE_HUD) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (currentScreen == AppScreen.VOICE_HUD) accentColor else DarkNavyCard)
                        .border(1.5.dp, accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice HUD",
                        tint = if (currentScreen == AppScreen.VOICE_HUD) Color.Black else accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            label = { Text("Voice", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = accentColor,
                indicatorColor = Color.Transparent,
                unselectedIconColor = TextDim,
                unselectedTextColor = TextDim
            )
        )

        // 4. Coding Matrix
        NavigationBarItem(
            selected = currentScreen == AppScreen.CODING,
            onClick = { onSelectScreen(AppScreen.CODING) },
            icon = { Icon(imageVector = Icons.Default.Code, contentDescription = "Matrix", modifier = Modifier.size(22.dp)) },
            label = { Text("Code", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = accentColor,
                indicatorColor = accentColor,
                unselectedIconColor = TextDim,
                unselectedTextColor = TextDim
            )
        )

        // 5. Suppliers / AI Providers Matrix
        NavigationBarItem(
            selected = currentScreen == AppScreen.SUPPLIERS,
            onClick = { onSelectScreen(AppScreen.SUPPLIERS) },
            icon = { Icon(imageVector = Icons.Default.Hub, contentDescription = "Suppliers", modifier = Modifier.size(22.dp)) },
            label = { Text("Suppliers", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = accentColor,
                indicatorColor = accentColor,
                unselectedIconColor = TextDim,
                unselectedTextColor = TextDim
            )
        )
    }
}
