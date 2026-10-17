# YapNPlay — Backend API contract (v0.3.0)

What the Android app expects from the Ktor server. The Kotlin types are
the source of truth: `appengine/.../protocol/` (wire messages, bodies)
and `appengine/.../games/base/` (game contract). The server depends on
the same code, so field names can never drift. Read with
`docs/project-report.md` (original plan) and `docs/decisions.md` (later
decisions that win).

## Conventions

- Base URL from configuration; dev default `http://10.0.2.2:8080`
  (`AppConstants.SERVER_BASE_URL`, emulator loopback). Server settings
  come from environment variables, never code.
- Auth: `Authorization: Bearer <userId>.<secret>` on every HTTP call and
  on the WebSocket upgrade, except guest creation. The secret is issued
  once; the server stores only its SHA-256 hash and compares in constant
  time. Logs never contain secrets.
- JSON field names are **camelCase**, exactly the Kotlin property names.
  Unknown fields are ignored (adding an optional field is safe).
- Errors: HTTP status + `{"code": "<ErrorCode>", "message": "…"}`; over
  the WebSocket a `Rejected(requestId, code, message)`.
- Time: epoch millis. Deadlines are **absolute server time**, always sent
  together with `serverTime` so clients correct their clock skew.
- The server is the authority for everything that matters: identity,
  rooms, membership, leader, readiness, countdown, matchmaking, seats,
  dice, legal moves, timers, CPU/autoplay, removals and results. The app
  draws, sends requests, and never computes an outcome.

## Versioning

`CONTRACT_VERSION` (`protocol/transport/ContractVersion.kt`), currently
**0.3.0**. Patch: optional field / new message. Minor: new required
field, channel or game-interface member. Major: breaking change.
Every bump is announced in `docs/decisions.md`.

0.3.0 added: MATCHMAKING channel (`JoinQueue`, `LeaveQueue`,
`QueueStatus`, `MatchFound`, `QueueCancelled`), `SelectGame`,
`SetBotFill` + `RoomInfo.botFill`, `SeatInfo.cpu`,
`GameDescriptor.matchmaking` (with `seats`) + `showcase`, optional `Me`
progression fields, `RoomSummary.isLeader` + `icon`, `Friends`,
`OnlineStats`, error codes `ROOM_LIMIT_REACHED`, `ALREADY_IN_QUEUE`,
`GAME_UNAVAILABLE`.

## HTTP endpoints

| Method + path | Body in | Body out | Notes |
|---|---|---|---|
| `POST /auth/guest` | — | `GuestCreated(userId, secret)` | No auth. Rate-limited per address. Server picks a name like `Player-4821`. |
| `GET /me` | — | `Me` | See below. |
| `PATCH /me` | `UpdateName(displayName)` | `Me` | Trim, 1–24 chars, no control chars, duplicates allowed. |
| `GET /rooms/mine` | — | `MyRooms(rooms: [RoomSummary])` | Rooms the user belongs to. |
| `POST /rooms/{roomId}/voice-token` | — | `VoiceToken(url, token, expiresAt)` | Members only (`NOT_MEMBER`); LiveKit, 10-minute TTL. |
| `GET /rooms/preview/{code}` | — | `RoomPreview(roomId, code, name, description?, memberCount, onlineCount, isMember)` | Before joining: "Join this room?". `isMember` from the connection's user; the app opens the room directly when true. `NOT_FOUND` / `INVALID_CODE`. |
| `POST /devices` | `RegisterDevice(token, platform)` | — | FCM token for pushes; upsert per user+token, re-sent when the token changes. |
| `GET /stats/online` | — | `OnlineStats(online)` | Connected users right now (Game Zone pill). |
| `GET /friends` | — | `Friends(friends: [FriendSummary])` | **Provisional**, post-POC friends; online first. |
| `GET /health` | — | `{status, contractVersion}` | No auth. |

### `Me`
```json
{ "userId": "user-keshav", "displayName": "Keshav", "xp": 2450, "level": 12, "coins": 4250, "gems": 120 }
```
- `xp`: computed by the server from the user's match results — Quick Play
  and room matches alike (formula to be designed; e.g. points by finishing
  place). `level` derives from `xp`.
- `coins`, `gems`: **provisional** — the economy is not designed yet.
  Return 0 until it is; the client already tolerates that.

