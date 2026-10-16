# STATUS — 2026-10-17

Read first: `docs/project-report.md` (the plan), `docs/decisions.md`
(later decisions that override it), `docs/ARCHITECTURE_RULES.md`,
`docs/backend-api-contract.md` (contract v0.3.0).

## Build
- `.\gradlew.bat :appengine:compileKotlinJvm :androidApp:compileDebugKotlin`
  compiles clean.
- Tests are paused on purpose while the UI is being tuned; several
  existing tests are expected to be out of date (profile/rooms/catalog
  shapes, renamed use cases, `GameDescriptor` additions). Fix together
  in one pass later.

## Progress against the report
| Phase | State |
|---|---|
| 1 Contracts | Done (single `:appengine` library instead of `:shared:*` modules); v0.3.0 |
| 2–4 Design | Replaced by the user's neon/glass mockups |
| 5 Ludo engine | Done |
| 7 Android base | Done |
| 8 Social screens (fake data) | Partial: Home, Rooms tab, Game Zone. Missing: Welcome/name, create-room flow, **Lobby**, settings, dialogs |
| 9 Ludo screens (local engine) | Not started |
| 6, 10, 11, 14 Backend | Not started |
| 12, 13, 15 Connectivity, voice, online match | Not started |

## Done in this session
- Top bar, Home cards, Rooms tab (60:40), Game Zone (carousel + grid +
  Quick Play with search timer, early start, CPU fill) on fake data.
- Repository → use case (`*UseCase`) → MVI everywhere; fakes only in
  `core/common/FakeDataProvider`, production calls written as comments.
- Screen view models scoped to the nav back stack
  (`entry.rememberScreenViewModel()`).
- Contract 0.3.0: matchmaking channel, `SelectGame`, CPU seats, game
  matchmaking/showcase config, progression fields, room leader/icon.

## Known gaps / next
1. Lobby screen (members, ready, countdown, game picker reusing
   `GameCarousel`, voice panel) — the POC's core screen.
2. Ludo match screen on the local engine (Phase 9).
3. `SessionReplaced` message is documented but not yet in `protocol/`.
4. Security pass (deferred by decision): `google-services.json` is
   committed, Firebase API key not restricted yet.
5. Art placeholders: see `docs/placeholder-assets.md`.
