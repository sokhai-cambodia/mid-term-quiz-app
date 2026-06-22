# QuizApp — Project Analysis

## 1. Tech Stack

| Layer | Technology |
|---|---|
| Language | **Kotlin** (2.2.10), 100% Kotlin codebase |
| UI Toolkit | **XML layouts** + **ViewBinding** in Activities, `findViewById` in RecyclerView adapters (no Jetpack Compose) |
| Min/Target SDK | minSdk 30, targetSdk 36, compileSdk 36 |
| Dependency Injection | **None** — no Hilt/Dagger; ViewModels build their own dependencies directly |
| Database | **Room** 2.8.4 (SQLite ORM) with **KSP** annotation processing |
| Async | **Kotlin Coroutines** 1.10.1 — `Flow`, `StateFlow`, `viewModelScope`, `suspend` functions |
| Architecture Components | AndroidX Lifecycle (ViewModel, `lifecycleScope`, `repeatOnLifecycle`) 2.8.7 |
| UI Widgets | Material Components, ConstraintLayout, RecyclerView |
| Local Storage | Room (structured data) + SharedPreferences (user settings) |
| Build System | Gradle (Kotlin DSL), AGP 9.2.1, version catalog (`libs.versions.toml`) |

## 2. Architecture Pattern: Simple MVVM

This is a deliberately flat **MVVM**, organized into two packages instead of a layered Clean Architecture:

```
data/      → Everything data-related: Room entities (doubling as the UI's model classes), DAO, Database, the one Repository
ui/        → View layer (Activities + ViewModels + Adapters), one package per screen
utils/     → PreferencesManager (SharedPreferences wrapper)
```

**Dependency direction:** `ui → data`, a straight line. There is no `domain` package, no repository interface/impl split, no use-case layer, and no mapper layer — `data/model/Question.kt` (and `QuizResult`, `QuizAttemptDetail`) are Room `@Entity` classes that are also the exact types the UI binds to. ViewModels call `QuizRepository` methods directly (`repository.getQuestions(...)`, `repository.insertResult(...)`, etc.).

This trades some textbook layering purity for something a lot easier to explain end-to-end: "the `data` folder is the Model, the `ui` folder is the View/ViewModel, and `QuizRepository` is the bridge between them."

## 3. Major Components

| Component | Purpose |
|---|---|
| `QuizApplication` | Plain `Application` subclass; applies the saved dark-mode preference at app startup before any Activity is created |
| `ui/base/BaseActivity` | Abstract base class all 8 Activities extend; centralizes ViewBinding inflate, edge-to-edge setup, window-inset padding, and a lazily-created `PreferencesManager`, via a template method (`initViews()` / `setupObservers()`). Dark mode is applied once globally by `QuizApplication` at startup, not per-Activity |
| **ui/splash** | `SplashActivity` — 2-second branded delay screen, then navigates to Main |
| **ui/main** | `MainActivity`/`MainViewModel` — category (Science/Math/History/General) & difficulty (Easy/Medium/Hard) picker; triggers DB seeding; navigation hub |
| **ui/quiz** | `QuizActivity`/`QuizViewModel` — runs the 5-question quiz session, per-question countdown timer, scoring |
| **ui/result** | `ResultActivity` — shows score, percentage-based feedback message, retake/home actions |
| **ui/history** | `HistoryActivity`/`HistoryViewModel`/`HistoryAdapter` — reactive list of past attempts with live search/filter |
| **ui/details** | `QuizDetailActivity`/`QuizDetailViewModel`/`QuizDetailAdapter` — drill-down into a single attempt's per-question answers |
| **ui/leaderboard** | `LeaderboardActivity`/`LeaderboardViewModel`/`LeaderboardAdapter` — all results ranked by score |
| **ui/settings** | `SettingsActivity`/`SettingsViewModel` — dark mode toggle, timer duration, clear history |
| **data/QuizRepository** | The only data manager for the whole app — a manual singleton (`QuizRepository.getInstance(context)`) wrapping `QuizDao`; every ViewModel calls it directly, no use-case indirection |
| **data/local** | Room `QuizDatabase` (3 entities, schema v6), `QuizDao`, `DatabaseSeeder` |
| **data/model** | `Question`, `QuizResult`, `QuizAttemptDetail` — Room `@Entity` classes that double as the UI's model types (no separate domain model, no mapper) |
| **utils/PreferencesManager** | Plain wrapper over `SharedPreferences` for dark mode + timer duration, constructed directly with a `Context` (no DI) |

## 4. End-to-End Quiz Flow

