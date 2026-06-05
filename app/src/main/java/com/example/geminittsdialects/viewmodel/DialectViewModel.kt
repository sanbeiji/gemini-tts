package com.example.geminittsdialects.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminittsdialects.api.DialectPcmPlayer
import com.example.geminittsdialects.api.DialectRepository
import com.example.geminittsdialects.data.SettingsRepository
import com.example.geminittsdialects.models.Dialect
import com.example.geminittsdialects.models.DialectData
import com.example.geminittsdialects.models.Language
import com.example.geminittsdialects.models.UserSettings
import com.example.geminittsdialects.models.VoiceStyle
import com.example.geminittsdialects.models.VoiceStyleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest

sealed class DialectUiState {
    object Idle : DialectUiState()
    object Loading : DialectUiState()
    object Playing : DialectUiState()
    data class Error(val message: String) : DialectUiState()
}

class DialectViewModel(
    private val settingsRepository: SettingsRepository,
    private val dialectRepository: DialectRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<DialectUiState>(DialectUiState.Idle)
    val uiState: StateFlow<DialectUiState> = _uiState.asStateFlow()

    val userSettings = settingsRepository.userSettingsFlow

    private val _selectedLanguage = MutableStateFlow<Language>(DialectData.languages.first())
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    private val _selectedDialect = MutableStateFlow<Dialect>(DialectData.languages.first().dialects.first())
    val selectedDialect: StateFlow<Dialect> = _selectedDialect.asStateFlow()

    private val _isMale = MutableStateFlow<Boolean>(false) // Default to female (Kore)
    val isMale: StateFlow<Boolean> = _isMale.asStateFlow()

    private val _selectedStyle = MutableStateFlow<VoiceStyle>(VoiceStyleData.styles.first())
    val selectedStyle: StateFlow<VoiceStyle> = _selectedStyle.asStateFlow()

    private val _textToSynthesize = MutableStateFlow<String>(DialectData.languages.first().defaultText)
    val textToSynthesize: StateFlow<String> = _textToSynthesize.asStateFlow()

    private val pcmPlayer = DialectPcmPlayer()

    init {
        // Ensure standard directory exists
        val cacheDir = File(context.cacheDir, "dialect_tts_cache")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            settingsRepository.saveSettings(settings)
        }
    }

    fun onLanguageSelected(language: Language) {
        _selectedLanguage.value = language
        // Auto-select the first dialect of that language
        val firstDialect = language.dialects.firstOrNull()
        if (firstDialect != null) {
            _selectedDialect.value = firstDialect
        }
        // Pre-fill text box with default text for the selected dialect or language
        _textToSynthesize.value = firstDialect?.defaultText ?: language.defaultText
    }

    fun onDialectSelected(dialect: Dialect) {
        _selectedDialect.value = dialect
        _textToSynthesize.value = dialect.defaultText ?: _selectedLanguage.value.defaultText
    }

    fun onStyleSelected(style: VoiceStyle) {
        _selectedStyle.value = style
    }

    fun onGenderSelected(isMale: Boolean) {
        _isMale.value = isMale
    }

    fun onTextChange(newText: String) {
        _textToSynthesize.value = newText
    }

    fun resetError() {
        if (_uiState.value is DialectUiState.Error) {
            _uiState.value = DialectUiState.Idle
        }
    }

    fun stopPlayback() {
        pcmPlayer.stop()
        if (_uiState.value is DialectUiState.Playing) {
            _uiState.value = DialectUiState.Idle
        }
    }

    private fun getAudioCacheFile(text: String, dialectId: String, voiceName: String, styleId: String): File {
        val hashInput = "${text}_${dialectId}_${voiceName}_${styleId}"
        val digest = MessageDigest.getInstance("MD5")
        val hashBytes = digest.digest(hashInput.toByteArray(Charsets.UTF_8))
        val hashString = hashBytes.joinToString("") { "%02x".format(it) }
        
        val cacheDir = File(context.cacheDir, "dialect_tts_cache")
        return File(cacheDir, "$hashString.pcm")
    }

    fun synthesizeAndPlay() {
        viewModelScope.launch {
            val text = _textToSynthesize.value.trim()
            if (text.isEmpty()) {
                _uiState.value = DialectUiState.Error("Please enter some text to synthesize.")
                return@launch
            }

            val settings = userSettings.first()
            if (settings.apiKey.isBlank()) {
                _uiState.value = DialectUiState.Error("Please configure your Gemini API key in settings.")
                return@launch
            }

            _uiState.value = DialectUiState.Loading

            val voiceName = if (_isMale.value) "Puck" else "Kore"
            val dialect = _selectedDialect.value
            val style = _selectedStyle.value
            val cacheFile = getAudioCacheFile(text, dialect.id, voiceName, style.id)

            try {
                val audioBytes = if (cacheFile.exists()) {
                    cacheFile.readBytes()
                } else {
                    val (styleText, tag) = getPromptAndTagForStyle(style, dialect.id)

                    val combinedPrompt = if (styleText.isNotBlank()) {
                        "${dialect.prompt}\nAdditionally, adjust your voice to: $styleText"
                    } else {
                        dialect.prompt
                    }

                    val base64Data = dialectRepository.generateSpeech(
                        apiKey = settings.apiKey,
                        text = "$tag$text",
                        dialectPrompt = combinedPrompt,
                        voiceName = voiceName
                    )
                    val decodedBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                    cacheFile.writeBytes(decodedBytes)
                    decodedBytes
                }

                _uiState.value = DialectUiState.Playing
                pcmPlayer.playRawPcm(audioBytes)
                _uiState.value = DialectUiState.Idle
            } catch (e: Exception) {
                _uiState.value = DialectUiState.Error(e.message ?: "An unexpected error occurred during synthesis.")
            }
        }
    }

    private fun getPromptAndTagForStyle(
        style: VoiceStyle,
        dialectId: String
    ): Pair<String, String> {
        return when (style.id) {
            "whispering" -> {
                if (dialectId.startsWith("zh_") || dialectId == "en_taiwanese") {
                    Pair("", "[softly] ")
                } else if (dialectId.startsWith("es_")) {
                    Pair("", "[gently] ")
                } else if (dialectId.startsWith("fr_")) {
                    Pair("", "[calmly] ")
                } else {
                    Pair("", "[whispers] ")
                }
            }
            "excited" -> Pair("Speak with high energy, enthusiasm, and a fast, lively pace.", "[excitedly] ")
            "melancholic" -> Pair("Speak slowly, with heavy pauses, downcast intonation, and a sad, melancholic tone.", "[sadly] ")
            "stern" -> Pair("Speak with a sharp, firm, assertive, and slightly angry or stern tone.", "[serious] ")
            else -> Pair("", "")
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            val cacheDir = File(context.cacheDir, "dialect_tts_cache")
            if (cacheDir.exists()) {
                cacheDir.deleteRecursively()
                cacheDir.mkdirs()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pcmPlayer.stop()
    }
}
