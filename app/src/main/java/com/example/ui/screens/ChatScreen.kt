package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChatMessage
import com.example.data.model.CodeProject
import com.example.data.model.MessageSender
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepCosmicBlack
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    accentColor: Color = NeonCyan
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val conversations by viewModel.conversations.collectAsState()
    val activeConvoId by viewModel.activeConversationId.collectAsState()
    val currentConvo = conversations.firstOrNull { it.id == activeConvoId } ?: conversations.firstOrNull()

    var textInput by remember { mutableStateOf("") }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()

    val messages = currentConvo?.messages ?: emptyList()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCosmicBlack)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkNavySurface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { showHistorySheet = true }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lona_logo),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.dp, accentColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = currentConvo?.title ?: "Neural Channel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Gemini Link: Active (Tap for History)",
                        fontSize = 10.sp,
                        color = accentColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.startNewConversation() }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Chat", tint = accentColor)
                }
                IconButton(onClick = { showHistorySheet = true }) {
                    Icon(imageVector = Icons.Default.History, contentDescription = "History", tint = TextWhite)
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(
                    message = msg,
                    accentColor = accentColor,
                    onSpeak = { text ->
                        val pitch = viewModel.prefs.getVoicePitch()
                        val speed = viewModel.prefs.getVoiceSpeed()
                        val lang = viewModel.prefs.getSelectedLanguage()
                        viewModel.voiceManager.speak(text, pitch, speed, lang)
                    },
                    onOpenInCodeMatrix = { code ->
                        val project = CodeProject(
                            name = "Imported AI Solution",
                            description = "Generated by LONA AI in Chat",
                            files = mapOf("index.html" to code)
                        )
                        viewModel.saveCodeProject(project)
                        viewModel.setActiveProject(project.id)
                        viewModel.navigateTo(AppScreen.CODING)
                    }
                )
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkNavySurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.toggleVoiceListening()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = accentColor
                )
            }

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = {
                    Text(
                        text = "Message LONA AI…",
                        color = TextDim,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = DarkNavyBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = accentColor,
                    focusedContainerColor = DarkNavyCard,
                    unfocusedContainerColor = DarkNavyCard
                ),
                maxLines = 4
            )

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendUserMessage(textInput)
                        textInput = ""
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (textInput.isNotBlank()) accentColor else TextDim
                )
            }
        }
    }

    // Chat History Bottom Sheet
    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            sheetState = sheetState,
            containerColor = DarkNavySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSMISSION HISTORY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    TextButton(onClick = { showClearConfirm = true }) {
                        Text(text = "Clear All", color = StatusErrorRed, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(conversations) { convo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (convo.id == activeConvoId) DarkNavyCard else Color.Transparent)
                                .border(1.dp, if (convo.id == activeConvoId) accentColor else DarkNavyBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.setActiveConversation(convo.id)
                                    showHistorySheet = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = convo.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${convo.messages.size} entries",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteConversation(convo.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = TextDim,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(text = "Purge All Transmissions?", color = TextWhite) },
            text = { Text(text = "This will permanently erase all local conversation memory.", color = TextMuted) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllHistory()
                    showClearConfirm = false
                    showHistorySheet = false
                }) {
                    Text(text = "Purge", color = StatusErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(text = "Cancel", color = TextWhite)
                }
            },
            containerColor = DarkNavySurface
        )
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    accentColor: Color,
    onSpeak: (String) -> Unit,
    onOpenInCodeMatrix: (String) -> Unit
) {
    val context = LocalContext.current
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Image(
                painter = painterResource(id = R.drawable.lona_logo),
                contentDescription = "LONA",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(1.dp, accentColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isUser) Color(0xFF132042) else DarkNavyCard)
                .border(
                    1.dp,
                    if (isUser) NeonPurple.copy(alpha = 0.4f) else accentColor.copy(alpha = 0.35f),
                    RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
        ) {
            val content = message.content

            // Check if contains code block
            if (content.contains("```")) {
                val parts = content.split("```")
                parts.forEachIndexed { index, part ->
                    if (index % 2 == 1) {
                        // Code block
                        val lines = part.trim().lines()
                        val lang = if (lines.firstOrNull()?.matches(Regex("^[a-zA-Z0-9]+$")) == true) lines.first() else "code"
                        val codeBody = if (lang != "code") lines.drop(1).joinToString("\n") else part

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF03050C))
                                .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = lang.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("code", codeBody))
                                                Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextWhite, modifier = Modifier.size(14.dp))
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { onOpenInCodeMatrix(codeBody) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Code, contentDescription = "Open in Matrix", tint = NeonCyan, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                Text(
                                    text = codeBody,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else if (part.isNotBlank()) {
                        Text(
                            text = part.trim(),
                            fontSize = 13.sp,
                            color = TextWhite,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                Text(
                    text = content,
                    fontSize = 13.sp,
                    color = TextWhite,
                    lineHeight = 18.sp
                )
            }

            if (!isUser) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onSpeak(content) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
