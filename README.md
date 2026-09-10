# Astro Run Save Planet

Astro Run Save Planet is a LibGDX 2D space game written in Java. The player controls an astronaut, avoids hazards, shoots an invading planet, collects items, and earns points by saving the planet. The project currently targets desktop through the LibGDX LWJGL3 backend.

## Features

- Start menu with introduction and instruction screens.
- Two playable modes:
  - **Galactic Mode**: the main endless-style game with rocks, coins, health packs, enemy bullets, scoring, and difficulty scaling.
  - **Training Mode**: a smaller practice mode focused on movement, shooting, and reaching 30 points.
- Arrow-key astronaut movement with screen-boundary limits.
- Enter-key shooting with a limited bullet supply.
- Planet rescue mechanic: hit the hostile planet five times to save it, gain 10 points, receive more bullets, and play the rescue sound.
- Moving rocks and enemy bullets that damage the astronaut.
- Collectible coins that add bullets.
- Collectible health packs that restore health up to the starting maximum.
- Pause, resume, retry, return-to-menu, and exit controls through the game UI.
- Game-over screen with current score and high-score handling.
- Background music and sound effects for menu actions, shooting, collisions, explosions, coins, health, and planet rescue.
- Explosion/thrust particle effects for astronaut destruction and movement effects.

## Game Flow

```text
AstroRunSavePlanet
        |
        v
StartScreen
  |     |       |
  |     |       +--> IntroductionScreen
  |     +----------> InstructionScreen
  +----------------> MainGameScreen (Galactic Mode)
                    TrainingScreen (Training Mode)

MainGameScreen -- astronaut health reaches zero --> GameOverScreen
GameOverScreen -- retry ------------------------> MainGameScreen
GameOverScreen -- menu -------------------------> StartScreen
```

The desktop launcher creates the game window, then `AstroRunSavePlanet` creates the shared `SpriteBatch` and opens `StartScreen`. Screen classes under `core/src/com/mygdx/game/Screens` own the menu, instructions, training, gameplay, and game-over flow.

## Controls

| Input       | Action                                                |
| ----------- | ----------------------------------------------------- |
| Up arrow    | Move up                                               |
| Down arrow  | Move down                                             |
| Left arrow  | Move left                                             |
| Right arrow | Move right                                            |
| Enter       | Fire a bullet                                         |
| Mouse/touch | Activate menu, pause, resume, retry, and exit buttons |

Movement is keyboard-based. Mouse input is used for UI buttons rather than direct astronaut movement.

## Gameplay Rules

### Astronaut

- Starts at approximately `(100, 300)` with 10 health points.
- Moves at 150 pixels per second and is constrained to the game window.
- Loses one health point when hit by a rock or enemy bullet.
- At zero health, the astronaut is animated off-screen with a particle effect and the game transitions to the game-over screen.

### Shooting and scoring

- The player starts with 10 bullets.
- The planet moves from right to left and fires aimed alien bullets periodically.
- Five successful hits save the planet and award 10 points plus 10 bullets.
- Galactic Mode increases planet speed every 20 points, up to a speed of 150.
- Galactic Mode also reduces the planet shooting interval every 20 points, down to 0.5 seconds.
- Training Mode ends its success condition when the player reaches 30 points.

### Hazards and collectibles

- Three rocks continuously scroll from right to left at random speeds between 50 and 100 pixels per second.
- A rock collision damages the astronaut and temporarily disables that rock's collision state.
- Coins move at 180 pixels per second. They are visible for about 28 seconds, then hidden for about 7 seconds before returning.
- Health packs first appear after about 30 seconds and remain available for about 7 seconds.
- Collecting a coin adds 10 bullets.
- Collecting a health pack restores one health point, without exceeding the starting maximum.
- Shooting a coin or health pack hides it temporarily but does not grant its collection reward.

## Project Structure

```text
SWE224_GAME/
|-- build.gradle                 Shared Gradle versions and module dependencies
|-- settings.gradle              Includes core and desktop modules
|-- gradle.properties            Gradle daemon and JVM settings
|-- highScore.txt                Checked-in starting high-score value
|-- assets/                      Textures, sounds, fonts, and particles
|-- core/
|   |-- build.gradle
|   `-- src/com/mygdx/game/
|       |-- AstroRunSavePlanet.java  LibGDX Game entry point
|       |-- Astronaut.java           Player entity
|       |-- Bullet.java              Player/enemy projectile entity
|       |-- Coin.java                Coin collectible
|       |-- GameSound.java           Sound loading and playback
|       |-- HealthPack.java           Health collectible
|       |-- Planet.java              Hostile/saveable planet entity
|       |-- Rock.java                Moving hazard
|       |-- Score.java               High-score persistence
|       `-- Screens/                 LibGDX screen implementations
`-- desktop/
    |-- build.gradle
    `-- src/com/mygdx/game/DesktopLauncher.java
```

