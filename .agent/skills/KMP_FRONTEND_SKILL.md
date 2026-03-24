---
name: kmp-frontend
description: Guidelines for building the Kotlin Multiplatform + Compose Multiplatform frontend for st-viz.
---

# KMP Frontend Development Skill

## Module Structure
```
shared/
├── src/
│   ├── commonMain/kotlin/com/stviz/shared/
│   │   ├── api/          # Ktor HTTP client calls
│   │   ├── model/        # Shared data models (Kotlin data classes)
│   │   ├── repository/   # Repository interfaces + implementations
│   │   ├── viewmodel/    # ViewModels using StateFlow (KMP compatible)
│   │   └── util/         # Helpers (date formatting, currency, etc.)
│   ├── androidMain/      # Android-specific actual declarations
│   └── iosMain/          # iOS-specific actual declarations
androidApp/
└── src/main/kotlin/com/stviz/android/
    ├── ui/               # Android-specific Compose screens (if any)
    └── MainActivity.kt
iosApp/
└── iosApp/               # iOS entry point (SwiftUI wrapper)
```

## ViewModel Pattern (KMP)
Use `kotlinx.coroutines` and `StateFlow`. Never use Android's `ViewModel` in `commonMain`:
```kotlin
// commonMain
class PortfolioViewModel(private val repo: PortfolioRepository) {
    private val _state = MutableStateFlow<PortfolioState>(PortfolioState.Loading)
    val state: StateFlow<PortfolioState> = _state.asStateFlow()

    fun loadPortfolios() = coroutineScope.launch {
        _state.value = PortfolioState.Loading
        repo.getPortfolios().fold(
            onSuccess = { _state.value = PortfolioState.Success(it) },
            onFailure = { _state.value = PortfolioState.Error(it.message) }
        )
    }
}

sealed class PortfolioState {
    object Loading : PortfolioState()
    data class Success(val portfolios: List<Portfolio>) : PortfolioState()
    data class Error(val message: String?) : PortfolioState()
}
```

## Ktor HTTP Client (shared)
```kotlin
// commonMain — ApiClient.kt
val client = HttpClient {
    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    install(Auth) {
        bearer { loadTokens { BearerTokens(tokenStorage.getToken(), "") } }
    }
    defaultRequest { url("http://localhost:8080/api/") }
}
```

## Serialization
Use `@Serializable` from `kotlinx.serialization` on all shared models:
```kotlin
@Serializable
data class Portfolio(val id: Long, val name: String, val description: String)
```

## Charts (KMP)
Use **Vico** (Android/KMP compatible) or **MPAndroidChart** for Android.
For compose-multiplatform web support, use **compose-charts** or render SVG via Canvas.

## Navigation
Use **Voyager** for KMP navigation:
```kotlin
object PortfolioListScreen : Screen {
    @Composable
    override fun Content() { /* Compose UI */ }
}
```

## Theming
Define a shared `AppTheme` in `commonMain` using `MaterialTheme` from Compose Multiplatform:
- Primary: Deep Blue `#1A237E`
- Secondary: Green `#43A047` (gains) / Red `#E53935` (losses)
- Background: Dark `#121212`
- Use Material 3 components.
