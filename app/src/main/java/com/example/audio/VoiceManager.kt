package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class VoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}

class VoiceManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _audioRms = MutableStateFlow(0f)
    val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var onSpeechRecognized: ((String) -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
        initSpeechRecognizer()
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _voiceState.value = VoiceState.LISTENING
                        _errorMessage.value = null
                    }

                    override fun onBeginningOfSpeech() {
                        _voiceState.value = VoiceState.LISTENING
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        _audioRms.value = rmsdB.coerceIn(0f, 10f)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _voiceState.value = VoiceState.THINKING
                    }

                    override fun onError(error: Int) {
                        _voiceState.value = VoiceState.IDLE
                        _audioRms.value = 0f
                        val msg = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client speech error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No voice detected. Please try again."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer busy"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout. Tap to speak again."
                            else -> "Speech recognition paused"
                        }
                        _errorMessage.value = msg
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        _spokenText.value = text
                        _voiceState.value = VoiceState.IDLE
                        _audioRms.value = 0f
                        if (text.isNotBlank()) {
                            onSpeechRecognized?.invoke(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _spokenText.value = it
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    fun startListening(languageCode: String = "en-US") {
        stopPlayback()
        _spokenText.value = ""
        _errorMessage.value = null

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            val localeStr = when (languageCode) {
                "ur" -> "ur-PK"
                "hi" -> "hi-IN"
                "roman_ur" -> "ur-Latn"
                else -> "en-US"
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeStr)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeStr)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        try {
            speechRecognizer?.startListening(intent)
            _voiceState.value = VoiceState.LISTENING
        } catch (e: Exception) {
            _errorMessage.value = "Failed to start speech listener: ${e.localizedMessage}"
            _voiceState.value = VoiceState.ERROR
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _voiceState.value = VoiceState.IDLE
        _audioRms.value = 0f
    }

    fun speak(
        text: String,
        pitch: Float = 1.0f,
        speed: Float = 1.0f,
        language: String = "en"
    ) {
        if (!isTtsInitialized || tts == null) return
        stopListening()

        val locale = when (language) {
            "ur" -> Locale("ur", "PK")
            "hi" -> Locale("hi", "IN")
            else -> Locale.US
        }

        tts?.apply {
            setLanguage(locale)
            setPitch(pitch)
            setSpeechRate(speed)
            val utteranceId = "LONA_${System.currentTimeMillis()}"
            speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
        _voiceState.value = VoiceState.SPEAKING
    }

    fun stopPlayback() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
        if (_voiceState.value == VoiceState.SPEAKING) {
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun setVoiceState(state: VoiceState) {
        _voiceState.value = state
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _voiceState.value = VoiceState.SPEAKING
                }

                override fun onDone(utteranceId: String?) {
                    _voiceState.value = VoiceState.IDLE
                }

                override fun onError(utteranceId: String?) {
                    _voiceState.value = VoiceState.IDLE
                }
            })
        }
    }

    fun release() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (_: Exception) {}
    }
}
