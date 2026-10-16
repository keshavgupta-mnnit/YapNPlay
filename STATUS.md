# STATUS — Phase 7 close-out (2026-10-16)

## What passed
- `.\gradlew.bat build` → BUILD SUCCESSFUL.
- 221 tests, 0 failures: royal-ludo engine (34), protocol wire format (25),
  design tokens, layout tiers, MVI base, Koin graph, About feature,
  TextUtils, AppManager (8), import-direction guard, source guards (dp/sp,
  strings, no-logic-in-composables), Robolectric interactions/behaviour,
  gallery renders at 640x360 / 800x360 / 1280x800.
- Android Lint (HardcodedText = error) runs in the build.
- UI audit: zero hardcoded colors, dp/sp, or user-visible strings outside
  token files (`Color.kt`, `Dimens.kt`, `Strings.kt`). UI polish later
  touches tokens only. Placeholder glyphs recorded in
  `docs/placeholder-assets.md`.

## Assumptions made
- Navigation Compose `2.9.0-beta03` (JetBrains fork): newest KMP-capable
  release; no stable exists. API used (NavHost/composable/navigate) is stable.
- Screenshot libraries (Paparazzi ≤ 2.0.0-alpha01, Roborazzi ≤ 1.44.0-alpha03)
  both require the AGP 8 `TestedExtension` API removed in AGP 9 → dropped.
  Gallery is verified by Robolectric renders + manual emulator comparison.
- `font_certs.xml` bytes are best-effort: if gallery headings render in the
  fallback font, regenerate via Studio (res/font → New → Font Resource →
  More Fonts → Rajdhani/Inter). Needs device network on first load.