### `RoomSummary` (inside `MyRooms`)
```json
{ "roomId": "room-college-crew", "code": "482193", "name": "College Crew",
  "memberCount": 10, "onlineCount": 6, "isLeader": true, "icon": "group" }
```
- `onlineCount` = members present right now; the app shows it as "6 live".
- `isLeader` is relative to the requesting user. `icon` ∈ `group | dice |
  family | gamepad` (unknown → `group`).

## Rooms

- Up to **10 members** per room. A user may belong to **5 rooms**
  (`MAX_ROOMS_PER_USER`, env var on the server; the app reads the same
  limit from the `max_rooms_per_user` remote flag and pre-checks it).
  Beyond it: `ROOM_LIMIT_REACHED`.
- Room codes are **6 digits** (`0–9`), generated randomly, retried on
  collision. Letters may be added later (would be a contract bump).
- A user is *present* in at most one room at a time, and is in at most
  one of: a room countdown, a Quick Play queue, a match.

## WebSocket `/ws`

One connection per user (a new connection replaces the old one, which
receives `SessionReplaced` and is closed). Heartbeat ping every 15 s,
dead after 45 s. Messages are `Envelope(requestId, channel, type,
payload)`; max 8 KB; 20 messages/s per connection. A repeated
`requestId` gets the original answer. Every request is answered with
`Ack` or `Rejected`, and state changes are pushed as **full snapshots
with a version number**.

Channels: `ROOM`, `MATCH`, `MATCHMAKING`.

### ROOM channel
Client → server: `CreateRoom(name?)`, `JoinRoom(code)` (also re-enters a
room you belong to), `LeaveRoom`, `ExitLobby`, **`SelectGame(gameId)`**
(leader only, `NOT_LEADER` otherwise; `GAME_UNAVAILABLE` for an unknown
or disabled game), **`SetBotFill(enabled)`** (leader only), `SetReady(ready)`,
`StartCountdown`, `CancelCountdown`, `RequestSnapshot(lastVersion)`.

