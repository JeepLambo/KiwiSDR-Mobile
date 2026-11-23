# KiwiSDR Mobile (Android)

An open-source Android mobile client and APK for browsing and listening to [KiwiSDR](http://kiwisdr.com) shortwave/VLF-HF receivers on Android devices.

## Features
- Connect to public KiwiSDR receivers (`://kiwisdr.com` / `://kiwisdr.com`) or custom KiwiSDR server URLs
- [Add 2–3 specifics about your build — e.g., Mobile-friendly WebView / native WebSocket streaming]
- [e.g., Audio playback and waterfall display on Android]
- [e.g., Minimum Android version supported, such as Android 8.0+]

## Installation (APK)
1. Download the latest `kiwisdr.apk` from the [Releases](https://github.com) page (or directly from the root of this repository).
2. On your Android device, open the downloaded `.apk` file.
3. If prompted, allow **Install from unknown sources** for your browser or file manager.
4. Launch **KiwiSDR Mobile** and select a receiver to start listening.

## Building from Source
The Android project source files are located in the [`/android`](./android) directory.
1. Clone this repository:
   ```bash
   git clone https://github.com
   ```
2. Open the `android/` folder in **Android Studio**.
3. Let Gradle sync, then build and run on your device or emulator (**Build -> Build Bundle(s) / APK(s) -> Build APK(s)**).

## Credits & Upstream License
- Based on the open-source [KiwiSDR (`Beagle_SDR_GPS`)](https://github.com) project by John Seamons (`jks-prv`).
- See `_LICENSE` and `_COPYRIGHT` in this repository for upstream licensing details.
