# Dual Job Dating - Technical Documentation

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Framework | Kotlin Multiplatform (KMP) | 2.3.0 |
| UI | Compose Multiplatform | 1.10.0 |
| Architecture | MVVM + Feature Modules | - |
| Dependency Injection | Koin | 4.1.0 |
| Networking | Ktor Client | 3.1.3 |
| Navigation | Jetpack Navigation Compose | 2.9.1 |
| Backend | Supabase (PostgREST, Auth, Storage, Realtime) | - |
| Image Loading | Coil | 3.2.0 |
| Serialization | Kotlinx Serialization | - |
| Local Storage | Multiplatform Settings | 1.1.1 |
| UI Components | Material Design 3 | - |
| Config Management | BuildKonfig | - |
| Platforms | Android (min SDK 24), iOS | - |

---

## Project Structure

All application code lives in `commonMain`. Platform-specific code is limited to the Ktor engine (OkHttp on Android, Darwin on iOS) and a native dialog on Android.

```
composeApp/src/
├── commonMain/kotlin/fh/msd/
│   ├── core/
│   │   ├── di/             # Koin module definitions (AppModule.kt)
│   │   ├── domain/         # Shared domain models and DTOs
│   │   ├── navigation/     # NavGraph and typed route definitions
│   │   ├── network/        # Ktor client setup, API base URL, error handling
│   │   ├── session/        # Authenticated user session state
│   │   └── ui/             # Theme, colors, typography, shared composables
│   └── feature/
│       ├── auth/           # Login / logout
│       ├── companies/      # Company card swiping and voting
│       ├── appointments/   # Appointment schedule display
│       └── profile/        # Student profile and settings
├── commonMain/composeResources/
│   ├── drawable/           # Shared SVG/vector assets
│   └── values/             # Localized strings (EN + DE)
├── androidMain/            # MainActivity, Android-specific dialogs
└── iosMain/                # iOS entry point (minimal)
```

---

## Architecture: MVVM + Feature Modules

Each feature is a self-contained vertical slice from data to UI. Features do not depend on each other.

### Layer Breakdown per Feature

```
feature/
└── <feature>/
    ├── data/
    │   ├── service/        # Raw API calls via Ktor or Supabase SDK - returns DTOs
    │   └── repository/     # Maps DTOs to domain models - the only thing ViewModel touches
    ├── domain/
    │   ├── model/          # Clean domain models (no serialization annotations)
    │   └── dto/            # @Serializable classes matching the exact API response shape
    └── ui/
        ├── Screen.kt       # Composable screen - no logic, only renders state
        ├── ViewModel.kt    # Holds state, processes events, emits navigation side effects
        ├── State.kt        # Immutable UI state data class
        └── Event.kt        # Sealed class of user-triggered actions
```

### Data Flow

```
UI (Screen)
  --> onEvent() --> ViewModel
                      --> Repository (interface)
                            --> Service (interface)
                                  --> Ktor / Supabase SDK
                      <-- Result<T>
                    _state.update { }
  <-- collectAsState()
```

---

## Key Conventions

### State

Each screen has a single immutable `State` data class held in a `MutableStateFlow` in the ViewModel. The UI only reads from this flow - it never holds local state for business logic.

```kotlin
data class CompanyListState(
    val companies: List<Company> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDone: Boolean = false
)
```

### Events

User interactions are modelled as a sealed class passed to `onEvent()` on the ViewModel. The screen contains no business logic.

```kotlin
sealed class CompanyListEvent {
    data class Vote(val companyId: String, val vote: VoteType) : CompanyListEvent()
}
```

### Navigation

One-time navigation side effects are emitted via `MutableSharedFlow` from the ViewModel and collected in the screen via `LaunchedEffect`. Routes are type-safe serializable objects, not strings.

```kotlin
// Route definition
@Serializable object LoginRoute
@Serializable object CompaniesRoute
@Serializable object AppointmentsRoute
@Serializable object ProfileRoute

// ViewModel emits navigation events
private val _navigation = MutableSharedFlow<CompanyNavigation>()
val navigation = _navigation.asSharedFlow()

// Screen collects and reacts
LaunchedEffect(Unit) {
    viewModel.navigation.collect { nav ->
        when (nav) {
            CompanyNavigation.ToAppointments -> navController.navigate(AppointmentsRoute)
        }
    }
}
```

