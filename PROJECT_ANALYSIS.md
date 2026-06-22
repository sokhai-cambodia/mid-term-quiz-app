# QuizApp — Project Analysis

## 1. Tech Stack

| Layer | Technology |
|---|---|
| Language | **Kotlin** (2.2.10), 100% Kotlin codebase |
| UI Toolkit | **XML layouts** + `findViewById` (no Jetpack Compose) |
| Min/Target SDK | minSdk 30, targetSdk 36, compileSdk 36 |
| Dependency Injection | **Hilt** 2.59.2 (Dagger-based) |
| Database | **Room** 2.8.4 (SQLite ORM) with **KSP** annotation processing |
| Async | **Kotlin Coroutines** 1.10.1 — `Flow`, `StateFlow`, `viewModelScope`, `suspend` functions |
| Architecture Components | AndroidX Lifecycle (ViewModel, `lifecycleScope`, `repeatOnLifecycle`) 2.8.7 |
| UI Widgets | Material Components, ConstraintLayout, RecyclerView |
| Local Storage | Room (structured data) + SharedPreferences (user settings) |
| Build System | Gradle (Kotlin DSL), AGP 9.2.1, version catalog (`libs.versions.toml`) |

## 2. Architecture Pattern: Clean MVVM

This is not plain MVVM — it's a **layered Clean Architecture** with MVVM on top, organized into three packages:

```
ui/        → View layer (Activities + ViewModels + Adapters)
domain/    → Business logic (Models, Repository interface, UseCases)
data/      → Data layer (Room entities, DAO, Repository impl, Mappers)
di/        → Hilt dependency injection modules
```

**Dependency direction:** `ui → domain ← data` (the data layer depends on domain, not the other way around — classic Dependency Inversion). The `domain/repository/QuizRepository` is an **interface**; `data/repository/QuizRepositoryImpl` implements it and is bound via Hilt (`@Binds`) in `RepositoryModule`. ViewModels never touch Room directly — they only call **UseCases**, which call the repository interface.

This is more advanced than a typical university MVVM exercise — it adds a genuine domain/use-case layer (a step toward Clean Architecture).

## 3. Major Components

