# YapNPlay Architecture Rules

**Version:** 1.0.0
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

## 5. Engineering rules (apply to all code)

1. **No hardcoded values.**
   - Text → string resources (Android) or constants (shared modules).
   - Sizes, colours, durations → named design tokens.
   - Numbers with meaning (code length, limits, timeouts) → named constants in one place per module.
   - Server settings → environment variables.

2. **Small, single-purpose classes** with dependencies injected via constructor.

3. **Interfaces only where something is likely to change** (voice provider, transport, storage) or must be replaced in tests (clock, randomness).

4. **Time and randomness are injected**, never read directly inside game or timer logic.

5. **Typed errors**, never error strings. No silent failures. Logs never contain secrets.

6. **Secrets** (keys, tokens, passwords) never go into code or git.

7. **Adaptive by default.** Layouts adapt to window size; nothing is sized for one phone.

8. **Teach as you build.** The agent explains what each step does and why before the user runs it.

---

## 6. Instructions for AI agents

Every AI agent working in this repository **must** follow these steps:

1. **Read this file first.** Restate the dependency rules in your plan.
2. **Never edit shared modules** (`:shared:protocol`, `:shared:game-api`) without explicit user approval.
3. **Read `docs/project-brief.md`** before starting any phase.
4. **Restate your plan** before writing code. Explain what you will create and why.
5. **Ask when unsure.** If a change might affect the contract, ask first.
6. **Do not break working logic** while restructuring. If a structural change forces breakage, do it anyway (industry-standard structure wins) and say what broke so it gets fixed — never silently degrade.

---

## 7. Contract versioning

- The contract version is defined in `shared/protocol/src/main/kotlin/.../ContractVersion.kt`.
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
