# STATUS — 2026-10-18

Read first: `docs/project-report.md` (the plan), `docs/decisions.md`
(later decisions that override it), `docs/ARCHITECTURE_RULES.md`,
`docs/backend-api-contract.md` (contract v0.3.0).

## Build
- `.\gradlew.bat :appengine:compileKotlinJvm :androidApp:compileDebugKotlin`
  compiles clean.
- Tests are paused on purpose while the UI is being tuned; several
  existing tests are expected to be out of date. Fix together in one
  pass later.

## Progress against the report (16 phases)
| Phase | State |
|---|---|
| 1 Contracts | Done (single `:appengine` library instead of `:shared:*` modules); v0.3.0 covers rooms, matchmaking, both games, seat handover, moderation |
| 2–4 Design | Replaced by the user's neon/glass mockups |
| 5 Ludo engine | Done |
| 7 Android base | Done |
| 8 Social screens (fake data) | Mostly done: Home, Rooms tab (join by code, invites), Game Zone, room lobby (members, roles, ready, countdown, game picker, voice, notifications). Missing: welcome/name entry, create-room screen (goes to a placeholder), "see all rooms", settings dialog |
| 9 Game screens (local engine) | Done, beyond scope: Royal Ludo and City Bazaar on fake servers, spectating, seat handover, autoplay/CPU, moderation |
| 6 Server, identity, rooms | Not started (no server module yet) |
| 10 Match service | Not started |
| 11 Voice tokens | Not started |
| 14 Hosting | Not started (parked: no hosting files in the repo for now) |
| 12 Android connectivity | Not started (every client has its production call written as a comment; fakes in use) |
| 13 Android voice client | Not started (only the local mic level monitor and the app-wide mic switch exist) |
| 15 Android online match | Not started |
| 16 End-to-end and family test | Not started |

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
