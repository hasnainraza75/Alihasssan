package com.example.data.local

import android.content.Context
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ChatHistoryRepository(private val context: Context) {
    private val storageFile = File(context.filesDir, "lona_chat_history.json")
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        try {
            if (!storageFile.exists()) {
                val defaultConvo = Conversation(
                    id = "default_session",
                    title = "Neural Link Initiation",
                    updatedAt = System.currentTimeMillis(),
                    messages = listOf(
                        ChatMessage(
                            sender = MessageSender.LONA,
                            content = "Greetings, Commander. I am LONA AI, your quantum neural assistant. Voice synthesizers, coding matrix, and device controls are online. How may I assist your mission today?",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                )
                _conversations.value = listOf(defaultConvo)
                saveToFile(listOf(defaultConvo))
                return
            }

            val jsonStr = storageFile.readText()
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<Conversation>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.optString("id", "")
                val title = obj.optString("title", "Untitled Chat")
                val updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                val msgsArray = obj.optJSONArray("messages") ?: JSONArray()
                val msgs = mutableListOf<ChatMessage>()
                for (j in 0 until msgsArray.length()) {
                    val mObj = msgsArray.getJSONObject(j)
                    val mId = mObj.optString("id", "")
                    val senderStr = mObj.optString("sender", "USER")
                    val sender = try { MessageSender.valueOf(senderStr) } catch (_: Exception) { MessageSender.USER }
                    val content = mObj.optString("content", "")
                    val ts = mObj.optLong("timestamp", System.currentTimeMillis())
                    val lang = mObj.optString("detectedLanguage", "en")
                    msgs.add(ChatMessage(id = mId, sender = sender, content = content, timestamp = ts, detectedLanguage = lang))
                }
                list.add(Conversation(id = id, title = title, updatedAt = updatedAt, messages = msgs))
            }
            _conversations.value = list
        } catch (e: Exception) {
            e.printStackTrace()
            _conversations.value = emptyList()
        }
    }

    private fun saveToFile(list: List<Conversation>) {
        try {
            val jsonArray = JSONArray()
            for (convo in list) {
                val cObj = JSONObject()
                cObj.put("id", convo.id)
                cObj.put("title", convo.title)
                cObj.put("updatedAt", convo.updatedAt)
                val msgsArray = JSONArray()
                for (msg in convo.messages) {
                    val mObj = JSONObject()
                    mObj.put("id", msg.id)
                    mObj.put("sender", msg.sender.name)
                    mObj.put("content", msg.content)
                    mObj.put("timestamp", msg.timestamp)
                    mObj.put("detectedLanguage", msg.detectedLanguage)
                    msgsArray.put(mObj)
                }
                cObj.put("messages", msgsArray)
                jsonArray.put(cObj)
            }
            storageFile.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveConversation(conversation: Conversation) = withContext(Dispatchers.IO) {
        val current = _conversations.value.toMutableList()
        val index = current.indexOfFirst { it.id == conversation.id }
        if (index >= 0) {
            current[index] = conversation
        } else {
            current.add(0, conversation)
        }
        _conversations.value = current
        saveToFile(current)
    }

    suspend fun addMessageToConversation(conversationId: String, message: ChatMessage) = withContext(Dispatchers.IO) {
        val current = _conversations.value.toMutableList()
        val index = current.indexOfFirst { it.id == conversationId }
        if (index >= 0) {
            val existing = current[index]
            val updatedMessages = existing.messages + message
            val title = if (existing.messages.isEmpty() && message.sender == MessageSender.USER) {
                message.content.take(30).trim() + "…"
            } else {
                existing.title
            }
            val updated = existing.copy(
                title = title,
                updatedAt = System.currentTimeMillis(),
                messages = updatedMessages
            )
            current[index] = updated
            current.sortByDescending { it.updatedAt }
            _conversations.value = current
            saveToFile(current)
        }
    }

    suspend fun renameConversation(conversationId: String, newTitle: String) = withContext(Dispatchers.IO) {
        val current = _conversations.value.toMutableList()
        val index = current.indexOfFirst { it.id == conversationId }
        if (index >= 0) {
            current[index] = current[index].copy(title = newTitle, updatedAt = System.currentTimeMillis())
            _conversations.value = current
            saveToFile(current)
        }
    }

    suspend fun deleteConversation(conversationId: String) = withContext(Dispatchers.IO) {
        val current = _conversations.value.filter { it.id != conversationId }
        _conversations.value = current
        saveToFile(current)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        _conversations.value = emptyList()
        if (storageFile.exists()) {
            storageFile.delete()
        }
    }
}
