# KiwiSDR Mobile 📻

A native Android client for connecting to and listening to [KiwiSDR](http://kiwisdr.com/) receivers around the world directly from your mobile device.

![Platform](https://shields.io)
![Language](https://shields.io[Kotlin%20/%20Java]-7F52FF)
![License](https://shields.io[MIT]-blue)

## 📱 Screenshots

| Receiver Directory | Waterfall & Spectrum | Tuner & Audio Controls |
| :---: | :---: | :---: |
| ![Directory](path/to/screenshot1.png) | ![Waterfall](path/to/screenshot2.png) | ![Controls](path/to/screenshot3.png) |

## ✨ Features

* **Public Receiver Directory:** Browse and search hundreds of public KiwiSDR nodes worldwide.
* **Direct Server Connection:** Connect directly to your own local or remote KiwiSDR via IP address, hostname, or proxy URL (`*.proxy.kiwisdr.com`).
* **Real-Time Waterfall & Spectrum:** Touch-friendly, pinch-to-zoom RF waterfall and spectrum display.
* **Full Demodulation Support:** Listen in AM, AMN, LSB, USB, CW, CWN, NBFM, and IQ modes.
* **Touch Tuning & Passband Control:** Fine-tune frequencies with a virtual VFO dial, direct keypad entry, or drag-to-adjust passband filters.
* **Favorites & Bookmarks:** Save your favorite KiwiSDR servers and frequently monitored frequencies/bands.
* **Background Audio:** Keep listening while your screen is off or while using other apps.

## 🚀 Getting Started

### Prerequisites
* Android `[8.0 (API 26)]` or higher
* An active internet connection (or local network access to a KiwiSDR)

### Installation

#### Option 1: Download APK
1. Go to the [Releases](../../releases) page.
2. Download the latest `.apk` file to your Android device.
3. Open the file and allow installation from unknown sources if prompted.

#### Option 2: Build from Source
1. Clone this repository:
   ```bash
   git clone https://github.com/[your-username]/[your-repo-name].git
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync the project dependencies.
4. Connect an Android device or start an emulator, then click **Run** (`Shift + F10`).

## 🛠️ Tech Stack

* **Language:** `[Kotlin / Java]`
* **UI Framework:** `[Jetpack Compose / XML Views]`
* **Networking:** `[OkHttp / Java-WebSocket]` (for KiwiSDR audio & waterfall WebSocket streams)
* **Audio Processing:** `[Android AudioTrack / Oboe / ADPCM decoder]`
* **Graphics:** `[Canvas / OpenGL ES]` (for high-FPS waterfall rendering)

## 🗺️ Roadmap

- [ ] S-meter and SNR readout
- [ ] Audio recording to `.wav`
- [ ] DX cluster / EiBi shortwave schedule overlays
- [ ] KiwiSDR extension support (FT8, WSPR, FAX, etc.)
- [ ] Android Auto support

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](../../issues).

1. Fork the project.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

## 📄 License

Distributed under the `[MIT]` License. See `LICENSE` for more information.

## 🙏 Acknowledgments

* [KiwiSDR](http://kiwisdr.com/) by John Seamons (ZL/KF6VO) for the open-source SDR platform and protocol.
* Open-Source Repo: https://github.com/jks-prv/KiwiSDR