| Component | Purpose |
|---|---|
| `QuizApplication` | Hilt entry point (`@HiltAndroidApp`); applies saved dark-mode preference at app startup before any Activity is created |
| **ui/splash** | `SplashActivity` — 2-second branded delay screen, then navigates to Main |
| **ui/main** | `MainActivity`/`MainViewModel` — category (Science/Math/History/General) & difficulty (Easy/Medium/Hard) picker; triggers DB seeding; navigation hub |
| **ui/quiz** | `QuizActivity`/`QuizViewModel` — runs the 5-question quiz session, per-question countdown timer, scoring |
| **ui/result** | `ResultActivity` — shows score, percentage-based feedback message, retake/home actions |
| **ui/history** | `HistoryActivity`/`HistoryViewModel`/`HistoryAdapter` — reactive list of past attempts with live search/filter |
| **ui/details** | `QuizDetailActivity`/`QuizDetailViewModel`/`QuizDetailAdapter` — drill-down into a single attempt's per-question answers |
| **ui/leaderboard** | `LeaderboardActivity`/`LeaderboardViewModel`/`LeaderboardAdapter` — all results ranked by score |
| **ui/settings** | `SettingsActivity`/`SettingsViewModel` — dark mode toggle, timer duration, clear history |
| **domain/usecase** | 8 single-responsibility use cases (`GetQuestionsUseCase`, `SaveQuizResultUseCase`, `SeedDatabaseUseCase`, `ObserveHistoryUseCase`, `SearchHistoryUseCase`, `ObserveLeaderboardUseCase`, `GetAttemptDetailsUseCase`, `ClearHistoryUseCase`) |
| **data/local** | Room `QuizDatabase` (3 entities, schema v6), `QuizDao` |
| **data/seed** | `DatabaseSeeder` — hardcodes 60 trivia questions (4 categories × 3 difficulties × 5 questions) |
| **utils/PreferencesManager** | Hilt-injected `@Singleton` wrapper over `SharedPreferences` for dark mode + timer duration |
| **di/** | `DatabaseModule` (provides Room DB/DAO), `RepositoryModule` (binds interface → impl) |

## 4. End-to-End Quiz Flow

```
SplashActivity (2s delay)
   → MainActivity
       - MainViewModel.seedDatabase() wipes & reseeds questions on every visit
       - user picks category + difficulty (button UI state)
       - "Start Quiz" → Intent extras (category, difficulty) → QuizActivity
   → QuizActivity (@AndroidEntryPoint)
       - QuizViewModel.loadQuestions(category, difficulty)
           → GetQuestionsUseCase → QuizRepository.getQuestions()
           → DAO query picks 5 RANDOM distinct questions matching category+difficulty
       - UI collects 3 StateFlows via repeatOnLifecycle(STARTED):
           questions, currentIndex, quizFinished
       - Each question: CountDownTimer (duration from PreferencesManager,
         default 30s) — auto-submits "None" on timeout
       - Tapping an option → checkAnswer() colors buttons (green=correct,
         red=wrong), 1s delay, then viewModel.answerQuestion(selected)
           - updates score (StateFlow), advances currentIndex,
             or sets quizFinished=true after 5th question
       - On quizFinished:
           - QuizViewModel.saveResults() → SaveQuizResultUseCase
               → inserts QuizResultEntity (score/category/date/timeSpent)
               → inserts QuizAttemptDetailEntity per question (full answer record)
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
- A **mapper layer** (`data/mapper/QuizMappers.kt`) converts between Room `*Entity` classes and clean `domain/model/*` classes — the UI/domain layers never see Room annotations.
- **SharedPreferences** (via `PreferencesManager`) is used separately for lightweight user settings (dark mode boolean, timer duration int) — not appropriate for Room.
- Question bank is **not persisted across reinstalls** — it's wiped and reseeded from hardcoded data (`DatabaseSeeder`) every time `MainActivity` loads, while `quiz_results`/`quiz_attempt_details` (history) persist.

## 6. Key Files Reference

| File | Why it matters |
|---|---|
| `QuizApplication.kt` | Hilt bootstrap + global dark-mode init |
| `di/DatabaseModule.kt`, `di/RepositoryModule.kt` | Defines the DI graph — how Room and the repository get wired to everything |
| `domain/repository/QuizRepository.kt` | The abstraction the whole app's business logic depends on |
| `data/repository/QuizRepositoryImpl.kt` | Concrete implementation bridging domain ↔ Room |
| `data/local/QuizDao.kt` | All SQL queries, including the `ORDER BY RANDOM() LIMIT 5` question-picking logic |
| `data/local/QuizDatabase.kt` | Room database definition, entity registry, version |
| `data/mapper/QuizMappers.kt` | Entity ↔ Domain model conversion |
| `ui/quiz/QuizViewModel.kt` | Core quiz session state machine (StateFlow-driven) |
| `ui/quiz/QuizActivity.kt` | Most complex Activity — timer, answer UI, Flow collection via `repeatOnLifecycle` |
| `domain/usecase/*.kt` | Single-responsibility business operations, the "use case" layer of Clean Architecture |
| `app/QuizApp_MVVM_Refactor_Spec.md` | Original refactor spec used to migrate from Activity-centric code to this MVVM/Clean Architecture design |

---

**Presentation note:** the project shows a clear evolutionary story — `QuizApp_MVVM_Refactor_Spec.md` documents the original plan (basic MVVM with `AndroidViewModel` + `LiveData`), but the actual implementation went further: Hilt DI, `Flow`/`StateFlow` instead of `LiveData`, and a full domain/use-case layer — a step toward Clean Architecture beyond the original spec.