### Screen classes

| Class                | Responsibility                                                   |
| -------------------- | ---------------------------------------------------------------- |
| `StartScreen`        | Main menu, mode selection, music, and navigation buttons         |
| `IntroductionScreen` | Game introduction/story screen                                   |
| `InstructionScreen`  | Instructions and control information                             |
| `MainGameScreen`     | Galactic Mode gameplay, collisions, score, pause, and difficulty |
| `TrainingScreen`     | Training gameplay and success flow                               |
| `GameOverScreen`     | Score display, high score, retry, menu, and exit                 |

## Technology Stack

- **Language:** Java 8
- **Game framework:** LibGDX 1.12.1
- **Desktop backend:** LibGDX LWJGL3
- **Build system:** Gradle with the Gradle 8.5 wrapper
- **Rendering:** LibGDX `SpriteBatch`, textures, sprites, bitmap/free-type fonts, and shape/geometry utilities
- **Audio:** LibGDX `Sound` with looping menu and gameplay background audio
- **Effects:** LibGDX particle effect files and particle textures
- **Declared libraries:** LibGDX Bullet, Box2D, FreeType, Controllers, AI, Ashley ECS, and Box2DLights

The current gameplay implementation mainly uses LibGDX graphics, input, audio, fonts, particles, and collision/geometry utilities. Some declared libraries are available through Gradle but are not central to the current game code.

## Requirements

- Windows, macOS, or Linux desktop environment
- Java Development Kit compatible with Java 8 source compatibility
- Internet access on the first build so Gradle can download Gradle 8.5 and Maven dependencies

## Build and Run

Run these commands from the project root, the directory containing `gradlew.bat` and `settings.gradle`:

### Windows

```powershell
.\gradlew.bat desktop:run
```

### macOS/Linux

```bash
./gradlew desktop:run
```

The desktop run task uses `assets/` as its working directory because the game loads files with paths such as `sounds/shoot.wav` and `backgrounds/...png`.

Other useful commands:

```text
gradlew.bat clean build       Compile and package all modules
gradlew.bat desktop:dist      Build a desktop distribution JAR
gradlew.bat desktop:debug     Launch with the Java debugger enabled
```

The default desktop window is 1200 x 800 pixels at 60 FPS and is titled **Astro Run Save Planet**.

## Assets

All runtime assets are stored in `assets/` and are included as the core module's resource directory:

| Folder             | Contents                                   |
| ------------------ | ------------------------------------------ |
| `astronaut/`       | Player images                              |
| `backgrounds/`     | Menu and gameplay backgrounds              |
| `bullets/`         | Player and enemy bullet images             |
| `buttons/`         | Menu and gameplay UI images                |
| `coins/`           | Coin images/animation frames               |
| `fonts/`           | TTF/OTF font files                         |
| `healthPack/`      | Health pack images                         |
| `particleEffects/` | Particle definitions and particle textures |
| `planets/`         | Hostile and saved planet images            |
| `rocks/`           | Rock hazard image                          |
| `sounds/`          | WAV music and sound effects                |

## High-Score Persistence

`Score.java` reads and writes `highScore.txt` using `Gdx.files.local(...)`. The repository contains an initial value of `40`, but the runtime local-storage location is determined by LibGDX and may not be the repository directory. The score is checked and saved through the game-over flow.

## Known Limitations and Maintenance Notes

- The `HealthPack` implementation refers to `HealthPack/Health_booster.png`, while the repository folder is named `healthPack/`. This works on case-insensitive Windows file systems but can fail on case-sensitive systems.
- Training Mode's continuation prompt currently resumes training rather than transitioning to Galactic Mode.
- Some screen transitions create replacement screens without consistently disposing every previous screen.
- Bullet instances may share a texture that is disposed by individual bullet cleanup; this should be reviewed if texture-related runtime errors appear.
- The instruction text mentions features such as jetpack boosts, missions, upgrades, alien ships, and levels that are not implemented as separate mechanics in the current source.
- Several declared dependencies, fonts, and asset files appear to be unused by the current implementation.
- The desktop Gradle tasks use `ignoreExitValue = true`, so an application failure may not always be reported as a Gradle task failure.

## License

No license file is currently included in the repository.