```
SplashActivity (2s delay)
   → MainActivity
       - MainViewModel.seedDatabase() wipes & reseeds questions on every visit
       - user picks category + difficulty (button UI state)
       - "Start Quiz" → Intent extras (category, difficulty) → QuizActivity
   → QuizActivity (extends BaseActivity<ActivityQuizBinding>)
       - initViews(): reads category/difficulty from Intent extras, calls
         viewModel.loadQuestions(category, difficulty)
           → QuizRepository.getQuestions()
           → DAO query picks 5 RANDOM distinct questions matching category+difficulty
       - setupObservers(): collects ONE StateFlow<QuizUiState> via
         repeatOnLifecycle(STARTED), where QuizUiState bundles questions,
         currentIndex, score, isFinished, isLoading, errorMessage
       - Each question rendered via binding.xxx (ViewBinding) using string
         resources (getString(R.string.question_progress, ...), option_a..d_format)
         instead of manual string concatenation
       - Each question: CountDownTimer (duration from PreferencesManager,
         default 30s) — auto-submits "None" on timeout
       - Tapping an option → checkAnswer() colors buttons (green=correct,
         red=wrong), 1s delay, then viewModel.answerQuestion(selected)
           - updates score, advances currentIndex, or sets isFinished=true
             after 5th question — all via one state.copy() on QuizUiState
       - On isFinished:
           - QuizViewModel.saveResults() calls repository.insertResult(...)
               → inserts a QuizResult row (score/category/date/timeSpent)
               → inserts a QuizAttemptDetail row per question (full answer record)
           - Intent → ResultActivity with score/total/category/difficulty/timeSpent
   → ResultActivity
       - Displays score, percentage-based message ("Excellent! 🎉" etc.)
       - Retake → finish() (returns to MainActivity's quiz launch point)
       - Go Home → MainActivity (CLEAR_TOP)

Separately, from MainActivity:
   → HistoryActivity: reactive Flow list of all QuizResults, live search by
     category/difficulty; tap an item → QuizDetailActivity (per-question review)
   → LeaderboardActivity: all results sorted by score DESC
   → SettingsActivity: toggle dark mode (applied instantly via AppCompatDelegate),
     set timer duration, clear all history
```

## 5. Database / Storage Approach

**Room database** (`quiz_database`, schema version 6, `fallbackToDestructiveMigration`) with 3 tables:

| Entity | Purpose | Notes |
|---|---|---|
| `questions` | Question bank | category, difficulty, 4 options, correct option |
| `quiz_results` | One row per completed quiz attempt | score, totalQuestions, dateTaken, timeSpent |
| `quiz_attempt_details` | One row per question answered | Foreign key → `quiz_results.id`, CASCADE delete, indexed; records selected vs. correct option for review |

- Reads are exposed as **`Flow`** from the DAO (reactive — UI auto-updates on DB changes), writes are `suspend` functions.
- There is no separate mapper layer — the Room `@Entity` classes in `data/model/` are the same classes Activities, ViewModels, and adapters work with directly.
- **SharedPreferences** (via `PreferencesManager`) is used separately for lightweight user settings (dark mode boolean, timer duration int) — not appropriate for Room.
- Question bank is **not persisted across reinstalls** — it's wiped and reseeded from hardcoded data (`DatabaseSeeder`) every time `MainActivity` loads, while `quiz_results`/`quiz_attempt_details` (history) persist.

## 6. Key Files Reference

| File | Why it matters |
|---|---|
| `QuizApplication.kt` | Global dark-mode init at startup; otherwise a plain `Application` |
| `data/QuizRepository.kt` | The single data manager the whole app depends on — wraps `QuizDao`, exposed as a manual singleton via `getInstance(context)` |
| `data/local/QuizDao.kt` | All SQL queries, including the `ORDER BY RANDOM() LIMIT 5` question-picking logic |
| `data/local/QuizDatabase.kt` | Room database definition, entity registry, version |
| `data/model/Question.kt`, `QuizResult.kt`, `QuizAttemptDetail.kt` | Room `@Entity` classes that are also the UI's model types — no domain/mapper indirection |
| `ui/base/BaseActivity.kt` | Shared `onCreate` template (ViewBinding inflate, edge-to-edge, window insets, lazy `PreferencesManager`) all 8 Activities build on |
| `ui/quiz/QuizViewModel.kt` | Core quiz session state machine — single consolidated `QuizUiState` (questions/currentIndex/score/isFinished/isLoading/errorMessage) exposed as one `StateFlow` |
| `ui/quiz/QuizActivity.kt` | Most complex Activity — timer, answer UI via ViewBinding, single `QuizUiState` flow collected via `repeatOnLifecycle` |

---

**Presentation note:** the project shows a clear evolutionary story. It started Activity-centric with no ViewModel/LiveData (see git history). It was then refactored into a layered **Clean MVVM** — Hilt DI, a `domain/` package with a repository interface and 8 single-responsibility use cases, and an entity↔domain-model mapper layer — a step toward full Clean Architecture, beyond what a typical mid-term project needs. That layering made the dependency graph and the data flow harder to narrate to a non-technical reviewer (e.g. "why does the ViewModel call a use case that calls a repository interface that's bound to an impl via a Hilt module that converts a Room entity into a domain model and back?").

The project was then **deliberately simplified back down to flat Simple MVVM**: Hilt, the `domain/` package, the mapper layer, and all use cases were removed; ViewModels now build a `QuizRepository` singleton directly and call it without indirection; the Room `@Entity` classes in `data/model/` serve as the UI's model classes too. The result is the same `Activity → ViewModel → Repository → Room` flow described above, with one file per responsibility and nothing to explain beyond "Model is `data/`, View/ViewModel is `ui/`, `QuizRepository` is the bridge."