### Service vs Repository

- **Service** - makes the raw API call and returns a DTO. No mapping, no business logic.
- **Repository** - maps DTOs to domain models. The ViewModel only depends on the Repository interface, never on a Service directly.

### DTOs vs Domain Models

- **DTOs** (`domain/dto/`) are `@Serializable` data classes that mirror the exact API response shape.
- **Domain Models** (`domain/model/`) are plain Kotlin data classes with no serialization annotations, used throughout the ViewModel and UI.
- Mapping happens exclusively inside `RepositoryImpl`.

---

## Dependency Injection (Koin)

All bindings are declared in a single `AppModule.kt`. Interfaces are bound to implementations here, making swapping straightforward without touching any other code.

```kotlin
val appModule = module {
    single { createHttpClient() }
    single<AuthService> { AuthServiceImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<SessionManager> { SessionManagerImpl(get()) }
    viewModel { LoginViewModel(get(), get()) }
    // ...
}
```

Koin is started in `App.kt` via `KoinApplication { modules(appModule) }`.

---

## Navigation

Navigation is handled by a single `NavGraph.kt` in `core/navigation`. All routes are type-safe serializable objects.

### Flow

```
Login --> Companies --> Appointments
                   --> Profile
```

- After successful login the Login destination is popped from the back stack.
- After voting on all companies the app navigates to Appointments.
- Profile is accessible from the Companies screen.

---

## Configuration

Runtime configuration is injected via BuildKonfig from `local.properties`, which is excluded from version control.

**Required `local.properties` entries:**

```properties
sdk.dir=<path to Android SDK>
BACKEND_BASE_URL=<backend URL>
SUPABASE_URL=<Supabase project URL>
SUPABASE_ANON_KEY=<Supabase anonymous key>
```

BuildKonfig exposes these as constants available in `commonMain` at compile time. The `-PisProduction=true` Gradle flag switches the app to the production backend.

---

## CI/CD

Two GitHub Actions pipelines build and deploy Android release bundles automatically.

| Pipeline | Branch | Track | Version Code Base |
|---|---|---|---|
| `android-testing.yml` | `android-testing` | Internal Testing | 2000 + run number |
| `android-production.yml` | `android-production` | Closed Testing (alpha) | 3000 + run number |

Both pipelines: set up JDK 17, restore Gradle cache, decode the signing keystore from secrets, build a release AAB, and upload to Google Play Console.

**Required GitHub Secrets:**

| Secret | Purpose |
|---|---|
| `KEYSTORE_BASE64` | Base64-encoded Android signing keystore |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Google Play service account credentials |

---

## Features

### Auth

- Email + password login via Supabase Auth
- Session token persisted locally with Multiplatform Settings
- Logout clears session and returns to Login

### Companies

- Displays active companies as a swipeable card stack (Tinder-style)
- Student can Like, Dislike, or Neutral-vote each company
- Votes are submitted to the backend in real time
- Confetti animation plays when all companies have been voted on
- Also accessible as a flat list view

### Appointments

- Displays the student's assigned appointment schedule after the admin runs the matching process
- Shows company name, time slot, and location per appointment

### Profile

- Displays student profile information
- Logout
- Links to Terms of Service and Privacy Policy

---

## Dependencies

```toml
# gradle/libs.versions.toml (abbreviated)

compose = "1.10.0"
compose-material3 = "1.10.0-alpha05"
compose-navigation = "2.9.1"
ktor = "3.1.3"                     # OkHttp (Android) / Darwin (iOS)
koin = "4.1.0"
supabase = "3.1.4"                 # postgrest-kt, auth-kt, storage-kt, realtime-kt
coil = "3.2.0"
multiplatform-settings = "1.1.1"
kotlinx-serialization-json        # via KMP plugin
buildkonfig = "0.15.2"
agp = "8.11.2"
kotlin = "2.3.0"
```
