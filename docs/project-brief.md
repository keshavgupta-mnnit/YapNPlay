# YapNPlay — Project Brief

## What we are building

A private social space for people who already know each other: friends, cousins, relatives. A persistent room holds its members, voice chat stays open, and games are played inside the room. Spectators are first-class: anyone not playing can watch, listen and talk, so nobody feels left out.

The first game is Ludo. Later games (card games, drawing games) must plug into the same platform without rewriting it.

## Proof-of-concept (POC) goal

A working version the family installs and plays, to answer one question: is gathering in a voice room and playing Ludo together good enough that people come back?

The POC may look rough, but it must be correct where changes are expensive later: server authority, stable seats, versioned messages and the voice lifecycle.

## Confirmed decisions

- **Android first:** Kotlin, Jetpack Compose, landscape only, Android 8 or newer, English only (all text in resources so Hindi can follow). Dependency injection with Koin. Clean architecture, repository pattern, SOLID principles, use cases where real logic exists, and MVI (Model-View-Intent: one immutable screen state, user actions as intents, one-way data flow). Code stays friendly to Kotlin Multiplatform (sharing Kotlin across platforms); one module per game.
- **Backend:** Kotlin with Ktor. The server is the authority for everything that matters.
- **Voice:** LiveKit first, hidden behind our own interface so a provider swap touches one implementation and one binding.
- **Identity:** guest accounts. Each user has a permanent, unique, system-generated ID and a display name that can change and need not be unique. Login providers attach later to the same ID. A guest account is lost on uninstall.
- **Rooms:** up to 10 members; join by six-character code with no approval; membership persists while offline; if the leader disconnects, leadership passes to the member present longest. The leader manages the room but gets no gameplay advantage.
- **Match start:** Ludo needs 2 to 4 players; if more than four are ready, the first four to press ready get seats and the rest spectate; the server assigns seat numbers and colours randomly; ten-second countdown that any eligible player can cancel.
- **Ludo rules** (traditional; fixed for public matches, toggles inside private rooms later): four pieces each; a piece leaves base on a 6 only; a 6, a capture and a piece reaching home each give an extra turn (a 1 gives none); the third six in a row is forfeited (nothing moves, the turn passes); an exact roll is needed to finish (smaller rolls move a piece closer, larger rolls cannot move that piece, and if no piece can move the turn passes); landing on a lone opposing piece sends it to base; each colour's start square and the star squares are safe; two or more same-colour pieces on one unsafe square are protected; no blocking.
- **Timers and removal:** 30 seconds per turn; at timeout the server plays the best computer-style move (automatic play) and counts a missed turn; a valid action resets the count. Each game declares its own timing values; Ludo waits only 5 seconds when a disconnected player's turn arrives. A seat is 'away' after three consecutive missed turns or 60 seconds of continuous disconnection. In the POC an away player and their pieces are removed from the board immediately, through one hook (the replacement policy) where the replacement-invitation flow will later be inserted; the removed user stays in the room as a spectator. Reconnecting before removal restores control; after removal the user only spectates.
- **Match end:** the match runs until one player is left; finishing order gives places; if everyone else is removed the sole remaining player ranks above removed players and the match ends; if everyone is removed the match is cancelled; removed players rank last, later-removed above earlier-removed. Later: a leader-proposed early close that all players must accept, with points by position.

## Modules

| Module / folder | Purpose | Phase |
|---|---|---|
| `:appengine` (single library) | Everything below is a plain source folder, never a module | Phase 7 |
| `games/base/` | Base contracts every game implements | Done |
| `games/common/gameplay/moves/` | Shared mechanics: dice, pieces (+cards/striker later) | Done |
| `games/royalludo/` | Royal Ludo engine, rules, rules tests | Done |
| `protocol/` | Wire message formats + transport framing | Done |
| `core/`, `ui/`, `di/`, `navigation/`, `features/` | Design system, MVI, components, screens, sample feature | Phase 7 |
| `:androidApp` | Thin launch shell (manifest + activity only) | Phase 7 |
| `:server` | Ktor backend | Later |

## Architecture principles

- No callback hell. A top bar owns ONLY bar UI.
- Prefer singular utils over prop drilling.
- Use navigation-compose for screen transitions.
- Break components as small as possible.
- Every component exposes `modifier: Modifier` as a default parameter.
- No hardcoded values, ever.
- Crash-free by construction: every catch block in crash-sensitive paths also reports via CrashReporter.

## How the system works

The server owns identities, rooms, membership, leader, readiness, countdown, seat assignment, dice, legal moves, timers, automatic play, removals, results and the event log; the app owns drawing, local screen state, microphone and speaker choices and sending requests, and never computes outcomes.

One WebSocket per user carries two logical channels: the room channel (who is here, ready, leader, countdown) and the match channel (game events). Voice is a separate connection to the voice provider and follows the room, not the match.

Every client request carries a request ID so duplicates are ignored; the server answers with complete state snapshots carrying version numbers, and deadlines travel as absolute server times plus the server's current time. Each match has one actor: a single worker that owns the match data and receives every event through one queue, so simultaneous events become ordered events.

Clock and randomness are injected so tests are deterministic.
