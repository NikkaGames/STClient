
# STClient

**STClient** is a highly obfuscated, performance-optimized Android native client written in C++ with JNI bindings. Designed for runtime enhancement of Unity-based games, it incorporates secure encrypted communication, ESP overlays, inventory manipulation, aimbotting, and advanced UI-interaction through a floating window interface.

> ⚠️ This project is intended strictly for educational and research use only.

## 🎯 Key Capabilities

- **ESP Overlay Rendering**: Real-time drawing of hitboxes, health bars, nicknames, lines, and UI elements based on live positional data.
- **Aimbot & Visuals**: Auto-aim via circular crosshair, supported by configurable radius settings.
- **Encrypted Command Protocol**: XOR and AES-based encryption on UDP for integrity-verified messaging.
- **Dynamic Feature Switching**: In-app toggles and sliders via JNI interface to a Kotlin-based UI.
- **Item Management**: Skin and inventory changers with JSON-based dynamic injection.
- **Cheat Modules**: Fast knife, fire rate, no recoil, bunnyhop, wallshot, grenade nukes, and more.
- **Self-Integrity & Anti-Tamper**: Obfuscation, control flow annotations, recursion traps, and emulator checks.

## 🧱 Architecture Overview

```
+-----------------------------+
| Android App (Floating UI)  |
+-------------+---------------+
              |
              v
+-----------------------------+
| JNI Interface (Kotlin <-> C++) |
+-----------------------------+
              |
              v
+-----------------------------+
| Feature Dispatcher (Cheat Handler) |
+-----------------------------+
              |
              +--> ESP Renderer (Framebuffer)
              +--> UDP Client (Encrypted Channel)
              +--> Much more?..
```

## 📦 Dependencies

- Android NDK (C++17)
- RapidJSON
- KittyMemory
- Custom AES and Base64 modules
- Unity-based game process
- JNI and libil2cpp presence

## 🛠️ Build Instructions

```bash
# Ensure Android NDK environment is configured
Build with Android Studio (r24 NDK recommended)
```

## 📡 Runtime Behavior

- UDP port `19132` is used for client-side command dispatch.
- UDP port `19133` is used for incoming ESP state rendering data.
- Secure handshake performed at initialization using AES key exchange and server timestamp validation.
- Obfuscated strings, logic branches, and conditional paths restrict reverse engineering attempts.

## 🔐 Security & Obfuscation

- Extensive use of `OBFUSCATE()` macros, fake branching (`Bloat<N>`), and recursive traps.
- JNI URL requests made with fake headers and string decoys.
- Library tamper-detection (`frida`, `vmos`, `appcloner`) and system property validation.
- All event data XOR-encrypted and hex-encoded over network.

## 📋 Example Feature Set (UI)

- ✅ Skin Changer
- 🎮 Weapon Modifier
- 🎯 Aimbot
- 📦 Inventory Enhancer
- 🧱 ESP Box/Line/Filled/Health/Nick
- 🚀 Fast Knife, Bunnyhop, No Recoil
- 💣 Unlimited Grenades, Fast Bomb
- 🌈 Chams & Visual Effects

## 📜 License

This software is proprietary and not for illegal use or redistribution. Any attempt to reverse, republish, or commercialize this project is strictly prohibited.
