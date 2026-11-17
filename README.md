# Sandevistan Overdrive (Forge 1.20.1)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.4.4-blue.svg)](https://files.minecraftforge.net/)
[![Java](https://img.shields.io/badge/Java-17%20LTS-blue.svg)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A high-speed cybernetic ability mod for **Minecraft Java Edition 1.20.1** built on **Minecraft Forge 47.4.4** and **Java 17**.

---

## 1. Overview
- **Mod Name:** Sandevistan Overdrive
- **Mod ID:** `sandevistan`
- **Minecraft Version:** 1.20.1
- **Mod Loader:** Minecraft Forge (47.4.4+)
- **Language:** Java 17 LTS
- **Build System:** Gradle (ForgeGradle 6.0)
- **Version:** 0.1.0

---

## 2. Features (Phase 1 — Foundation)

- **Keybind Activation (`V`):** Toggle rapid neural acceleration with key `V`. Pressing `V` while active allows manual early cancellation.
- **Server-Authoritative State:** Managed via official Forge Capabilities (`ICapabilitySerializable<CompoundTag>`) and synchronized across network boundaries using Forge `SimpleChannel`.
  - Active Duration: Default 8.0s (160 ticks).
  - Cooldown: Default 20.0s (400 ticks).
  - Lifecycle: `READY` -> `ACTIVE` -> `COOLDOWN` -> `READY`.
- **Transient Speed Attribute:** +300% movement speed modifier applied transiently (`AttributeModifier.Operation.MULTIPLY_BASE`) via persistent UUID.
- **Clean State Reset:** Automatic modifier removal on death, respawn, disconnect, or dimension switch.
- **Cybernetic HUD Overlay:** Real-time on-screen status display registered via Forge `RegisterGuiOverlaysEvent` / `IGuiOverlay` with countdown timer and visual charge meter.
- **Automated Tests:** Built-in JUnit 5 unit test suite covering all state transitions and timer logic.

---

## 3. Requirements
- Minecraft `1.20.1` with [Minecraft Forge](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html) `47.4.4` or newer.
- Java 17 (64-bit JDK/JRE).

---

## 4. Controls
- `V`: Activate / deactivate Sandevistan
- `R`: Dash (planned for Phase 2)

---

## 5. Configuration
Configuration files are automatically generated in `.minecraft/config/`:
- `sandevistan-common.toml`: `activeDurationTicks`, `cooldownTicks`, `speedMultiplier`
- `sandevistan-client.toml`: `hudEnabled`, `hudX`, `hudY`

---

## 6. Build Instructions
Build the jar file using Gradle with Java 17:

- Windows:
  ```powershell
  .\gradlew.bat clean build
  ```
- Linux / macOS:
  ```bash
  ./gradlew clean build
  ```

Run unit tests:
```powershell
.\gradlew.bat test
```

Compiled jar location:
`build/libs/sandevistan-1.20.1-0.1.0.jar` (or `sandevistan-0.1.0.jar`)

---

## 7. Status & Limitations
- Phase 1 backport to Forge 1.20.1 verified via unit tests, full jar compilation, and dedicated server startup.
- Phase 2 (Dash) and Phase 3 (Time Dilation) are planned for subsequent phases.
