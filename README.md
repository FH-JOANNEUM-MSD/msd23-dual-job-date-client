# Dual Job Dating - Architecture Documentation

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Kotlin Multiplatform (KMP) |
| UI | Compose Multiplatform |
| Architecture | MVVM |
| Dependency Injection | Koin |
| Networking | Ktor |
| Navigation | Jetpack Navigation Compose |
| Backend | Supabase (postgrest, auth, storage, realtime) |
| Platforms | Android, iOS |

---

## Project Structure

All application code lives in `commonMain`. There is no platform-specific code except for the Ktor engine (OkHttp on Android, Darwin on iOS).

```
composeApp/src/commonMain/kotlin/fh/msd/jobdating/
├── core/
│   ├── di/
│   └── navigation/
└── feature/
    ├── auth/
    ├── companies/
    └── appointments/
```

---

## Architecture: MVVM + Feature Modules

The app is structured around self-contained feature modules. Each feature owns its full vertical slice from data to UI.

### Layer Breakdown per Feature

```
feature/
└── <feature>/
    ├── data/
    │   ├── service/        # API calls (Ktor / Supabase)
    │   └── repository/     # Abstracts data source from ViewModel
    ├── domain/
    │   ├── model/          # Domain models used across UI and repo
    │   └── dto/            # Serializable data transfer objects (API shape)
    └── ui/
        ├── Screen.kt       # Composable screen
        ├── ViewModel.kt    # Holds state, handles events, emits navigation
        ├── State.kt        # Immutable UI state data class
        └── Event.kt        # Sealed class of user-triggered events
```

### Data Flow

```
UI (Screen)
  --> onEvent() --> ViewModel
                      --> Repository (interface)
                            --> Service (interface)
                                  --> API / Supabase
                      <-- Result
                    _state.update { }
  <-- collectAsState()
```

---

## Key Conventions

### State
Each screen has a single immutable `State` data class held in a `MutableStateFlow` inside the ViewModel.

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
User interactions are modelled as a sealed class and passed to `onEvent()` on the ViewModel. The screen never contains logic.

```kotlin
sealed class CompanyListEvent {
    data class Vote(val companyId: String, val vote: VoteType) : CompanyListEvent()
}
```

### Navigation
One-time navigation side effects are emitted via `MutableSharedFlow` from the ViewModel and collected in the screen with `LaunchedEffect`.

```kotlin
// ViewModel
private val _navigation = MutableSharedFlow<CompanyNavigation>()
val navigation = _navigation.asSharedFlow()

// Screen
LaunchedEffect(Unit) {
    viewModel.navigation.collect { nav ->
        when (nav) {
            is CompanyNavigation.ToAppointments -> onDone()
        }
    }
}
```

This keeps navigation logic out of the screen and fully testable in the ViewModel.

### Service vs Repository
- **Service** — responsible only for making the raw API call and returning a DTO.
- **Repository** — maps DTOs to domain models and is the only thing the ViewModel talks to.

The ViewModel is injected with the **Repository** interface, never the Service directly.

### DTOs vs Domain Models
- **DTOs** (`domain/dto/`) are `@Serializable` data classes that match the API response shape exactly.
- **Domain Models** (`domain/model/`) are clean Kotlin data classes with no serialization annotations, used throughout the UI and ViewModel.
- Mapping happens inside `RepositoryImpl`.

---

## Dependency Injection (Koin)

All bindings are declared in a single `AppModule.kt`. Interfaces are bound to their implementations here, making it trivial to swap test implementations for real ones.

```kotlin
val appModule = module {
    single<AuthService> { AuthServiceTest() }       // swap to AuthServiceImpl() when ready
    single<AuthRepository> { AuthRepositoryTest(get()) }
    viewModel { LoginViewModel(get()) }
    ...
}
```

Koin is initialized in `App.kt` via `KoinApplication`.

---

## Navigation

Navigation is handled by a single `NavGraph.kt` in `core/navigation`. All routes are defined as a sealed class.

```kotlin
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Companies : Screen("companies")
    data object Appointments : Screen("appointments")
}
```

### Flow
```
Login --> Companies --> Appointments
```
- After login, the Login screen is popped off the back stack.
- After voting on all companies, the Companies screen is popped and Appointments is shown.

---

## Test vs Real Implementations

During development, each Service and Repository has a `Test` implementation with hardcoded data.

| Class | Purpose |
|---|---|
| `CompanyServiceTest` | Returns 3 hardcoded companies, no network call |
| `CompanyRepositoryTest` | Maps from test service, injected into ViewModel |
| `CompanyServiceImpl` *(future)* | Real Ktor implementation |
| `CompanyRepositoryImpl` *(future)* | Wired to real service |

To switch from test to real: update the binding in `AppModule.kt`. Nothing else changes.

---

## Features

### Auth
- Login with email + password
- Logout
- Token stored after login (to be implemented with Supabase Auth)

### Companies
- Displays active companies as a swipeable card stack (Tinder-style)
- Student can Like, Dislike, or skip (neutral = no action)
- After all companies are voted on, navigates to Appointments

### Appointments
- Displays the student's assigned appointment schedule
- Shows company name and time slot per appointment
- Only available after the matching process has been run by admin

---

## Dependencies (libs.versions.toml)

```
compose, compose-material3, compose-navigation
ktor-client-core, ktor-client-okhttp (android), ktor-client-darwin (ios)
ktor-client-content-negotiation, ktor-serialization-kotlinx-json
koin-core, koin-compose-viewmodel
supabase: postgrest-kt, auth-kt, storage-kt, realtime-kt
coil-compose, coil-network-ktor
androidx-lifecycle-viewmodel, androidx-lifecycle-runtime
```