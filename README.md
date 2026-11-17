# Sandevistan Overdrive

A high-speed cybernetic ability mod for Minecraft Java Edition 1.21.1 built on NeoForge.

---

## 1. Overview
- **Mod Name:** Sandevistan Overdrive
- **Mod ID:** `sandevistan`
- **Minecraft Version:** 1.21.1
- **Mod Loader:** NeoForge (21.1.235+)
- **Language:** Java 21
- **Build System:** Gradle (NeoGradle userdev 7.1.38)
- **Version:** 0.1.0

---

## 2. Features

### Currently Implemented (Phase 1)
- **Keybind Activation (`V`):** Toggle rapid neural acceleration with key `V`. Pressing `V` while active allows manual cancellation.
- **Server-Authoritative State:** Managed via NeoForge Data Attachments (`AttachmentType`) and custom network payloads.
  - Active Duration: Default 8.0s (160 ticks).
  - Cooldown: Default 20.0s (400 ticks).
  - Lifecycle: `READY` -> `ACTIVE` -> `COOLDOWN` -> `READY`.
- **Transient Speed Attribute:** +300% movement speed modifier applied transiently without corrupting player data.
- **Clean State Reset:** Automatic modifier removal on death, respawn, disconnect, or dimension switch.
- **Cybernetic HUD Overlay:** Real-time on-screen status display with countdown timer and visual charge meter.
- **Automated Tests:** Unit test suite covering all state transitions and timer logic.

### Planned Features
- **Phase 2 (Movement & Dash):** Directional quick-dash (`R`) with raycast collision safety.
- **Phase 3 (Local Time Dilation):** Entity and projectile deceleration within an area of effect.
- **Phase 4 (Visual & Audio):** Motion trails, electric particles, and sound effects.
- **Phase 5 (Hardware Tiers):** Multiple cyberware tiers with varied stats and tuning.
- **Phase 6 (Multiplayer Optimization):** Conflict resolution for overlapping active fields and balance passes.

---

## 3. Requirements
- Minecraft 1.21.1
- NeoForge 21.1.235 or newer
- Java 21 (64-bit JDK/JRE)

---

## 4. Controls
- `V`: Activate / deactivate Sandevistan
- `R`: Dash (planned for Phase 2)

---

## 5. Configuration
Configuration files are saved in `.minecraft/config/`:
- `sandevistan-server.toml`: `activeDurationTicks`, `cooldownTicks`, `speedMultiplier`
- `sandevistan-client.toml`: `hudEnabled`, `hudX`, `hudY`

---

## 6. Build Instructions
Build the jar file using Gradle:
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
`build/libs/sandevistan-0.1.0.jar`

---

## 7. Status & Limitations
- Phase 1 verified via unit tests and successful jar build.
- Visual afterimages and projectile slowing are planned for subsequent phases.
