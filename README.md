# LONA AI — Futuristic Native Android Assistant

**LONA AI** is an advanced, high-performance Android AI assistant engineered with Jetpack Compose, Material 3, and Kotlin Coroutines. It features a cyberpunk holographic visual interface, a real procedural rotating 3D Earth globe, a real-time speech pipeline, a multi-file sandboxed coding matrix, and a 15-category system permission matrix.

---

## ⚡ Zero-Firebase Architecture

LONA AI operates completely independent of Firebase:
- **No Google Services plugin**: No `google-services.json` requirement.
- **No Firebase Authentication**: Replaced with local self-sovereign cryptographic account storage.
- **No Firebase Firestore**: Replaced with offline local persistence (JSON repository / SQLite storage).
- **Direct Gemini REST Engine**: Directly communicates with official Google Generative Language APIs (`generativelanguage.googleapis.com`) using OkHttp and streaming Server-Sent Events (SSE).

---

## 🚀 Key Systems & Features

### 1. Futuristic Holographic Interface
- **Rotating 3D Earth Globe**: Procedural Canvas-based sphere with real rotating meridians, latitude rings, luminous continent nodes, atmospheric halos, and orbiting satellite particles.
- **Center Glowing Microphone**: Animated multi-layer neon wave halos responding dynamically to voice states (`IDLE`, `LISTENING`, `THINKING`, `SPEAKING`).
- **Cyberpunk Dark Palette**: Deep Cosmic Black (`#050814`), Dark Navy (`#0A1128`), with customizable neon accents (Neon Cyan, Electric Purple, Hot Pink, Cyber Blue).
- **Edge-Lighting**: Ambient neon glow framing active app boundaries.
- **Animated Splash Screen**: Quantum boot sequence with typewriter diagnostics and cybernetic logo pulse.

### 2. Autonomous Gemini AI Integration
- **Direct HTTPS REST API**: Connects to `gemini-2.5-flash`, `gemini-2.5-pro`, `gemini-2.0-flash`, and `gemini-1.5-flash`.
- **Live Latency Ping**: In-app handshake verification measuring real-time API roundtrip latency in milliseconds.
- **Contextual Memory**: Sends conversational turns for true multi-turn dialog.
- **Encrypted Local Storage**: API keys are securely stored on device without plain-text leakages.
- **API Key Portal**: Direct in-app navigation to [Google AI Studio API Keys](https://aistudio.google.com/apikey).

### 3. Real-Time Speech Pipeline
- **Speech Recognition**: Android `SpeechRecognizer` integration with real-time audio RMS amplitude feedback.
- **Multilingual Support**: English, Urdu (`ur-PK`), Roman Urdu, and Hindi (`hi-IN`).
- **Vocal Synthesizer**: Android `TextToSpeech` engine with granular pitch and speed modulation.
- **Persona Profiles**: Female, Male, and Korean Cybernetic archetype personas.
- **Interruption Controls**: One-tap instant audio termination.

### 4. Code Matrix & Sandboxed WebView Preview
- **Multi-File Workspace**: Tabbed editing for `index.html`, `style.css`, and `script.js`.
- **Live Preview**: Sandboxed Android `WebView` executing and rendering live web applications in real-time.
- **Starter Templates**: Cyberpunk Neural Portal, Orbital Particle Physics Canvas, and custom projects.
- **AI Code Synthesis**: Prompt LONA to write, edit, or fix HTML/CSS/JS code directly.
- **Export & Share**: System share sheet for distributing project code.

### 5. 15-Category Permission Matrix
Granular, least-privilege transparency matching Android permission standards:
1. Microphone (`RECORD_AUDIO`)
2. Camera (`CAMERA`)
3. Location (`ACCESS_FINE_LOCATION`)
4. Contacts (`READ_CONTACTS`)
5. Call Logs (`READ_CALL_LOG`)
6. Phone (`CALL_PHONE`)
7. Notifications (`POST_NOTIFICATIONS`)
8. SMS (`READ_SMS`)
9. Music & Audio (`READ_MEDIA_AUDIO`)
10. Photos & Videos (`READ_MEDIA_IMAGES`)
11. Nearby Devices (`BLUETOOTH_CONNECT`)
12. Files & Documents (SAF - Storage Access Framework)
13. Accessibility (`ACTION_ACCESSIBILITY_SETTINGS`)
14. Default Assistant (`ACTION_VOICE_INPUT_SETTINGS`)
15. Background Voice Service (`ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS`)

---

## 🛠️ GitHub Actions CI/CD & Debug APK

The automated CI workflow is configured in `.github/workflows/build-apk.yml`.

### Workflow Pipeline:
1. Triggers on `push` and `pull_request` to `main`, plus manual `workflow_dispatch`.
2. Sets up JDK 17 (Temurin).
3. Validates Gradle wrapper checksums.
4. Installs Android SDK platform tools.
5. Executes unit tests (`./gradlew testDebugUnitTest`).
6. Builds the Debug APK (`./gradlew assembleDebug`).
7. Uploads the generated APK as an artifact:
   - **Artifact Name**: `LONA-AI-debug-APK`
   - **File Location**: `app/build/outputs/apk/debug/app-debug.apk`
   - **Retention**: 14 Days

### How to Download the Debug APK:
1. Navigate to the **Actions** tab in your GitHub repository.
2. Click on the latest workflow run: **"LONA AI Android Build"**.
3. Under the **Artifacts** section at the bottom of the page, click **"LONA-AI-debug-APK"** to download the ZIP containing the installable APK.
4. Transfer and install on any Android device running Android 7.0 (API 24) or newer.

---

## 🔐 Release APK Signing Guide

To produce a production-signed Release APK (`app-release.apk`):

1. **Generate a Keystore**:
   ```bash
   keytool -genkey -v -keystore lona-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias lona_alias
   ```
2. **Configure Secrets**:
   Store the following as GitHub Actions Secrets in your repository:
   - `KEYSTORE_BASE64`: Base64 encoded contents of `lona-release-key.jks`.
   - `STORE_PASSWORD`: Keystore password.
   - `KEY_PASSWORD`: Key alias password.
   - `KEY_ALIAS`: Keystore alias name.
3. Run `./gradlew assembleRelease` to generate the signed APK.
