# YapNPlay Architecture Rules

**Version:** 2.1.0
**Last updated:** 2026-10-15

This document is the single source of truth for code organisation, naming, testing and engineering discipline. **Every AI agent and every developer must read this file before writing code.**

---

## 1. Library and folder rules

There is **exactly one library**: `:appengine`. Everything below is a plain
source folder inside it — never a Gradle module, never a separate library.

```
appengine/
  core/             # Shared resources, util classes, UI components (used in 2+ places)
  games/base/        # Base contracts EVERY game must implement (no domain/ folder — nothing game-specific here)
  protocol/         # Wire message formats, grouped by feature: transport/, room/, match/, http/, errors/
  games/elements/   # Reusable game elements, one folder each: dice/, pieces/, board/ (later cards/, striker/)
                    #   each element keeps only the layers it needs: mechanics/, ui/, utils/, protocol/
  games/
    royalludo/     # Everything specific to Royal Ludo lives here and ONLY here
  di/               # Dependency injection modules (created with the app layer)
  ui/               # App-level UI (created with the app layer)
    components/     # Shared Compose components
    screens/        # Simple feature screens (screen composable + ViewModel only)
```

**Hard rules:**
- One `build.gradle.kts` for the whole library. No per-folder build files, ever.
- Folders are packages, not modules. Nothing inside `app-engine` may declare its own Gradle module.
- Game-specific code lives inside that game's folder under `games/` and nowhere else.
- Shared code (used in 2+ places) lives in `core/` or `appengine/ui/components/`, never duplicated per game.
- Each folder contains only the architecture layers it needs — nothing more (see §2).
- **Dependencies point one way only — never bidirectional:**
  ```
  games/{game}  →  games/elements  →  games/base
  protocol/     →  games/elements  →  games/base
  ```
  `games/base` knows nothing about mechanics or games. `games/elements`
  (dice, pieces, …) knows nothing about any specific game. Games know
  their mechanics and the base contracts, never the reverse. Verified
  by grepping imports: no upward reference may exist.

---

## 2. Folder responsibilities and layers

| Folder | Responsibility | Must NOT contain |
|---|---|---|
| `core/` | Shared resources, util classes, UI components | Game-specific code |
| `games/base/` | Base contracts: `Game`, `GameDescriptor`, `TurnPolicy`, `MandatoryRules`/`HouseRules`, `PlayMode`, `GameState`, events, randomness | Any game implementation |
| `protocol/` | Wire message formats: transport framing, room/match channels, HTTP bodies | Game logic, business rules |
| `games/elements/{element}/` | A reusable game element (dice, pieces, board) with its mechanics, UI (e.g. the glass dice), utils (geometry) and wire messages | Anything specific to one game |
| `games/{game}/engine/` | Pure game rules, state, events, geometry calls | Network, clock, Android, UI |
| `games/{game}/domain/` | Use cases and repository interfaces for that game | Implementations |
| `games/{game}/data/` | Repository implementations, network and local managers for that game | UI |
| `games/{game}/ui/` | That game's screens, components and ViewModels | Other games' code |
| `games/{game}/utils/` | That game's constants and util classes (e.g. `RoyalLudoBoardUtils`) | Global top-level functions — logic lives in util **classes** |
| `games/{game}/` (root) | Only what is specific to that game and fits no layer | Shared code |

### Game folder rule

Each game folder (`games/{game}/`) contains `engine/`, `domain/`, `data/`,
`ui/` and `utils/` — but **only the layers it actually needs**. If a game has
no repository yet, it has no `data/` folder. `games/base/` has no `domain/`
folder because it holds nothing game-specific. Never create empty ceremonial
folders.

### Base contracts every game follows (`games/base/`)

- `GameDescriptor`: id, display name, min/max players, max spectators,
  turn policy, timing, default rules, rules summary.
- `TurnPolicy`: `Sequential` (one seat acts; engine reports the actor) or
  `Simultaneous` (all act together; future games).
- `MandatoryRules` / `HouseRules`: fixed identity vs room-toggleable rules.
- `PlayMode`: `PRIVATE_ROOM` (house rules may change) vs `PUBLIC_MATCH`
  (mandatory AND house both locked to traditional).
- `Game : GameDescriptor`: `start`, `apply`, `currentActor`, `autoplay`,
  `removeSeat`, `viewFor`, `isFinished`, `outcome`. Engines are pure:
  no clock, no network, no hidden randomness.

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

### 5.0 No god files — small, single-purpose, reusable

- **One file per concept, file name matching the concept.** Never merge
  unrelated types into one file to "save files" (e.g. a `GameContracts.kt`
  holding everything is forbidden).
