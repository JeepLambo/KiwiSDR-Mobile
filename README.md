# KiwiSDR Mobile (Android)

An open-source Android mobile client and APK for browsing and listening to [KiwiSDR](http://kiwisdr.com) shortwave/VLF-HF receivers on Android devices.

## Requirements
- **Minimum OS:** Android 7.0 (Nougat / API 24) or higher
- **Target OS:** Android 15 (API 35)

## Installation (APK)
1. Download `kiwisdr.apk` from the [Releases](https://github.com) page (or from the root of this repository).
2. Open the `.apk` file on your Android device and allow **Install from unknown sources** if prompted.
3. Launch **KiwiSDR Mobile** and choose a receiver to start listening.

## Building from Source
1. Clone this repository:
   ```bash
   git clone https://github.com/JeepLambo/KiwiSDR-Mobile
   ```
2. Open the `android/` directory in **Android Studio**.
3. Sync Gradle and build via **Build -> Build Bundle(s) / APK(s) -> Build APK(s)**.

## Credits & Upstream License
- Based on the open-source [KiwiSDR (`Beagle_SDR_GPS`)](https://github.com) project by John Seamons (`jks-prv`).
- See `_LICENSE` and `_COPYRIGHT` for upstream licensing details.
