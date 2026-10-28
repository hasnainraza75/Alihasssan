package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.CodeProject
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun CodingWorkspaceScreen(
    viewModel: MainViewModel,
    accentColor: Color = NeonCyan
) {
    val context = LocalContext.current
    val projects by viewModel.projects.collectAsState()
    val activeProjectId by viewModel.activeProjectId.collectAsState()
    val currentProject = projects.firstOrNull { it.id == activeProjectId } ?: projects.firstOrNull()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Editor, 1: Live Preview
    var selectedFile by remember { mutableStateOf("index.html") }
    var fileContent by remember(currentProject, selectedFile) {
        mutableStateOf(currentProject?.files?.get(selectedFile) ?: "")
    }

    var showAiPromptDialog by remember { mutableStateOf(false) }
    var aiPromptText by remember { mutableStateOf("") }
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var newProjectName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCosmicBlack)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkNavySurface)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentProject?.name ?: "LONA Code Matrix",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
                Text(
                    text = "Multi-File Web Engine & Sandbox",
                    fontSize = 10.sp,
                    color = accentColor,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // AI Generate Button
                IconButton(onClick = { showAiPromptDialog = true }) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Generate", tint = NeonPurple)
                }
                // Save Project
                IconButton(
                    onClick = {
                        if (currentProject != null) {
                            val updatedFiles = currentProject.files.toMutableMap()
                            updatedFiles[selectedFile] = fileContent
                            val updated = currentProject.copy(files = updatedFiles)
                            viewModel.saveCodeProject(updated)
                            Toast.makeText(context, "Project saved to local matrix", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", tint = accentColor)
                }
                // Copy Code
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("code", fileContent))
                        Toast.makeText(context, "Copied $selectedFile", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextWhite)
                }
                // Export Project HTML
                IconButton(
                    onClick = {
                        val html = currentProject?.getCombinedHtml() ?: fileContent
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, html)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Export LONA Project"))
                    }
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Export", tint = TextWhite)
                }
            }
        }

        // Projects Horizontal Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF090E21))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            projects.forEach { proj ->
                val isSelected = proj.id == activeProjectId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) DarkNavyCard else Color.Transparent)
                        .border(1.dp, if (isSelected) accentColor else DarkNavyBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            viewModel.setActiveProject(proj.id)
                            selectedFile = proj.files.keys.firstOrNull() ?: "index.html"
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = proj.name,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) accentColor else TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, NeonPurple, RoundedCornerShape(8.dp))
                    .clickable { showNewProjectDialog = true }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "+ New Project",
                    fontSize = 11.sp,
                    color = NeonPurple,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Editor vs Preview Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkNavySurface,
            contentColor = TextWhite,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = accentColor
                )
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Code Editor", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = {
                    // Auto-sync current file changes before preview
                    if (currentProject != null) {
                        val updatedFiles = currentProject.files.toMutableMap()
                        updatedFiles[selectedFile] = fileContent
                        viewModel.saveCodeProject(currentProject.copy(files = updatedFiles))
                    }
                    selectedTabIndex = 1
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Live Preview", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                }
            )
        }

        if (selectedTabIndex == 0) {
            // File tabs (index.html, style.css, script.js)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF04060E))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val files = currentProject?.files?.keys?.toList() ?: listOf("index.html")
                files.forEach { fileName ->
                    val isCurFile = fileName == selectedFile
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurFile) DarkNavyCard else Color.Transparent)
                            .border(1.dp, if (isCurFile) accentColor.copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable {
                                // Save current file edits
                                if (currentProject != null) {
                                    val map = currentProject.files.toMutableMap()
                                    map[selectedFile] = fileContent
                                    viewModel.saveCodeProject(currentProject.copy(files = map))
                                }
                                selectedFile = fileName
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = fileName,
                            fontSize = 11.sp,
                            fontWeight = if (isCurFile) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurFile) accentColor else TextDim,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Code Editor
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF02040A))
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = fileContent,
                    onValueChange = { fileContent = it },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = NeonCyan,
                        lineHeight = 18.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = accentColor,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        } else {
            // Live Preview inside sandboxed WebView
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                val combinedHtml = currentProject?.getCombinedHtml() ?: fileContent
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            webViewClient = WebViewClient()
                            loadDataWithBaseURL(null, combinedHtml, "text/html", "UTF-8", null)
                        }
                    },
                    update = { view ->
                        view.loadDataWithBaseURL(null, combinedHtml, "text/html", "UTF-8", null)
                    }
                )
            }
        }
    }

    // AI Generation Dialog
    if (showAiPromptDialog) {
        AlertDialog(
            onDismissRequest = { showAiPromptDialog = false },
            title = {
                Text(
                    text = "LONA AI Code Synthesis",
                    color = TextWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Describe the web component or feature to generate or repair for this project:",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = aiPromptText,
                        onValueChange = { aiPromptText = it },
                        placeholder = { Text("e.g. Add a glowing countdown timer with neon styling", color = TextDim, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = DarkNavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkNavyCard,
                            unfocusedContainerColor = DarkNavyCard
                        ),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val prompt = aiPromptText.trim()
                        if (prompt.isNotBlank()) {
                            showAiPromptDialog = false
                            val fullPrompt = "Write code for: $prompt. Please provide clean, modern HTML/CSS/JS."
                            viewModel.sendUserMessage(fullPrompt)
                            viewModel.navigateTo(com.example.ui.AppScreen.CHAT)
                            Toast.makeText(context, "Transmitted to LONA Chat. Check generated code.", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(text = "Synthesize", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiPromptDialog = false }) {
                    Text(text = "Cancel", color = TextWhite)
                }
            },
            containerColor = DarkNavySurface
        )
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            title = { Text(text = "Initialize Project Matrix", color = TextWhite, fontFamily = FontFamily.Monospace) },
            text = {
                OutlinedTextField(
                    value = newProjectName,
                    onValueChange = { newProjectName = it },
                    placeholder = { Text("Project Name (e.g. Cyber Hologram)", color = TextDim) },
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProjectName.isNotBlank()) {
                            viewModel.createNewProject(newProjectName.trim(), "Custom LONA Project")
                            showNewProjectDialog = false
                            newProjectName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(text = "Create", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) {
                    Text(text = "Cancel", color = TextWhite)
                }
            },
            containerColor = DarkNavySurface
        )
    }
}
