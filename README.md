# QuizApp

A native Android trivia quiz app built with Kotlin. Pick a category and difficulty, answer timed multiple-choice questions, and track your results on a History screen and a Leaderboard.

## Features

- **Categories:** Science, Math, History, General — each with Easy / Medium / Hard difficulty
- **Timed quiz:** configurable per-question countdown timer (15 / 30 / 60 / 90s), 5 random questions per category+difficulty
- **Results:** score, percentage message, and time spent after each attempt
- **History:** searchable list of past attempts (by category/difficulty), with a per-attempt detail breakdown (correct/incorrect/timed-out per question) and a clear-all action
- **Leaderboard:** all attempts ranked by score
- **Settings:** dark mode toggle, timer duration, clear history
- **Persistence:** all questions and results are stored locally in a Room (SQLite) database — the question bank is seeded automatically on first launch

## Tech stack

- **Language:** Kotlin
- **UI:** Android Views + XML layouts (`findViewById`, no Jetpack Compose)
- **Architecture:** Clean Architecture (domain / data / presentation) + MVVM
- **Async:** Kotlin Coroutines + `Flow` / `StateFlow`
- **DI:** Hilt
- **Persistence:** Room
- **Build:** Gradle Kotlin DSL with a version catalog (`gradle/libs.versions.toml`)

| Tool | Version |
|---|---|
| AGP | 9.2.1 |
| Kotlin | 2.2.10 |
| KSP | 2.3.2 |
| Room | 2.8.4 |
| Hilt | 2.59.2 |
| Lifecycle | 2.8.7 |
| Coroutines | 1.10.1 |
| compileSdk / targetSdk | 36 |
| minSdk | 30 |

## Architecture

The app is split into three layers, with dependencies pointing inward (presentation → domain ← data):

```
com.group4.quizapp/
├── QuizApplication.kt          # @HiltAndroidApp entry point
├── di/                         # Hilt modules (Database, Repository bindings)
├── domain/                     # Pure Kotlin, no Android/Room dependencies
│   ├── model/                  # Question, QuizResult, QuizAttemptDetail
│   ├── repository/             # QuizRepository interface
│   └── usecase/                # One use case per business operation
├── data/                       # Implements the domain layer
│   ├── local/                  # Room entities, DAO, Database
│   ├── mapper/                 # Entity <-> domain model mapping
│   ├── repository/             # QuizRepositoryImpl
│   └── seed/                   # DatabaseSeeder (question bank)
├── ui/                         # One package per screen: Activity + ViewModel (+ Adapter)
│   ├── splash/  main/  quiz/  result/
│   └── history/  leaderboard/  details/  settings/
└── utils/
    └── PreferencesManager.kt   # SharedPreferences wrapper (dark mode, timer duration)
```

- **ViewModels** are `@HiltViewModel`, constructor-injected with use cases, and expose state as `StateFlow`. They contain no Android `Application`/`Context` dependency.
- **Use cases** wrap repository-touching operations (`GetQuestionsUseCase`, `SaveQuizResultUseCase`, `ObserveHistoryUseCase`, etc.). Trivial pure computations (score math, button colors) stay in the ViewModel/View layer.
- **History and Leaderboard are reactive end-to-end:** the DAO returns `Flow<List<...>>` for those queries, so the UI updates automatically whenever the underlying table changes (e.g. clearing history or finishing a quiz) — no manual reload calls.
- **`getQuestions`** (one quiz session) and **`getAttemptDetails`** (one result's breakdown) stay as one-shot `suspend fun` calls, since they don't need to react to later changes.

## Project structure

```
app/
├── src/
│   ├── main/         # App source, resources, manifest
│   ├── test/          # JVM unit tests (ViewModels)
│   └── androidTest/    # Instrumented tests (Room DAO)
├── build.gradle.kts
gradle/
└── libs.versions.toml  # Centralized dependency versions
build.gradle.kts         # Root project config
settings.gradle.kts
```

## Getting started

### Prerequisites

- Android Studio (recent stable channel, matching AGP 9.2.1 / Kotlin 2.2.10)
- Android SDK with platform 36 installed
- A device or emulator running API 30+

### Build & run

```bash
./gradlew assembleDebug      # build a debug APK
./gradlew installDebug       # build and install on a connected device/emulator
```

Or just open the project root in Android Studio and run the `app` configuration.

### Tests

```bash
./gradlew testDebugUnitTest        # JVM unit tests (ViewModels)
./gradlew connectedAndroidTest      # instrumented tests (Room DAO) — requires a device/emulator
```

## Notes

- The question bank (60 questions across 4 categories × 3 difficulties) is wiped and reseeded from `data/seed/DatabaseSeeder.kt` every time the Main screen loads. There's no remote source — everything ships in-app.
- Room uses `fallbackToDestructiveMigration()`, so a schema version bump wipes existing data rather than migrating it.