Lobby messages (all ROOM channel): `SetHouseRule(key, enabled)` and
`SelectGame(null)` ("Change game") are leader-only (`NOT_LEADER`);
`TransferLeader(userId)` leader-only, `TransferAdmin(userId)` and
`RemoveMember(userId)` admin-only (`NOT_ADMIN`); `SetWatching(watching)` and `SetVoiceState(micOn,
speakerOn)` for yourself. `RoomInfo` carries `adminId`, `description`,
`houseRules` (key → on/off, defaults from the game's `houseRuleOptions`,
reset on every game change together with everyone's ready/watching);
`RoomMember` carries `watching`, `micOn`, `speakerOn`, `isFriend` (relative
to the receiver) and `onlineSince`. Succession: leader → longest
`onlineSince` among online members; admin → top of the room leaderboard.
Friends: `POST /friends/{userId}`, `DELETE /friends/{userId}` (provisional).

No message ever carries a list of user ids: the server already knows the
room's members and who is ready, and it is the only one that seats them.

Server → client: `Ack`, `Rejected`, `RoomSnapshot(version, serverTime,
room)` where `room.selectedGame` is the picked game id, `MatchStarting(matchId)`.

Match start in a room: the leader picks a game, ready members press
ready, the leader starts a 10 s countdown (any ready player may cancel).
`StartCountdown` is accepted only when `ready humans ≥ minPlayers`, or
when `botFill` is on (then bots will make up the difference). At zero the
server re-validates and seats:
1. humans: the first `maxPlayers` ready members by ready time (the rest
   spectate);
2. bots: if `botFill` is on and humans < `minPlayers`, add
   `minPlayers − humans` CPU seats — **never more than the minimum**;
3. sends `MatchStarting(matchId)` to everyone; seated players open the
   game window, everyone else can watch.
Examples: Royal Ludo (4–4) with 2 ready + botFill → 2 humans + 2 bots;
without botFill it can't start. UNO-style
(4–8) with 2 ready + botFill → 2 humans + 2 bots; with 5 ready → 5
humans.

### MATCHMAKING channel — Quick Play

The player is identified by the connection (bearer `userId.secret`);
`JoinQueue` carries only the game id. The server replies with the
`matchId` of the match session to open.
```
client                         server
  │ JoinQueue(gameId) ───────────▶│ Ack; user state = IN_QUEUE(gameId)
  │◀──────── QueueStatus(found, needed, searchDeadline, serverTime)
  │          …QueueStatus each time a player joins/leaves…
  │◀──────── MatchFound(matchId, gameId, humanPlayers, cpuPlayers)
  │          (continues on MATCH channel: MatchSnapshot…)
  │ LeaveQueue ─────────────────▶│ any time before MatchFound
```
Server algorithm, one queue (an actor with one inbox) per game:
1. On `JoinQueue`: reject `GAME_UNAVAILABLE` (unknown / disabled game) or
   `ALREADY_IN_QUEUE` (already queued, in a countdown or in a match).
2. Group waiting users into a forming match of exactly
   `game.matchmaking.seats` seats (Royal Ludo: 4 — not minPlayers). Its
   search window opens with the first player: `searchDeadline = now +
   game.matchmaking.searchSeconds` (30 s for Royal Ludo).
3. **Early start**: the moment `seats` humans are in, send `MatchFound`
   and start — don't wait for the deadline.
4. **Deadline**: start with the humans present; if
   `matchmaking.fillWithCpu`, fill every empty seat (up to `seats`) with a CPU
   (`SeatInfo.cpu = true`, `controllerUserId = "cpu-<n>"`) driven by the
   engine's `autoplay` with a human-like delay. If CPU fill is off and
   fewer than `minPlayers` humans are present, send
   `QueueCancelled(NOT_ENOUGH_PLAYERS)`.
5. A disconnect while queued removes the user from the queue.
6. `LeaveQueue` after `MatchFound` is ignored (the match already exists).

Seats and colours are assigned randomly by the server, as in rooms.
Public matches always use the game's traditional rules
(`PlayMode.PUBLIC_MATCH`).

### Spectators (rooms only)

Room members present but not seated are spectators: not ready, beyond
`maxPlayers`, removed for being away, or arrived mid-match. They:
- receive `MatchSnapshot` with `viewer.role = SPECTATOR`, `seat = null`
  and an empty `legalMoves`, plus `MatchEvent` / `MatchEnded`;
- may send `MatchRequestSnapshot` to start watching; any game action from
  them is rejected (`NOT_YOUR_TURN`);
- stay in the room's voice (listen and talk);
- never count toward seats, `minPlayers`, `maxPlayers` or bot fill;
  `maxSpectators` caps them when a game sets it.

**Hidden information:** `publicState` is built per viewer by the game's
`viewFor`. The server must never put data in it that the viewer may not
see (a spectator gets the public table; a player gets that plus their
own hand). The app never filters secrets out itself.

Quick Play (random) matches have no spectators.

### MATCH channel
Client → server: game actions (`RollDice(matchId)`, `MovePiece(matchId,
pieceId)` for Royal Ludo), `MatchRequestSnapshot(matchId, lastVersion)`.
Server → client: `MatchSnapshot` (per-viewer; spectators get no legal
moves; `seats[].cpu` marks CPU seats), `MatchEvent(version, event)`,
`MatchEnded(ranking)`. Timers, autoplay, away detection and removal are
exactly as in the report (Phase 10): 30 s turns, 5 s wait when the actor
is disconnected, away after 3 missed turns or 60 s disconnected.

## Games — how the server knows a game

Games are code, not database rows. Each game implements `Game :
GameDescriptor` in `appengine/.../games/<game>/` and is registered once
in `GameRegistry` (Koin `gamesModule` on the app; the same list on the
server). The descriptor gives everything matchmaking and rooms need:

| Field | Royal Ludo |
|---|---|
| `id` | `royal-ludo` |
| `displayName` | Royal Ludo |
| `minPlayers` / `maxPlayers` | 4 / 4 (every table has 4; the engine itself supports 2–4) |
| `maxSpectators` | unlimited |
| `turnPolicy` | Sequential |
| `timing` | 30 s turn, 5 s disconnected wait, away after 3 misses / 60 s |
| `matchmaking` | Quick Play always 4 seats, 30 s search, CPU fill on |
| `showcase` | taglines Roll • Race • Rule, art key `royal-ludo` |

A game can be switched off without a release through a remote flag
(`royal_ludo_enabled`, or `game_enabled_<id>` for others); the server
must honour the same switch (`GAME_UNAVAILABLE`).

## Invite links and pushes

- **Invite link (shared text):** `https://kglabs28-yapnplay.web.app/join/<code>`,
  The app side is done (manifest filter with autoVerify). Server side
  (not in this repo): the host must serve `/.well-known/assetlinks.json`
  for package `com.kglabs28.yapnplay` with the signing SHA-256s (debug
  `7C:9E:BF:37:FA:FD:C7:2F:91:11:EE:09:DF:B2:10:BE:15:F0:F3:C6:57:BC:6E:A1:C2:14:B0:01:61:D2:F9:00`,
  plus the release key), and a `/join/<code>` page for people without
  the app. Until that file is served, Android opens these links in the
  browser. Changing the host: INVITE_HOST and the manifest placeholder.
- **Opening an invite:** the app calls `GET /rooms/preview/{code}`;
  member → room lobby, otherwise "Join <room name>?" → `JoinRoom(code)`.
- **Join requests:** `JoinRoom(code)` from a non-member answers
  `JoinRoomResult(PENDING_APPROVAL, roomName)` and puts the request in
  the admin's Waiting List; a member gets `JoinRoomResult(JOINED, roomName,
  room)`. On approval the requester is added and gets a push.
- **Room activity push** (FCM data message): when a member comes online
  in a room, push to the room's other members who are not present:
  `type=room_activity, roomId, roomName, title="<name> entered <room>",
  body="<N> players are in <room>"`. One notification per room
  (replaced); throttle per user+room (e.g. once per 5 minutes) and
  aggregate several arrivals. Tap → `yapnplay://room/<roomId>` → lobby.

