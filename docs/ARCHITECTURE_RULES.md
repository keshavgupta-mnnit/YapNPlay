# YapNPlay Architecture Rules

**Version:** 2.0.0
**Last updated:** 2026-10-15

This document is the single source of truth for code organisation, naming, testing and engineering discipline. **Every AI agent and every developer must read this file before writing code.**

---

## 1. Module dependency rules

The dependency graph is strictly one-directional. A module may only depend on modules **below** it.

```
:app  →  :server (NOT allowed — see note)
:app  →  :shared:protocol
:app  →  :shared:game-api
:app  →  :games:ludo

:server  →  :shared:protocol
:server  →  :shared:game-api
:server  →  :games:ludo

:games:ludo  →  :shared:game-api
:games:ludo  →  :shared:protocol (for serialization of game-specific types)

:shared:game-api  →  (nothing — pure Kotlin)
:shared:protocol  →  (nothing — pure Kotlin)
```

**Hard rules:**
- `:shared:game-api` and `:shared:protocol` must **never** depend on any other module in this project. They are pure Kotlin libraries using only Kotlin and kotlinx libraries.
- `:server` must **never** depend on `:app`.
- `:app` must **never** depend on `:server`.
- Game engines (`:games:ludo`) must **never** depend on `:server` or `:app`.

---

## 2. Layer responsibilities

| Module | Layer | Responsibility | Must NOT do |
|---|---|---|---|
| `:shared:game-api` | Contract | Game interface, identifiers, types, events, randomness | Serialization, network, Android, clock |
| `:shared:protocol` | Contract | Wire message formats, envelope, error codes, HTTP bodies | Game logic, business rules |
| `:games:ludo` | Engine | Pure Ludo rules, state transitions, autoplay, views | Network, clock, Android, serialization of protocol types |
| `:server` | Backend | Authority for identity, rooms, matches, timers, event log | UI, drawing, microphone |
| `:app` | Client | Drawing, local screen state, microphone/speaker, sending requests | Computing game outcomes, deciding dice |

---

## 3. Naming conventions

### Packages
- `:shared:game-api` → `com.kglabs28.yapnplay.shared.game.api`
- `:shared:protocol` → `com.kglabs28.yapnplay.shared.protocol`
- `:games:ludo` → `com.kglabs28.yapnplay.games.ludo`
- `:server` → `com.kglabs28.yapnplay.server`
- `:app` → `com.kglabs28.yapnplay`

### Files
- One type per file (exceptions: small sealed hierarchies that belong together).
- File name matches the primary type name (PascalCase).
- Interfaces are prefixed with `I` only when there is a naming conflict; otherwise use descriptive names without prefix.

### Classes and types
- PascalCase for types, classes, objects, enums.
- camelCase for functions, properties, parameters.
- `UPPER_SNAKE_CASE` for constants.
- Sealed hierarchies: base name is descriptive (e.g., `Action`, `Viewer`, `GameEvent`), subclasses are suffixed (e.g., `RollDice`, `MovePiece`).

### Functions
- Boolean-returning functions: `is*`, `has*`, `can*` prefix (e.g., `isFinished`).
- Factory functions: `create*`, `from*`, `of*` prefix.

---

## 4. Test rules

1. **Tests ship with the feature.** A feature without tests is not done.
2. **Many unit tests, some integration tests, UI flow tests for every screen.**
3. **Shared modules**: every message type must have a round-trip serialization test. Golden files (stored JSON) are compared against serialisation output.
4. **Game engines**: deterministic tests using injected `GameRandom` and injected clock. No hidden randomness.
5. **No silent failures**: every test asserts something meaningful.
6. **Test naming**: `backtick description` style — describes the behaviour being tested.

### Golden files
- Location: `shared/protocol/src/test/resources/golden/`
- One file per message type, named after the message type (e.g., `CreateRoom.json`, `MatchSnapshot.json`).
- Tests fail if serialisation drifts from the golden file.
- To update a golden file: run the test, copy the actual output to the golden file, verify the change is intentional.

---

## 5. Hard rules — no exceptions

These rules apply to **every line of code** in this project. No exceptions, no shortcuts.

### 5.1 No hardcoded values

- **Text** → Android: `strings.xml`. Shared/server: constants file. Never inline user-visible strings.
- **Sizes, colours, durations** → named design tokens (e.g., `Dimens.kt`, `Colors.kt`, `Durations.kt`).
- **Numbers with meaning** (code length, limits, timeouts, seat counts) → named constants in one place per module. Never magic numbers in logic.
- **Server settings** → environment variables. Never in code.

### 5.2 KMP-first (Kotlin Multiplatform)

- This is a **Kotlin Multiplatform** project. Code must be written with multiplatform in mind from day one.
- Shared modules (`:shared:game-api`, `:shared:protocol`) are pure Kotlin — no `java.*`, no `android.*` imports.
- Platform-specific code goes behind interfaces with `expect`/`actual` or behind injected abstractions.
- The goal: share business logic, contracts, and game engines across Android, iOS, web, and desktop.

### 5.3 Compose component structure (Android/KMP)

- **All Compose components** live in `ui/components/`. Never define a composable inside a screen file.
- **Screen files** (`ui/screens/{ScreenName}/{ScreenName}Screen.kt`) contain **only**:
  - The screen `@Composable` function (assembly of components)
  - The screen ViewModel (if the screen owns one)
- **Never** put business logic, state derivation, or data loading in a `@Composable` body. That goes in the ViewModel.
- **Component categorisation**: if a screen has many components, group them in a subfolder:
  ```
  ui/components/
  ├── common/           # Shared across screens (AppTopBar, LoadingIndicator, etc.)
  ├── lobby/            # Lobby screen components
  ├── room/             # Room screen components
  ├── match/            # Match screen components
  └── ludo/             # Ludo game components
  ```
