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

## 2. Features

### Phase 1 — Foundation
- **Keybind Activation (`V`):** Toggle rapid neural acceleration with key `V`. Pressing `V` while active allows manual early cancellation.
- **Server-Authoritative State:** Managed via Forge Capabilities (`ICapabilitySerializable<CompoundTag>`) and synchronized across network boundaries using Forge `SimpleChannel`.
  - Active Duration: Default 8.0s (160 ticks).
  - Cooldown: Default 20.0s (400 ticks).
  - Lifecycle: `READY` -> `ACTIVE` -> `COOLDOWN` -> `READY`.
- **Transient Speed Attribute:** +300% movement speed modifier applied transiently (`AttributeModifier.Operation.MULTIPLY_BASE`) via persistent UUID.
- **Clean State Reset:** Automatic modifier removal on death, respawn, disconnect, or dimension switch.
- **Cybernetic HUD Overlay:** Real-time on-screen status display registered via Forge `RegisterGuiOverlaysEvent` / `IGuiOverlay` with countdown timer and visual charge meter.

### Phase 2 — Movement & Dash
- **Directional Dash (`R`):** High-speed multi-directional dash (W, A, S, D, diagonals, jump, or look direction) while Sandevistan is `ACTIVE`.
- **Server-Authoritative Validation:** Input directions sanitized (NaN/Infinity protection, normalized diagonal vectors so diagonal speeds never exceed limits).
- **Collision Safety:** Swept AABB checks and world border checks prevent phasing through solid blocks or getting stuck.
- **Air Dash:** Configurable aerial dash support with upward lift handling.
- **Dash Charge & Cooldown:** Default 2 charges with 1.0s (20 ticks) recharge cooldown displayed directly on the HUD.
- **Fall Distance Reset:** Resets accumulated fall distance at the start of a dash without granting permanent fall immunity.

### Phase 3 — Local Time Dilation
- **Localized Field (24 Blocks):** Spherical dilation field centered on active players without altering global server TPS or invoking `Thread.sleep`.
- **Mob Slowdown (Tier A & Kinetic Dilation):** Mobs inside radius suffer an 80% speed reduction (`dilationFactor = 0.2`, 5x slower) via transient attribute modifiers and velocity damping.
- **Projectile Slowdown:** Flying projectiles (arrows, tridents, fireballs, throwables) are slowed down to match the visual dilation factor, with trajectory gravity compensation preventing unnatural immediate falling.
- **Multiplayer Overlap Resolution:** When multiple players activate Sandevistan concurrently, entities in intersecting fields maintain consistent 0.2 factor (no compounding) and only restore when the last covering field deactivates.
- **Immunity & Selectivity:** Activating players and other Sandevistan users are immune to dilation; passive/hostile mobs and projectiles are selectively configurable.
- **Leak-Free Entity Lifecycle:** Tracked entities are automatically cleaned up on death, despawn, chunk unload, or dimension change.

---

## 3. Requirements
- Minecraft `1.20.1` with [Minecraft Forge](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html) `47.4.4` or newer.
- Java 17 (64-bit JDK/JRE).

---

## 4. Controls
- `V`: Activate / deactivate Sandevistan
- `R`: Directional Dash (when Sandevistan is ACTIVE)
  - `W + R`: Forward Dash
  - `S + R`: Backward Dash
  - `A + R`: Left Dash
  - `D + R`: Right Dash
  - `W + A + R` / `W + D + R`: Diagonal Dash
  - `Space + R`: Jump Dash / Air Dash
  - `R` (alone): Dash toward current look orientation

---

## 5. Configuration
Configuration files are automatically generated in `.minecraft/config/`:
- `sandevistan-common.toml`:
  - `gameplay`: `activeDurationTicks` (160), `cooldownTicks` (400), `speedMultiplier` (3.0)
  - `dash`: `dashEnabled` (true), `dashCooldownTicks` (20), `dashSpeed` (1.25), `dashDistance` (5.0), `dashAirEnabled` (true), `dashMaxCharges` (2)
  - `timeDilation`: `timeDilationEnabled` (true), `timeDilationRadius` (24.0), `timeDilationFactor` (0.2), `affectHostileMobs` (true), `affectPassiveMobs` (true), `affectProjectiles` (true), `affectOtherPlayers` (false), `dilationScanIntervalTicks` (2), `maxAffectedEntities` (64)
- `sandevistan-client.toml`:
  - `hud`: `hudEnabled` (true), `hudX` (10), `hudY` (10)

---

## 6. Build Instructions
Build the jar file using Gradle with Java 17:

- Windows:
  ```powershell
  .\gradlew.bat clean build --no-parallel
  ```
- Linux / macOS:
  ```bash
  ./gradlew clean build --no-parallel
  ```

Run unit tests:
```powershell
.\gradlew.bat test --no-parallel
```

Compiled jar location:
`build/libs/sandevistan-0.1.0.jar`

---

## 7. Status & Technical Verification
- **Phase 1, Phase 2, Phase 3 Fully Implemented & Verified:**
  - 23/23 Unit Tests Passed across State FSM, Dash math, and Time Dilation multi-player overlap tracking.
  - Dedicated Server 1.20.1 booted cleanly in 7.1s with 0 crashes, 0 classloader leaks, and fully functional capabilities.
  - Reobfuscated JAR size: ~54 KB.
