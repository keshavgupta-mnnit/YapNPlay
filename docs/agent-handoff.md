# YapNPlay — Agent Handoff (paste into any AI before use)

KMP social gaming app (first game: Royal Ludo). Root package
`com.kglabs28.yapnplay.appengine`. Read `docs/ARCHITECTURE_RULES.md`
first, restate your plan, ask when unsure.

## Structure: one library, folders not modules

Single module `:appengine` (one build file, Android + JVM). Never create
a Gradle module or per-folder build file.

```
games/base/                  # Base contracts, mechanic-free (Action = marker)
  identity/ rules/ match/ play/  + Game.kt (engine interface)
games/common/gameplay/moves/ # Shared mechanics: dice/, pieces/ (+cards/… later)
games/royalludo/             # Game-specific only: engine/, utils/
protocol/                    # Wire format: transport/, Room/Match/Http messages
```

Tests mirror this in `commonTest` with `kotlin.test`. Build: `.\gradlew.bat build`.

## Hard rules

- Folders get only layers they need (`engine/domain/data/ui/utils`) — no empty or single-file folders.
- One file per concept; no god files; no global functions (logic in util classes).
- No `-`/`_` in folder/package names (`royalludo`). Display names and the wire id `GameId("royal-ludo")` keep natural spelling.
- One-way dependencies only: `games/* → games/common → games/base`, `protocol → games/common → games/base`. Never a cycle.
- Base contracts: `Game : GameDescriptor` (id, players, spectators, `TurnPolicy`, timing, rules). Rules split `MandatoryRules` (fixed) vs `HouseRules` (room toggles). `PRIVATE_ROOM` accepts anything; `PUBLIC_MATCH` locks all to traditional.
- Engines are pure (no clock/network/hidden randomness; injected `GameRandom`). Tests ship with every feature.
- UI: components in `ui/components/`, screens assemble only; `modifier` param everywhere; adaptive phones/tablets/landscape; no hardcoded values; Koin; MVI + repositories.
- Royal Ludo: 52-track, safe starts+stars, base on 6, extra turns, 3rd-six forfeit, exact finish, lone capture, pair protection, scored autoplay. Board renders viewer-relative; engine stays seat-agnostic.
- Never touch git unless explicitly asked, one instruction at a time.