- **No god composables**: each component does one thing. If a component grows too large, break it down.

### 5.4 Modifier parameter pattern

- **Every** composable exposes a `modifier: Modifier = Modifier` parameter.
- The standard size/spacing is baked into the component's default.
- Callers override only what they need by passing a custom modifier — **never** use `if/else` inside a component to vary its appearance.
- Example:
  ```kotlin
  @Composable
  fun DiceRoller(
      value: Int,
      modifier: Modifier = Modifier,
      size: Dp = 48.dp,
  ) { ... }
  ```
  If a caller needs a bigger dice, they pass `size = 72.dp`. No `if (big) ... else ...` inside.

### 5.5 Adaptive layouts

- Layouts must adapt to window size — phones, tablets, and landscape.
- Use `BoxWithConstraints`, `WindowSizeClass`, or `Modifier.fillMaxSize()` with weight-based layouts.
- Never hardcode pixel sizes for layout structure. Use `dp` only for design tokens.
- Test on multiple screen sizes and orientations.

### 5.6 Clean architecture + MVI + Repository pattern

- **MVI (Model-View-Intent)**: one immutable screen state, user actions as intents, one-way data flow.
  - `State` — immutable data class holding everything the UI needs.
  - `Intent` — sealed class of user actions (button clicks, text input, etc.).
  - `ViewModel` — receives intents, calls use cases, emits new state.
- **Clean architecture layers** (inside `:app` and `:server`):
  ```
  ui/ (View) → domain/ (Use Cases) → data/ (Repositories) → sources/ (Data Sources)
  ```
  - `ui/` — Composables, ViewModels, state, intents.
  - `domain/` — Use cases (one per user action), repository interfaces, domain models.
  - `data/` — Repository implementations, data source implementations, DTOs.
  - `sources/` — WebSocket, HTTP, local storage, voice provider.
- **Repository pattern**: the ViewModel never talks to a data source directly. It talks to a use case, which talks to a repository.
- **SOLID principles**: Single responsibility, Open-closed, Liskov substitution, Interface segregation, Dependency inversion.

### 5.7 Koin for dependency injection

- Use **Koin** for all dependency injection. No manual `Singleton` objects, no service locators.
- Define modules in a `di/` package. Each feature area has its own module.
- ViewModels are obtained via `viewModel { }` or `getViewModel()`. Never create a ViewModel manually.
- Repositories and data sources are `single { }` or `factory { }` as appropriate.

### 5.8 Resource files

- **All user-visible text** in `strings.xml`. Never hardcode a string in Kotlin code.
- **Colours** in `colors.xml` or as design token constants.
- **Dimensions** in `dimens.kt` as design tokens.
- **No string concatenation** for user-visible text. Use `stringResource()` with format args.

---

## 6. Instructions for AI agents

Every AI agent working in this repository **must** follow these steps:

1. **Read this file first.** Restate the dependency rules and hard rules in your plan.
2. **Never edit shared modules** (`:shared:protocol`, `:shared:game-api`) without explicit user approval.
3. **Read `docs/project-brief.md`** before starting any phase.
4. **Restate your plan** before writing code. Explain what you will create and why.
5. **Ask when unsure.** If a change might affect the contract or these rules, ask first.
6. **Do not break working logic** while restructuring. If a structural change forces breakage, do it anyway (industry-standard structure wins) and say what broke so it gets fixed — never silently degrade.
7. **Never create inline composables, hardcoded strings, or magic numbers.** If you catch yourself doing this, stop and refactor.
8. **Every composable gets a `modifier` parameter.** No exceptions.
9. **Every screen file contains only the screen composable and its ViewModel.** Everything else goes in `ui/components/`.
10. **Tests ship with the feature.** If you write code without tests, it is not done.

---

## 7. Contract versioning

- The contract version is defined in `shared:protocol/src/main/kotlin/.../ContractVersion.kt`.
- Current version: **0.1.0**
- **Any change to a shared module bumps the version** and is announced to all development chats.
- Bump rules:
  - **Patch** (0.1.0 → 0.1.1): new optional field, new message type, bug fix.
  - **Minor** (0.1.0 → 0.2.0): new required field, new channel, new game interface method.
  - **Major** (0.1.0 → 1.0.0): breaking change to existing types.

---

## 8. Git workflow

- `main` is the default branch. All work happens on feature branches.
- Commit messages follow conventional commits: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`.
- Before committing: run `./gradlew build` and ensure all tests pass.
- **Secrets never go into git.** Use `.gitignore` and environment variables.

---

## 9. Technology stack

| Concern | Technology | Version |
|---|---|---|
| Language | Kotlin | 2.0.21 |
| Build | Gradle (Kotlin DSL) | 9.6.0 (wrapper) |
| Android Gradle Plugin | AGP | 9.4.1 |
| Serialization | kotlinx.serialization | 1.7.3 |
| Coroutines | kotlinx.coroutines | 1.9.0 |
| Date/Time | kotlinx.datetime | 0.6.1 |
| Testing (shared) | JUnit 5 (Jupiter) | 5.11.2 |
| Testing (Android) | JUnit 4, Espresso | (existing) |
| DI | Koin | (Phase 2+) |
| Voice | LiveKit | (Phase 2+) |
| Backend | Ktor | (Phase 3+) |
| UI | Jetpack Compose | (Phase 2+) |
| Platform targets | KMP (Android first, then iOS/web/desktop) | — |
