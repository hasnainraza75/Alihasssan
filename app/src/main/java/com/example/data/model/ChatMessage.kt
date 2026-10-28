package com.example.data.model

import java.util.UUID

enum class MessageSender {
    USER,
    LONA,
    SYSTEM
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val detectedLanguage: String = "en"
)

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val messages: List<ChatMessage> = emptyList()
)
