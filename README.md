# Sensor Lamp

A NeoForge mod for Minecraft 1.21.1 that adds lamps, lanterns and a player
sensor that switch on when a player comes near — a quiet alternative to the
sculk sensor.

- **Blocks:** Sensor Lamp, Sensor Lantern / Soul Lantern (standing, hanging
  and wall-mounted), Player Sensor.
- **Hidden sensors:** attach a sensor to other blocks; crouch + right-click
  with an empty hand to change its settings.
- Detection range and off delay are configurable per sensor, with defaults
  for newly placed sensors.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.265 or newer

## Download

Get the jar from [GitHub Releases](https://github.com/Fallatori/Sensorlamp/releases).
Each release lists the SHA-256 checksum of the jar, and is tagged with the
matching source (`vX.Y.Z`).

### packwiz

```sh
packwiz url add "Sensor Lamp" https://github.com/Fallatori/Sensorlamp/releases/download/v1.1.1/sensorlamp-1.1.1.jar
```

## Building

```sh
./gradlew build
```

The jar is written to `build/libs/`. Archive timestamps and file order are
fixed, so building a release tag with the same JDK version (noted in the
release) should reproduce the checksum published on that release.

## License

Code and original assets: MIT, Copyright (c) 2026 Fallatori — see
[LICENSE](LICENSE).

Third-party material and attribution (NeoForged MDK build template, references
to vanilla Minecraft resources) are described in [NOTICE.md](NOTICE.md).
`LICENSE`, `NOTICE.md` and `LICENSE-NeoForged-MDK.txt` are also included in the
jar under `META-INF/`.

Not an official Minecraft product. Not approved by or associated with Mojang
or Microsoft.