## Royal Ludo match additions

- **Events:** besides rolls, moves and captures, a third six sends
  `TurnReverted(seat, pieces)` (board as it was when the turn began) — the
  app glides every changed piece back.
- **Snapshot:** includes the finishing order so far (`finishedSeats`), so
  places ("1st", "2nd") show on the board and the cards during the match.
  Also `startedAt` (when the match began) for the match clock in the top bar.
- **Roll before move:** a roll is never skipped. Every `DiceRolled` is sent,
  and it comes before the move it allows, including an automatic single
  move, a bot's move and a timed-out turn. The app shows the full roll and
  lets the die rest on its face before any piece moves.
- **Fairness:** every roll (players and bots alike) comes from the
  server's secure random source, uniform and independent each time; a
  6-6-6 happens about once in 216 sets of three rolls. The app never rolls.
- **Spectator count:** the number of people actually watching (not
  players); the top bar lists only spectators.

## Moderation channel (all games and rooms)

- `ReportPlayer(contextId, reportedUserId, reason="voice_chat")` from the
  reporter. Voters = everyone present (players and spectators) except the
  reporter and the reported person; fewer than 2 → rejected.
- Each voter gets `ReportVoteRequested(reportId, reporterName,
  reportedName, expiresAt)` and answers `CastReportVote(reportId, agree)`;
  no answer by `expiresAt` = no vote.
- Required agreements: 2 when there are 4 or fewer voters, otherwise 3.
- `ReportResolved(reportId, upheld, reportedName, agreed, required)` to the
  reporter. Upheld → incident against the reported person; otherwise
  against the reporter (false report).
- An incident sends `PenaltyIssued(pointsDeducted, blockedUntil)` to the
  person it is against: points are deducted and new matches are refused
  until `blockedUntil`. A completed rewarded ad
  (`POST /penalties/clear-with-ad`, verified server-side) lifts the block.
- Muting someone "for me" is local to the device (voice provider:
  unsubscribe their audio); the server is not told.

## Error codes

`NOT_AUTHENTICATED, NOT_FOUND, NOT_MEMBER, NOT_LEADER, ROOM_FULL,
INVALID_CODE, ALREADY_IN_ROOM, NOT_ENOUGH_READY, STALE_VERSION,
NOT_YOUR_TURN, ILLEGAL_MOVE, MATCH_ENDED, RATE_LIMITED, BAD_REQUEST,
ROOM_LIMIT_REACHED, ALREADY_IN_QUEUE, GAME_UNAVAILABLE`. The app maps
each to a friendly sentence; raw codes are never shown.

## Client status (what is mocked today)

Every client call goes through an `*ApiClient` / `*ClientImpl` whose
production code is written but commented out, and which returns
`FakeDataProvider` data after a 600 ms delay. Switching to the real
server = uncomment the production path, delete the fake lines. The
WebSocket paths additionally need the app-wide connection manager
(report Phase 12).
