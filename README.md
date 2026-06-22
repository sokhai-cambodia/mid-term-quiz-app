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
- **UI:** Android Views + XML layouts, with ViewBinding in Activities (RecyclerView adapters still use `findViewById`); no Jetpack Compose
- **Architecture:** Simple MVVM (no DI framework, no domain/use-case layer — see below)
- **Async:** Kotlin Coroutines + `Flow` / `StateFlow`
- **Persistence:** Room

| Tool | Version |
|---|---|
| AGP | 9.2.1 |
| Kotlin | 2.2.10 |
| KSP | 2.3.2 |
| Room | 2.8.4 |
| Lifecycle | 2.8.7 |
| Coroutines | 1.10.1 |
| compileSdk / targetSdk | 36 |
| minSdk | 30 |

## Architecture

A flat, two-layer **Simple MVVM**: `data` for everything storage-related, `ui` for everything the user sees. No dependency-injection framework, no mappers, no use-case layer — ViewModels call `QuizRepository` directly.

```
com.group4.quizapp/
├── QuizApplication.kt          # Applies saved dark-mode preference at startup
├── data/
│   ├── model/                  # Question, QuizResult, QuizAttemptDetail
│   │                           #   — same @Entity classes used by Room AND the UI
│   ├── local/                  # QuizDao, QuizDatabase, DatabaseSeeder
│   └── QuizRepository.kt       # The only data manager for the whole app (singleton)
├── ui/                         # One package per screen: Activity + ViewModel (+ Adapter)
│   ├── base/                   # BaseActivity<VB> — shared onCreate template (insets, ViewBinding)
│   ├── splash/  main/  quiz/  result/
│   └── history/  leaderboard/  details/  settings/
└── utils/
    └── PreferencesManager.kt   # SharedPreferences wrapper (dark mode, timer duration)
```

- **ViewModels** are `AndroidViewModel`s — each one builds its own `QuizRepository` via `QuizRepository.getInstance(application)` (a manual singleton, no DI). They expose state as `StateFlow`. `QuizViewModel` consolidates its quiz-session state into a single `QuizUiState` data class (one `StateFlow`) rather than separate flows per field; the other ViewModels expose simpler per-screen flows directly.
- **All Activities extend `BaseActivity<VB : ViewBinding>`** (`ui/base/BaseActivity.kt`), which handles ViewBinding inflate, `enableEdgeToEdge()`, and window-inset padding in one place, and lazily provides a `PreferencesManager`. Activities only implement `initViews()` (required) and optionally `setupObservers()`. Dark mode is applied exactly once, globally, in `QuizApplication.onCreate()` (and re-applied directly by `SettingsActivity` when toggled) — not per-Activity.
- **No use-case layer:** ViewModels call `repository.getQuestions(...)`, `repository.insertResult(...)`, etc. directly — a straight line of `Activity → ViewModel → Repository → Room`.
- **History and Leaderboard are reactive end-to-end:** the DAO returns `Flow<List<...>>` for those queries, so the UI updates automatically whenever the underlying table changes (e.g. clearing history or finishing a quiz) — no manual reload calls.
- **`getQuestions`** (one quiz session) and **`getAttemptDetails`** (one result's breakdown) stay as one-shot `suspend fun` calls, since they don't need to react to later changes.

## Project structure

```
app/
├── src/
│   ├── main/         # App source, resources, manifest
│   └── androidTest/   # Instrumented tests (Room DAO)
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
./gradlew connectedAndroidTest      # instrumented tests (Room DAO) — requires a device/emulator
```

There are currently no JVM unit tests: `QuizViewModel` and friends are `AndroidViewModel`s that build their own `QuizRepository` internally, so they can't be exercised with a fake repository without reintroducing a DI seam or adding Robolectric.

## Notes

- The question bank (60 questions across 4 categories × 3 difficulties) is wiped and reseeded from `data/local/DatabaseSeeder.kt` every time the Main screen loads. There's no remote source — everything ships in-app.
- Room uses `fallbackToDestructiveMigration()`, so a schema version bump wipes existing data rather than migrating it.
