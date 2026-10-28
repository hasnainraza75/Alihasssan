package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("lona_ai_secure_prefs", Context.MODE_PRIVATE)

    private val _apiKey = MutableStateFlow(getApiKey())
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _selectedModel = MutableStateFlow(getSelectedModel())
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _voicePersona = MutableStateFlow(getVoicePersona())
    val voicePersona: StateFlow<String> = _voicePersona.asStateFlow()

    private val _voicePitch = MutableStateFlow(getVoicePitch())
    val voicePitch: StateFlow<Float> = _voicePitch.asStateFlow()

    private val _voiceSpeed = MutableStateFlow(getVoiceSpeed())
    val voiceSpeed: StateFlow<Float> = _voiceSpeed.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(getSelectedLanguage())
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _themeAccent = MutableStateFlow(getThemeAccent())
    val themeAccent: StateFlow<String> = _themeAccent.asStateFlow()

    private val _edgeLighting = MutableStateFlow(isEdgeLightingEnabled())
    val edgeLighting: StateFlow<Boolean> = _edgeLighting.asStateFlow()

    private val _username = MutableStateFlow(getUsername())
    val username: StateFlow<String> = _username.asStateFlow()

    fun getApiKey(): String = prefs.getString("gemini_api_key", "") ?: ""

    fun setApiKey(key: String) {
        prefs.edit().putString("gemini_api_key", key.trim()).apply()
        _apiKey.value = key.trim()
    }

    fun getSelectedModel(): String =
        prefs.getString("gemini_model", "gemini-2.5-flash") ?: "gemini-2.5-flash"

    fun setSelectedModel(model: String) {
        prefs.edit().putString("gemini_model", model).apply()
        _selectedModel.value = model
    }

    fun getVoicePersona(): String =
        prefs.getString("voice_persona", "Female") ?: "Female"

    fun setVoicePersona(persona: String) {
        prefs.edit().putString("voice_persona", persona).apply()
        _voicePersona.value = persona
    }

    fun getVoicePitch(): Float = prefs.getFloat("voice_pitch", 1.0f)

    fun setVoicePitch(pitch: Float) {
        prefs.edit().putFloat("voice_pitch", pitch).apply()
        _voicePitch.value = pitch
    }

    fun getVoiceSpeed(): Float = prefs.getFloat("voice_speed", 1.0f)

    fun setVoiceSpeed(speed: Float) {
        prefs.edit().putFloat("voice_speed", speed).apply()
        _voiceSpeed.value = speed
    }

    fun getSelectedLanguage(): String =
        prefs.getString("selected_language", "en") ?: "en"

    fun setSelectedLanguage(lang: String) {
        prefs.edit().putString("selected_language", lang).apply()
        _selectedLanguage.value = lang
    }

    fun getThemeAccent(): String =
        prefs.getString("theme_accent", "cyan") ?: "cyan"

    fun setThemeAccent(accent: String) {
        prefs.edit().putString("theme_accent", accent).apply()
        _themeAccent.value = accent
    }

    fun isEdgeLightingEnabled(): Boolean =
        prefs.getBoolean("edge_lighting", true)

    fun setEdgeLighting(enabled: Boolean) {
        prefs.edit().putBoolean("edge_lighting", enabled).apply()
        _edgeLighting.value = enabled
    }

    fun getUsername(): String = prefs.getString("user_name", "Neural Commander") ?: "Neural Commander"

    fun setUsername(name: String) {
        prefs.edit().putString("user_name", name).apply()
        _username.value = name
    }

    fun getUserEmail(): String = prefs.getString("user_email", "commander@lona.ai") ?: "commander@lona.ai"

    fun setUserEmail(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    fun saveLocalAccount(name: String, email: String) {
        prefs.edit()
            .putString("user_name", name)
            .putString("user_email", email)
            .putBoolean("is_logged_in", true)
            .apply()
        _username.value = name
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", true)

    fun clearLocalAccount() {
        prefs.edit()
            .remove("user_name")
            .remove("user_email")
            .putBoolean("is_logged_in", false)
            .apply()
        _username.value = "Guest User"
    }
}
