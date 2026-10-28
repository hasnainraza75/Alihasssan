package com.example.ui

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.VoiceManager
import com.example.audio.VoiceState
import com.example.data.api.GeminiApiClient
import com.example.data.local.ChatHistoryRepository
import com.example.data.local.PreferencesManager
import com.example.data.local.ProjectRepository
import com.example.data.model.ChatMessage
import com.example.data.model.CodeProject
import com.example.data.model.Conversation
import com.example.data.model.MessageSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    HOME,
    CHAT,
    VOICE_HUD,
    CODING,
    PERMISSIONS,
    SETTINGS
}

data class PermissionItem(
    val id: String,
    val title: String,
    val permissionManifest: String?,
    val description: String,
    val isGranted: Boolean,
    val isRestrictedOrSpecial: Boolean = false,
    val settingsAction: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val prefs = PreferencesManager(application)
    val chatRepo = ChatHistoryRepository(application)
    val projectRepo = ProjectRepository(application)
    val geminiApi = GeminiApiClient()
    val voiceManager = VoiceManager(application)

    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeConversationId = MutableStateFlow("default_session")
    val activeConversationId: StateFlow<String> = _activeConversationId.asStateFlow()

    private val _apiStatusMessage = MutableStateFlow<String?>(null)
    val apiStatusMessage: StateFlow<String?> = _apiStatusMessage.asStateFlow()

    private val _isTestingApi = MutableStateFlow(false)
    val isTestingApi: StateFlow<Boolean> = _isTestingApi.asStateFlow()

    private val _apiLatency = MutableStateFlow<Long?>(null)
    val apiLatency: StateFlow<Long?> = _apiLatency.asStateFlow()

    private val _activeProjectId = MutableStateFlow("template_login")
    val activeProjectId: StateFlow<String> = _activeProjectId.asStateFlow()

    private val _permissionsList = MutableStateFlow<List<PermissionItem>>(emptyList())
    val permissionsList: StateFlow<List<PermissionItem>> = _permissionsList.asStateFlow()

    val conversations: StateFlow<List<Conversation>> = chatRepo.conversations
    val projects: StateFlow<List<CodeProject>> = projectRepo.projects

    init {
        voiceManager.onSpeechRecognized = { text ->
            if (text.isNotBlank()) {
                handleVoiceInput(text)
            }
        }
        refreshPermissions()
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setActiveConversation(id: String) {
        _activeConversationId.value = id
    }

    fun setActiveProject(id: String) {
        _activeProjectId.value = id
    }

    fun startNewConversation() {
        viewModelScope.launch {
            val newConvo = Conversation(
                title = "New Neural Transmission",
                messages = listOf(
                    ChatMessage(
                        sender = MessageSender.LONA,
                        content = "Neural link refreshed. What objective shall we execute?"
                    )
                )
            )
            chatRepo.saveConversation(newConvo)
            _activeConversationId.value = newConvo.id
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            chatRepo.deleteConversation(id)
            val remaining = chatRepo.conversations.value
            if (remaining.isNotEmpty()) {
                _activeConversationId.value = remaining.first().id
            } else {
                startNewConversation()
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            chatRepo.clearAllHistory()
            startNewConversation()
        }
    }

    fun handleVoiceInput(recognizedText: String) {
        sendUserMessage(recognizedText, isFromVoice = true)
    }

    fun sendUserMessage(text: String, isFromVoice: Boolean = false) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val convoId = _activeConversationId.value
        val userMsg = ChatMessage(sender = MessageSender.USER, content = trimmed)

        viewModelScope.launch {
            chatRepo.addMessageToConversation(convoId, userMsg)

            val apiKey = prefs.getApiKey()
            val model = prefs.getSelectedModel()

            if (apiKey.isBlank()) {
                val alertMsg = ChatMessage(
                    sender = MessageSender.LONA,
                    content = "Neural Link Warning: Gemini API key is missing. Please configure your key in Settings to activate autonomous cognition."
                )
                chatRepo.addMessageToConversation(convoId, alertMsg)
                if (isFromVoice) {
                    voiceManager.speak("Gemini API key is required. Please set it in settings.")
                }
                return@launch
            }

            voiceManager.setVoiceState(VoiceState.THINKING)
            val currentConvo = chatRepo.conversations.value.firstOrNull { it.id == convoId }
            val history = currentConvo?.messages ?: emptyList()

            val responseResult = geminiApi.generateContent(
                apiKey = apiKey,
                model = model,
                history = history,
                userPrompt = trimmed
            )

            if (responseResult.isSuccess) {
                val replyText = responseResult.getOrNull() ?: ""
                val lonaMsg = ChatMessage(
                    sender = MessageSender.LONA,
                    content = replyText
                )
                chatRepo.addMessageToConversation(convoId, lonaMsg)
                voiceManager.setVoiceState(VoiceState.IDLE)

                if (isFromVoice) {
                    // Filter code blocks for speech to avoid speaking raw syntax
                    val speakableText = replyText.replace(Regex("```[\\s\\S]*?```"), "[Code output sent to Matrix]")
                    val pitch = prefs.getVoicePitch()
                    val speed = prefs.getVoiceSpeed()
                    val lang = prefs.getSelectedLanguage()
                    voiceManager.speak(speakableText.take(300), pitch = pitch, speed = speed, language = lang)
                }
            } else {
                val err = responseResult.exceptionOrNull()?.localizedMessage ?: "Neural link error"
                val errMessage = ChatMessage(
                    sender = MessageSender.LONA,
                    content = "Transmission Interrupted: $err"
                )
                chatRepo.addMessageToConversation(convoId, errMessage)
                voiceManager.setVoiceState(VoiceState.ERROR)
                if (isFromVoice) {
                    voiceManager.speak("Neural transmission failed. $err")
                }
            }
        }
    }

    fun toggleVoiceListening() {
        if (voiceManager.voiceState.value == VoiceState.LISTENING) {
            voiceManager.stopListening()
        } else if (voiceManager.voiceState.value == VoiceState.SPEAKING) {
            voiceManager.stopPlayback()
        } else {
            val lang = prefs.getSelectedLanguage()
            voiceManager.startListening(lang)
        }
    }

    fun stopVoice() {
        voiceManager.stopListening()
        voiceManager.stopPlayback()
    }

    fun testGeminiConnection() {
        val key = prefs.getApiKey()
        val model = prefs.getSelectedModel()
        _isTestingApi.value = true
        _apiStatusMessage.value = "Pinging Google Generative Language Neural Gateway..."
        _apiLatency.value = null

        viewModelScope.launch {
            val result = geminiApi.testConnection(key, model)
            _isTestingApi.value = false
            if (result.isSuccess) {
                val latency = result.getOrNull() ?: 0L
                _apiLatency.value = latency
                _apiStatusMessage.value = "Neural Link Online! Roundtrip: ${latency}ms (Status: 200 OK)"
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                _apiLatency.value = null
                _apiStatusMessage.value = "Connection Failed: $err"
            }
        }
    }

    fun saveCodeProject(project: CodeProject) {
        viewModelScope.launch {
            projectRepo.saveProject(project)
        }
    }

    fun createNewProject(name: String, description: String) {
        viewModelScope.launch {
            val newP = CodeProject(
                name = name,
                description = description,
                files = mapOf(
                    "index.html" to "<!DOCTYPE html>\n<html>\n<head>\n  <title>$name</title>\n</head>\n<body>\n  <h1>$name</h1>\n</body>\n</html>",
                    "style.css" to "body { background: #070B19; color: #00F5D4; font-family: sans-serif; padding: 20px; }",
                    "script.js" to "console.log('$name initialized');"
                )
            )
            projectRepo.saveProject(newP)
            _activeProjectId.value = newP.id
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            projectRepo.deleteProject(id)
            val remaining = projectRepo.projects.value
            if (remaining.isNotEmpty()) {
                _activeProjectId.value = remaining.first().id
            }
        }
    }

    fun refreshPermissions() {
        val context = getApplication<Application>()
        val list = listOf(
            PermissionItem(
                id = "mic",
                title = "Microphone",
                permissionManifest = android.Manifest.permission.RECORD_AUDIO,
                description = "Captures live vocal inquiries for the LONA real-time speech conversational pipeline.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "camera",
                title = "Camera",
                permissionManifest = android.Manifest.permission.CAMERA,
                description = "Enables real-time optical capture, document scanning, and vision analysis.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "location",
                title = "Location",
                permissionManifest = android.Manifest.permission.ACCESS_FINE_LOCATION,
                description = "Provides precise orbital coordinate telemetry for localized weather and navigation assistance.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "contacts",
                title = "Contacts",
                permissionManifest = android.Manifest.permission.READ_CONTACTS,
                description = "Facilitates user-directed contact lookup for initiated voice calls or messages.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "call_logs",
                title = "Call Logs",
                permissionManifest = android.Manifest.permission.READ_CALL_LOG,
                description = "Provides call history intelligence for authorized communication diagnostics.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "phone",
                title = "Phone",
                permissionManifest = android.Manifest.permission.CALL_PHONE,
                description = "Allows initiating authorized calls upon explicit user voice command.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "notifications",
                title = "Notifications",
                permissionManifest = android.Manifest.permission.POST_NOTIFICATIONS,
                description = "Transmits mission status alerts, coding progress, and background voice notifications.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "sms",
                title = "SMS",
                permissionManifest = android.Manifest.permission.READ_SMS,
                description = "Allows reading or dispatching urgent user-confirmed text transmissions.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "audio",
                title = "Music & Audio",
                permissionManifest = android.Manifest.permission.READ_MEDIA_AUDIO,
                description = "Enables sound synthesis analysis and playback of audio files.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "photos",
                title = "Photos & Videos",
                permissionManifest = android.Manifest.permission.READ_MEDIA_IMAGES,
                description = "Accesses user-selected imagery for wallpaper customization and visual coding assets.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "nearby",
                title = "Nearby Devices",
                permissionManifest = android.Manifest.permission.BLUETOOTH_CONNECT,
                description = "Connects to nearby holographic peripherals, Bluetooth mics, and headsets.",
                isGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            ),
            PermissionItem(
                id = "files",
                title = "Files & Documents",
                permissionManifest = null,
                description = "Utilizes Android Storage Access Framework (SAF) for exporting projects with zero broad storage risks.",
                isGranted = true,
                isRestrictedOrSpecial = false
            ),
            PermissionItem(
                id = "accessibility",
                title = "Accessibility",
                permissionManifest = null,
                description = "Configures Android Accessibility Service for voice-driven screen navigation upon manual user enrollment.",
                isGranted = false,
                isRestrictedOrSpecial = true,
                settingsAction = android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS
            ),
            PermissionItem(
                id = "default_assistant",
                title = "Default Assistant",
                permissionManifest = null,
                description = "Designates LONA AI as the primary system voice assistant invoked via long-press home or power key.",
                isGranted = false,
                isRestrictedOrSpecial = true,
                settingsAction = android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS
            ),
            PermissionItem(
                id = "background_service",
                title = "Background Voice Service",
                permissionManifest = null,
                description = "Allows continuous ambient neural monitoring without system battery throttling.",
                isGranted = true,
                isRestrictedOrSpecial = true,
                settingsAction = android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            )
        )
        _permissionsList.value = list
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
