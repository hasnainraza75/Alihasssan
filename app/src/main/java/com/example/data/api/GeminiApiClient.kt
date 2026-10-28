package com.example.data.api

import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class GeminiApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val systemInstruction = """
        You are LONA AI, an advanced, highly intelligent, futuristic AI neural assistant.
        You speak with confidence, clarity, precision, and a modern cyberpunk futuristic tone.
        You are an expert full-stack coder (HTML, CSS, JS, Python, Kotlin, Android), a device action advisor, and a voice conversationalist.
        You support and converse fluently in English, Urdu (اردو), Roman Urdu, and Hindi (हिंदी).
        When requested for code, always return complete, runnable, well-structured code with markdown syntax codeblocks.
    """.trimIndent()

    suspend fun testConnection(apiKey: String, model: String): Result<Long> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("API Key is empty. Please enter your Google Gemini API key."))
        }
        val startTime = System.currentTimeMillis()
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val payload = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "LONA AI Neural Ping Test. Respond with 'PONG'."))
                        })
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().put("maxOutputTokens", 10))
            }

            val request = Request.Builder()
                .url(url)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val latency = System.currentTimeMillis() - startTime
                if (response.isSuccessful) {
                    Result.success(latency)
                } else {
                    val code = response.code
                    val errorBody = response.body?.string() ?: ""
                    val errorMsg = parseErrorMessage(code, errorBody)
                    Result.failure(Exception("HTTP $code: $errorMsg"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateContent(
        apiKey: String,
        model: String,
        history: List<ChatMessage>,
        userPrompt: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("API key required. Configure your Gemini key in Settings."))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val payload = buildRequestPayload(history, userPrompt)

            val request = Request.Builder()
                .url(url)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val code = response.code
                    val errorBody = response.body?.string() ?: ""
                    return@withContext Result.failure(Exception("Gemini API Error ($code): ${parseErrorMessage(code, errorBody)}"))
                }

                val bodyStr = response.body?.string() ?: ""
                val json = JSONObject(bodyStr)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        return@withContext Result.success(text)
                    }
                }
                Result.success("LONA AI received an empty neural transmission.")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun streamGenerateContent(
        apiKey: String,
        model: String,
        history: List<ChatMessage>,
        userPrompt: String
    ): Flow<String> = flow {
        if (apiKey.isBlank()) {
            emit("API key required. Please configure your Google Gemini API key in Settings.")
            return@flow
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:streamGenerateContent?alt=sse&key=$apiKey"
        val payload = buildRequestPayload(history, userPrompt)

        val request = Request.Builder()
            .url(url)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val code = response.code
                    val err = response.body?.string() ?: ""
                    emit("Error ($code): ${parseErrorMessage(code, err)}")
                    return@use
                }

                val source = response.body?.byteStream() ?: return@use
                val reader = BufferedReader(InputStreamReader(source))
                var line: String? = reader.readLine()
                while (line != null) {
                    if (line.startsWith("data: ")) {
                        val jsonStr = line.substring(6).trim()
                        if (jsonStr.isNotEmpty() && jsonStr != "[DONE]") {
                            try {
                                val json = JSONObject(jsonStr)
                                val candidates = json.optJSONArray("candidates")
                                if (candidates != null && candidates.length() > 0) {
                                    val parts = candidates.getJSONObject(0)
                                        .optJSONObject("content")
                                        ?.optJSONArray("parts")
                                    if (parts != null && parts.length() > 0) {
                                        val text = parts.getJSONObject(0).optString("text", "")
                                        if (text.isNotEmpty()) {
                                            emit(text)
                                        }
                                    }
                                }
                            } catch (_: Exception) {
                                // continue parsing
                            }
                        }
                    }
                    line = reader.readLine()
                }
            }
        } catch (e: Exception) {
            emit("Neural link interrupted: ${e.localizedMessage ?: "Network error"}")
        }
    }.flowOn(Dispatchers.IO)

    private fun buildRequestPayload(history: List<ChatMessage>, userPrompt: String): JSONObject {
        return JSONObject().apply {
            // System instruction
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemInstruction))
                })
            })

            // Contents array (Conversation history + current prompt)
            val contents = JSONArray()
            // Take up to last 8 turns for context
            val recent = history.takeLast(8)
            for (msg in recent) {
                val role = if (msg.sender == MessageSender.USER) "user" else "model"
                contents.put(JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", msg.content))
                    })
                })
            }
            // Add latest user prompt
            contents.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", userPrompt))
                })
            })

            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 2048)
            })
        }
    }

    private fun parseErrorMessage(code: Int, body: String): String {
        return try {
            val json = JSONObject(body)
            val errObj = json.optJSONObject("error")
            val msg = errObj?.optString("message") ?: ""
            if (msg.isNotEmpty()) {
                msg
            } else {
                when (code) {
                    400 -> "Invalid request or malformed API key."
                    403 -> "API Key permission denied or Generative Language API not enabled."
                    429 -> "Rate limit or quota exceeded. Please wait a moment."
                    500, 503 -> "Gemini AI servers temporarily unavailable. Please retry."
                    else -> "HTTP $code response from AI Studio server."
                }
            }
        } catch (_: Exception) {
            "HTTP $code: Unknown server response."
        }
    }
}
