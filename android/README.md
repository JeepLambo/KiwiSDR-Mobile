# 🥝 KiwiSDR Mobile Client for Android

A lightweight, memory-optimized Android application for accessing **KiwiSDR Software Defined Radio (SDR)** receivers from Android phones and tablets.

---

## 🌟 Key Features

* 🚀 **Instant Offline UI Loading**: Bundles static KiwiSDR web client assets (`kiwisdr.min.js`, `kiwisdr.min.css`) directly inside the APK for instant launch without downloading scripts over the internet.
* 🔊 **WebAudio & WebSocket Streaming**: Direct support for real-time WebSockets (`SND` audio and `W/F` waterfall spectrum data) and unblocked HTML5 WebAudio playback.
* ⚡ **Lightweight & Low Memory Footprint**: Configured with automatic view teardown and memory cleanup on exit to prevent background battery drain and RAM leaks.
* 📱 **Mobile Touch Friendly**: Full-screen display with Android back-gesture navigation support (`BackHandler`).
* 🎨 **Official Mascot Icon**: High-resolution Kiwi bird with headphones launcher icon across all density buckets (`mdpi` through `xxxhdpi`).

---

## 📥 How to Install `kiwisdr.apk` on Android

### Method 1: Manual Transfer via USB
1. Connect your Android tablet or phone to your PC using a USB cable.
2. Set the USB connection mode on your Android device to **File Transfer (MTP)**.
3. Copy `kiwisdr.apk` from `C:\Users\ishma\KiwiSDR\kiwisdr.apk` into your device's **Downloads** folder.
4. On your Android device, open the **Files** (or **My Files**) app.
5. Navigate to **Downloads** and tap **`kiwisdr.apk`**.
6. If Android displays a security prompt (*"Install unknown apps"*), tap **Settings** and toggle **Allow from this source**.
7. Tap **Install**, then tap **Open** to launch KiwiSDR Mobile!

### Method 2: Command Line via ADB
If USB Debugging is enabled on your device:
```cmd
adb install C:\Users\ishma\KiwiSDR\kiwisdr.apk
```

---

## 🛠 How It Works (Architecture)

### 📂 Directory & File Structure
```
android/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   ├── index.html            # Local HTML host page
│   │   │   ├── kiwisdr.min.js        # KiwiSDR JavaScript client engine
│   │   │   └── kiwisdr.min.css       # KiwiSDR stylesheet
│   │   ├── java/com/kiwisdr/mobile/
│   │   │   ├── MainActivity.kt       # Activity entry point
│   │   │   └── ui/
│   │   │       └── KiwiWebViewScreen.kt # Memory-optimized WebView component
│   │   ├── res/                      # Icons, layouts, and strings
│   │   └── AndroidManifest.xml       # App configuration & permissions
│   └── build.gradle.kts              # Android app build configuration
├── build.gradle.kts                  # Root project build configuration
└── settings.gradle.kts               # Module settings
```

### ⚙️ Engine Details
* **Local Asset Host**: The app opens `file:///android_asset/index.html` on startup.
* **Network Permissions**: Configured with `INTERNET`, `ACCESS_NETWORK_STATE`, `usesCleartextTraffic="true"`, and `MIXED_CONTENT_ALWAYS_ALLOW` so both HTTP and HTTPS KiwiSDR servers load audio and waterfall streams smoothly.
* **Auto Permission Granting**: `WebChromeClient.onPermissionRequest` automatically grants WebAudio and microphone permissions without popping up browser consent prompts.

---

## 🔨 How to Rebuild the APK from Source

To compile or modify the application:

1. Open PowerShell and navigate to the project directory:
   ```powershell
   cd C:\Users\ishma\KiwiSDR\android
   ```

2. Execute the Gradle build script:
   ```powershell
   $env:JAVA_HOME="C:\Program Files\Unity\Hub\Editor\6000.6.8f1\Editor\Data\PlaybackEngines\AndroidPlayer\OpenJDK"
   $env:ANDROID_HOME="C:\Users\ishma\AppData\Local\Android\Sdk"
   Remove-Item Env:\ANDROID_SDK_HOME -ErrorAction SilentlyContinue
   Remove-Item Env:\ANDROID_PREFS_ROOT -ErrorAction SilentlyContinue
   $env:ANDROID_USER_HOME="C:\Users\ishma\.android"

   .\gradlew.bat assembleDebug --no-daemon
   ```

3. The generated APK file will be saved at:
   `C:\Users\ishma\KiwiSDR\kiwisdr.apk`