- **SOLID, single responsibility first:** each class/function does one thing.
  If a file grows past one screen of logic, split it.
- **Write for reuse:** prefer small pure functions and narrow interfaces over
  one-off logic. If two places need it, it belongs in a shared folder.
- **Contract-first:** behaviour shared across games/features is declared as an
  interface in `games/base/` (or `core/`) first, implemented per feature second.
  Future changes extend the contract; they never fork it.

### 5.1 No hardcoded values

- **Text** → Android: `strings.xml`. Shared/server: constants file. Never inline user-visible strings.
- **Sizes, colours, durations** → named design tokens (e.g., `Dimens.kt`, `Colors.kt`, `Durations.kt`).
- **Numbers with meaning** (code length, limits, timeouts, seat counts) → named constants in one place per module. Never magic numbers in logic.
- **Server settings** → environment variables. Never in code.

### 5.2 KMP-first (Kotlin Multiplatform)

- This is a **Kotlin Multiplatform** project. Code must be written with multiplatform in mind from day one.
- The single `:appengine` library is pure Kotlin — no `java.*`, no `android.*` imports in `commonMain`.
- Platform-specific code goes behind interfaces with `expect`/`actual` or behind injected abstractions.
- The goal: share business logic, contracts, and game engines across Android, iOS, web, and desktop.

### 5.3 Compose component structure (Android/KMP)

- **All Compose components** live in `ui/components/`. Never define a composable inside a screen file.
- **Every screen directory keeps exactly 3 files**:
  1. `{Screen}Screen.kt` — the `@Composable` function (assembly of components)
  2. `{Screen}ViewModel.kt` — the MVI view model, containing nothing but intent handling
  3. `{Screen}Contract.kt` — all state, intent and effect types, so the ViewModel stays clean
- **Never** put business logic, state derivation, or data loading in a `@Composable` body. That goes in the ViewModel.
- **Compose files contain UI only — zero logic.** No helper functions, no
  validation, no formatting inside a `@Composable` file. Pure logic goes to
  a util class (e.g. `TextUtils`), an extension in its own file, or a use
  case. A `@Composable` file that contains a non-composable function fails
  review.
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
- **Clean architecture layers** (inside each game/feature folder):
  ```
  ui/ (View) → domain/ (Use Cases) → data/ (Repositories) → sources/ (Data Sources)
  ```
  Each folder gets only the layers it needs — no empty ceremonial folders.
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

### 5.8 Text, colours, dimensions

- **All user-visible text** in `core/ui/Strings.kt` (interface `Strings`,
  `EnStrings` default, `LocalStrings` composition local). Never hardcode a
  word where the user can see it. Hindi later = `HiStrings`, zero call-site
  changes. `@Composable` previews and tests are exempt from the no-global
  rule, as are test helpers.
- **Colours** in `core/designsystem/Color.kt` as named tokens.
- **Dimensions** in `core/designsystem/Dimens.kt` as named tokens.
- **No string concatenation** for user-visible text. Formatting lives in
  `Strings.kt` functions (e.g. `aboutInfo(...)`).

### 5.9 Game bots decide like a person (every game)

Every computer decision — CPU seats, Autoplay, away or bot-held seats, in
every game now and later — weighs **profit, loss and risk**, never a plain
if/else or a single fixed threshold:

- List the options the rules allow at that moment (move this piece or
  that, buy or pass, build or skip, bid or pass, pay or stay, which thing
  to sell…).
- Give each one an **expected value**: what it is likely to earn from now
  on, minus what it costs or gives up now, minus the risk it adds (the
  chance of a loss times how big that loss would be — being captured,
  paying rent, going broke). Use the game's real odds (dice, cards,
  squares ahead) and what has happened so far in the match (which
  squares are busy, which earn).
- Pick the highest value. Cash cushions and reserves are part of the
  risk term, not stand-alone rules.
- The bot follows the match's own rules (house rules included); it never
  counts on a rule that is switched off.
- Every weight is a runtime setting (admin page), never a literal.
- The server owns the bot; the app's engine copy mirrors it exactly.
- Each bot ships with tests that read like a person's choice ("with
  ₹10,000 and hotels ahead it doesn't buy the ₹8,500 city").
- **Shared shape:** options → evaluate (gain, cost, risk) → pick. A
  *standing* factor scales risk: a bot far behind takes chances, a leader
  plays safe.
- **Explainable:** each bot decision records its top options and scores
  (debug log, admin page), so odd moves can be traced and tuned.
- **Tuned by self-play:** an offline bot-vs-bot arena compares setting
  sets over many games; the winners become the defaults.
- New situations a game's rules create (taxes, cards, auctions, blocks,
  extra turns, endgame) are added to its evaluation, not special-cased.

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
