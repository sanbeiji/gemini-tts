# Gemini Dialect Synthesizer 🗣️

An Android demonstration application showcasing the multimodal Text-to-Speech (TTS) capabilities of Google's **Gemini 3.1 Flash** (`gemini-3.1-flash-tts-preview`) model. The application synthesizes text with highly specific regional accents and dialects by leveraging custom descriptive system prompt instruction wrappers.

---

## 🌟 Features

* **Large Text Input Box**: Pre-filled with natural sample text (2 sentences) tailored to the selected language. Users can freely type their own custom sentences.
* **Language Selector**: Supports 5 languages:
  - English
  - Spanish
  - German
  - French
  - Chinese (Mandarin/Sinitic)
* **Voice Gender Selection**: Segmented controls to toggle between:
  - **Female voice** (`Kore`)
  - **Male voice** (`Puck`)
* **Over 20 Regional Dialects**:
  - **English**: London, Inverness (Scotland), Galway (Ireland), Bostonian, NYC Mobster, Texas Drawl, California Surfer, Adelaide (Australia), Taiwanese English, Japanese English, Shakespearean Actor.
  - **Spanish**: Madrid (Castilian), Barcelona (Catalan substrate), Mexico.
  - **German**: Berlin (Berlinerisch), Vienna (Wienerisch).
  - **French**: Paris (Standard metropolitan), Quebec (Québécois).
  - **Chinese (Mandarin/Sinitic)**: Beijing Mandarin (Erhua), Shanghai Mandarin (Wu substrate), Cantonese (standard Yue readings), Taiwanese Mandarin (urban Taipei style), Taiwanese Southern Accent, Taiwanese Southern Heavy + Minnan substrate.
* **Instant Local Cache**: Compares MD5 hashes of text inputs, dialect IDs, and voice configurations to save and reload synthesized PCM audio from local cache directory (`dialect_tts_cache`), minimizing API usage.
* **Micro-Animated UI**: Displays dynamic soundwave frequency indicators (`SpeechWaves`) that pulse rhythmically during voice playback.

---

## 🛠️ Architecture

* **UI Layer**: Built entirely in **Jetpack Compose** using Material 3 guidelines and custom responsive grids (`FlowRow` layouts for flexible chip wrapping).
* **State Management**: Orchestrated by `DialectViewModel` with a reactive `DialectUiState` flow (`Idle`, `Loading`, `Playing`, `Error`).
* **API Engine**: Uses **Ktor HTTP Client** (with OkHttp engine and `kotlinx.serialization` JSON parser) to make requests to the Google AI Studio Gemini API endpoint.
* **Audio Playback**: Implemented using Android's native **AudioTrack** API in static mode, configured to feed raw 24kHz mono 16-bit linear PCM audio byte streams.

---

## 📦 File Structure

The project has been structured cleanly to make it highly modular:

* [MainActivity.kt](file:///Users/joelewis/Code/gemini-tts/app/src/main/java/com/example/geminittsdialects/MainActivity.kt): Application entry point, theme setup, and status/navigation bar styling.
* [DialectSynthesizerScreen.kt](file:///Users/joelewis/Code/gemini-tts/app/src/main/java/com/example/geminittsdialects/ui/DialectSynthesizerScreen.kt): Compose UI layout containing dropdowns, segmented buttons, cards, filter chips, and the custom pulsing audio spectrum visualizer.
* [DialectViewModel.kt](file:///Users/joelewis/Code/gemini-tts/app/src/main/java/com/example/geminittsdialects/viewmodel/DialectViewModel.kt): Manages states, triggers API requests, controls play/stop, hashes inputs, and saves/reads from the local caching system.
* [DialectRepository.kt](file:///Users/joelewis/Code/gemini-tts/app/src/main/java/com/example/geminittsdialects/api/DialectRepository.kt): Constructs the Gemini API JSON payloads and parses returned base64 inline audio data.
* [DialectPcmPlayer.kt](file:///Users/joelewis/Code/gemini-tts/app/src/main/java/com/example/geminittsdialects/api/DialectPcmPlayer.kt): Runs background coroutines to write raw PCM arrays directly to the native Android speaker track.
* [DialectModels.kt](file:///Users/joelewis/Code/gemini-tts/app/src/main/java/com/example/geminittsdialects/models/DialectModels.kt): Holds static language and dialect declarations, pre-filled default scripts, and detailed system prompt guidelines.

---

## 🚀 Getting Started

### 1. Requirements
* Android Studio (Ladybug or newer)
* Android SDK 31 (Android 12) or higher
* Google AI Studio Gemini API Key

### 2. Configure API Key
When the application starts, it will check if an API key is saved on the device. If missing, it opens a secure key overlay. You can obtain your key from [Google AI Studio](https://aistudio.google.com/app/apikey). You can also click the gear icon in the top-right corner to change the API key at any time.

### 3. Build & Run
You can compile and build the APK directly using Gradle:
```bash
cd /Users/joelewis/Code/gemini-tts
./gradlew assembleDebug
```
The compiled debug APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`
