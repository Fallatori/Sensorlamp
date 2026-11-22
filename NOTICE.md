# Sensor Lamp — Notices and Attribution

Sensor Lamp is a NeoForge mod for Minecraft 1.21.1.
Author: Fallatori — source: https://github.com/Fallatori/Sensorlamp

## Original work (MIT)

Unless stated otherwise below, the Java source code, data files, language
files, block/item models and textures in this repository and in the
distributed jar are Copyright (c) 2026 Fallatori and licensed under the MIT
License (see `LICENSE`). This covers, in particular:

- `textures/block/sensor_lamp.png`, `sensor_lamp_on.png`,
  `player_sensor.png`, `player_sensor_on.png` — drawn by Fallatori.
- `textures/block/lantern_off_overlay.png`,
  `textures/block/soul_lantern_off_overlay.png`,
  `textures/item/sensor_indicator.png` — drawn programmatically from
  hand-chosen colours by `tools/GenerateOverlayTextures.java`; no existing
  image is read or sampled.

## Third-party material

### NeoForged MDK (MIT)

The Gradle build scripts, Gradle wrapper scripts and the
`META-INF/neoforge.mods.toml` template are derived from the NeoForged Mod
Development Kit, Copyright (c) 2023 NeoForged project, MIT License.
The full notice is in `LICENSE-NeoForged-MDK.txt`.

### Gradle wrapper (Apache-2.0) — repository only

`gradle/wrapper/gradle-wrapper.jar`, `gradlew` and `gradlew.bat` are part
of Gradle (Apache License 2.0). They are used to build the mod and are
**not** included in the distributed mod jar.

### Minecraft (Mojang) — referenced, not bundled

The mod jar does **not** contain any Minecraft textures. Several models
*reference* resources from the user's own installed copy of Minecraft by
resource location, which the game resolves at runtime:

- Parent models `minecraft:block/cube_all`, `minecraft:block/block`,
  `minecraft:block/template_lantern`,
  `minecraft:block/template_hanging_lantern`, `minecraft:item/generated`.
- Textures `minecraft:block/lantern`, `minecraft:block/soul_lantern`,
  `minecraft:item/lantern`, `minecraft:item/soul_lantern`. The mod draws
  its own overlays (above) on top of these.

`models/block/template_wall_lantern.json` is original work by Fallatori,
created from scratch for this mod. Its lantern body and lid are dimensioned
to match Minecraft's hanging lantern (same box coordinates and UV
rectangles) so that the vanilla lantern texture maps onto it identically.
The two overlay templates likewise use the lantern body/lid box dimensions
so they line up with the vanilla geometry.

Minecraft is a trademark of Mojang Synergies AB / Microsoft. Sensor Lamp
is not an official Minecraft product and is not approved by or associated
with Mojang or Microsoft. Use of Minecraft itself is governed by the
Minecraft EULA and Usage Guidelines.

### Compile-time APIs (not bundled)

The mod compiles against Minecraft, NeoForge, Mixin and their libraries,
using Mojang's official mappings and Parchment parameter names. None of
these are included in the mod jar; they are provided by the game and mod
loader at runtime.