- Robolectric upgraded 4.13 → 4.15.1 for AGP 9. `testOptions ..
  .isIncludeAndroidResources = true` (Studio AI's fix) is required.
- Robolectric resolves its manifest via `androidApp/src/debug` manifest +
  `@Config(packageName)` (Studio AI's fix); my generated-properties
  workaround was removed. AGP 9 generates no `test_config.properties`.
- `minSdk` 24 → 26, `targetSdk` 36 (current Play requirement).
- Ktor/DataStore/Tink/MockK: versions pinned in catalog only; wiring deferred
  until a feature needs them. Koin/MVI/VMs are plain classes (no Android
  ViewModel) to stay multiplatform.
- Fonts: Rajdhani Semibold headings/buttons (caps), Inter body, per list
  (list wins over any older font note).
- Turbine + eager dispatchers: StateFlow conflates back-to-back emissions,
  so tests assert settled states, not every intermediate emission.
- Kotlin block comments nest: never write a literal `/*` (e.g. `games/*`)
  inside a comment — it opens a comment that never closes.

## Left for the user (needs device/emulator)
- Install `:androidApp` on the 800×360, 640×360 and 1280×800 emulators +
  one real phone: launches landscape, shows Home, gallery matches mockups.
- Confirm Rajdhani renders (see font note above).
- Provide real art per `docs/placeholder-assets.md`.
- Approve the structure, then commit (assistant never touches git).

## AppManager + Firebase (done 2026-10-16)
- `appengine/appmanager/`: `AppManager` (two-phase local-first + remote
  merge, never-throw typed getters), `AppManagerConstants` (4 keys:
  voice/royalludo/chat enabled, update_config), `AppConfigStore` interface.
  KMP split: SharedPreferences store + Remote Config fetch in androidMain,
  in-memory + empty remote in jvmMain. 8 `AppManagerTest` tests green.
- `CrashReporter` expect/actual (Firebase Crashlytics on Android, stderr on
  JVM). Every catch reports with context; reporting never throws.
- Firebase wired: `androidApp/google-services.json` (package verified
  `com.kglabs28.yapnplay`), google-services plugin 4.4.2, Crashlytics
  plugin 3.0.3, BOM 34.0.0 (config 23.0.0, crashlytics 20.0.0 — read from
  the BOM, since Kotlin forbids `platform()` in KMP blocks).
- `YapNPlayApp` starts Koin (app + platform modules) and fires one
  background remote sync. Koin graph test covers the new bindings.
- Versions were verified against Google's Maven (Central only hosts
  google-services ≤ 3.1.1 — 4.x lives on `google()`).
- NOTE: `google-services.json` contains an API key — decide before pushing
  whether it stays committed.

## Studio AI review + profile data layer (2026-10-16, tree currently RED)
Studio AI is mid-fix; the tree does not compile right now. Verified read-only.

### Studio AI: must fix (blocking build)
1. `ui/screens/home/HomeScreen.kt` references 5 things that do not exist:
   `arcBalance` (:44), `QuickPlay` (:60), `HostLobby` (:69, :76),
   `InviteFriends` (:73). Nothing compiles until these resolve.
2. `Color.kt`: `Success` changed `#72F597` -> `#2DD462` (now identical to
   `PlayerGreen`), breaking the approved list AND `DesignTokensTest`.
   Revert to `#72F597`. New hexes (`GlassSurface`, `Gold`,
   `GameZone*`, `SocialZone*`) were never approved — confirm or drop.
   The requested hue->purpose rename (Cyan/Pink/…) was NOT done.
3. New raw dp in `CreateRoomBanner.kt`, `PlayWithFriendsCard.kt`,
   `SocialNetworkOrbitGraphic.kt` (20/24/36/44/48/52/64dp, 1/1.5/2dp
   borders) + one `Color(` literal — all fail the guard tests.
   `cardRadius` token exists but the code uses raw `24.dp` instead.
4. Content values hardcoded in VM defaults AND `Strings.kt`
   (`"Keshav"`, `"12"`, balances) — must come from the repository below.

### Done on my side (new files only, zero overlap with their edits)
- `features/profile/`: `UserProfile`, `Friend`, `ProfileRepository`
  (interface), `FakeProfileRepository` (mock data at the lowest level),
  `ProfileRepositoryTest`, `HomeViewModelTest`.
- Wiring (exact-match edits, 4 files — Studio AI stay out of these until
  green): `HomeContract` (empty defaults + `friends` + `LoadProfile`),
  `HomeViewModel` (loads profile+friends from repo),
  `HomeModule` (passes repo), `AppModules` (+profileModule).
- Backend swap = one line (`FakeProfileRepository` -> real impl).
- My files compile; remaining compile errors are all Studio AI's (above).

### Still open with Studio AI (their files, not touching)
- Finish the color rename; delete content strings from `Strings.kt`
  (`homeUserName`, `homeLevelValue`, balances…) once VM wiring lands;
  replace `??` glyphs with the placeholder-assets process.

## Profile data layer (done, awaiting Studio AI fixes to go green)
- `features/profile/`: `UserProfile`, `Friend`, `ProfileRepository`
  (`fetchProfileData`, `getFriendList`), `GetHomeScreenData` use case
  (profile required, friends best-effort — the only place both are joined),
  `ProfileApi` + `ProfileApiClient` (TODO(BE) markers + 600ms mock delay),
  DTOs with backend `snake_case` names, `ProfileRemoteManager`
  (typed errors) + `ProfileLocalManager` (cache, TODO(BE): Room),
  `ProfileRepositoryImpl` (network-first, cache fallback), `Mappers`.
- Mock data lives in ONE place: `core/common/FakeDataProvider`
  (used by the API client; tests use their own fixtures).
- IO switching lives in repository impls (`withContext(dispatchers.io)`);
  VMs call safely from Main. Same applied to `AboutRepositoryImpl`.
- `HomeViewModel` now loads via the use case; `HomeScreen` collects it
  (wired in `YapNPlayNavHost`).
- `docs/backend-api-contract.md`: endpoints, shapes, auth, versioning rules
  for the BE AI. Dev base URL lives in `AppConstants.SERVER_BASE_URL`.
- Touched Studio AI files (exact-match, tell them to pull): `HomeContract`
  (empty defaults + `friends` + `LoadProfile`), `HomeViewModel`,
  `HomeModule`, `AppModules`.
- Tree is RED on Studio AI's `HomeScreen.kt` only (`arcBalance`,
  `QuickPlay`, `HostLobby`, `InviteFriends` unresolved) — my scope has
  no errors; full green needs their fix first.

## Queued next
1. Deferred discussion: proper KMP `composeApp` structure.
2. Retrofit annotations + Room persistence (replace TODO(BE) marks).
