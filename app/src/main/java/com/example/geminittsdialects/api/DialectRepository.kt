package com.example.geminittsdialects.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class DialectRepository {

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            }
        }
        install(ContentNegotiation) {
            json(jsonConfig)
        }
    }

    suspend fun generateSpeech(
        apiKey: String,
        text: String,
        dialectPrompt: String,
        voiceName: String
    ): String {
        try {
            // Combine the dialect guidelines with the text to recite
            val fullPrompt = "$dialectPrompt\n\nPlease recite the following text exactly as requested: \"$text\""

            val requestBody = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = fullPrompt)
                        )
                    )
                ),
                generationConfig = GenerationConfig(
                    responseModalities = listOf("AUDIO"),
                    speechConfig = SpeechConfig(
                        voiceConfig = VoiceConfig(
                            prebuiltVoiceConfig = PrebuiltVoiceConfig(
                                voiceName = voiceName
                            )
                        )
                    )
                )
            )

            val fetchModel = "gemini-3.1-flash-tts-preview"
            val url = "https://generativelanguage.googleapis.com/v1beta/models/${fetchModel}:generateContent?key=$apiKey"

            val httpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val responseText = httpResponse.bodyAsText()
            android.util.Log.d("GeminiTTS", "Raw Response: $responseText")
            val response = jsonConfig.decodeFromString<GeminiResponse>(responseText)

            if (response.error != null) {
                throw Exception(response.error.message)
            }

            val candidate = response.candidates?.firstOrNull()
            val part = candidate?.content?.parts?.find { it.inlineData != null && it.inlineData.mimeType.startsWith("audio/") }
            
            if (part?.inlineData?.data == null) {
                throw Exception("No audio data returned from Gemini API. Check if your API key is correct and valid for the gemini-3.1-flash-tts-preview model.")
            }

            return part.inlineData.data
        } catch (e: Exception) {
            val msg = e.message ?: "Audio generation failed"
            val sanitizedMsg = if (apiKey.isNotBlank()) msg.replace(apiKey, "[REDACTED]") else msg
            throw Exception(sanitizedMsg)
        }
    }
}
