# YapNPlay — Backend API contract (v0.3.1)

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
**0.3.1**. Patch: optional field / new message. Minor: new required
field, channel or game-interface member. Major: breaking change.
Every bump is announced in `docs/decisions.md`.

0.3.1 added: profile and friends — `Me`/`FriendSummary` fields `username`,
`userCode`, `avatarId` (+ `Me.gamesPlayed`, `gamesWon`); `PATCH /me` takes
`UpdateProfileBody`; friend requests, remove friend, block, block list and unblock endpoints;
`RoomMember.avatarId` (match tables find players' avatars by user id).

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
| `PATCH /me` | `UpdateProfileBody(displayName?, username?, avatarId?)` | `Me` | Only the fields sent change. Name: trim, 1–24 chars, no control chars. Username: 3–20 of `A–Z a–z 0–9 _ .`. Neither is unique. `avatarId` ∈ `a01`…`a18` (preset avatars). Same JSON as the old `UpdateName` when only the name changes. |
| `GET /rooms/mine` | — | `MyRooms(rooms: [RoomSummary])` | Rooms the user belongs to. |
| `POST /rooms/{roomId}/voice-token` | — | `VoiceToken(url, token, expiresAt)` | Members only (`NOT_MEMBER`); LiveKit, 10-minute TTL. |
| `GET /rooms/preview/{code}` | — | `RoomPreview(roomId, code, name, description?, memberCount, onlineCount, isMember)` | Before joining: "Join this room?". `isMember` from the connection's user; the app opens the room directly when true. `NOT_FOUND` / `INVALID_CODE`. |
| `POST /devices` | `RegisterDevice(token, platform)` | — | FCM token for pushes; upsert per user+token, re-sent when the token changes. |
| `GET /stats/online` | — | `OnlineStats(online)` | Connected users right now (Game Zone pill). |
| `GET /friends` | — | `Friends(friends: [FriendSummary])` | The user's friends. |
| `GET /friends/requests` | — | `FriendRequests(requests: [FriendRequestSummary(requestId, from: FriendSummary)])` | Requests other people sent the user, newest first. |
| `POST /friends/requests` | `SendFriendRequestBody(userCode)` | `SendFriendRequestReply(outcome)` | `outcome` ∈ `SENT`, `NOT_FOUND`, `SELF`, `ALREADY_FRIENDS`, `ALREADY_REQUESTED`, `BLOCKED` (the user blocked them), `ACCEPTED` (they had already asked the user: now friends). A person who blocked the user gets `NOT_FOUND`. |
| `POST /friends/requests/{requestId}/accept` | — | — | Becomes friends both ways. |
| `POST /friends/requests/{requestId}/decline` | — | — | The sender isn't told. |
| `DELETE /friends/{userId}` | — | — | Ends the friendship both ways. |
| `POST /blocks` | `BlockUserBody(userId)` | — | Ends any friendship and pending request; the blocked person can't send the user friend requests or room invites. |
| `GET /blocks` | — | `BlockedUsers(users: [BlockedUserSummary(userId, displayName, username, avatarId, blockedAt)])` | Newest first; `blockedAt` epoch millis. |
| `DELETE /blocks/{userId}` | — | — | Unblock; no friendship comes back. |
| `GET /health` | — | `{status, contractVersion}` | No auth. |

### `Me`
```json
{ "userId": "user-keshav", "displayName": "Keshav", "xp": 2450, "level": 12, "coins": 4250, "gems": 120,
  "username": "keshav_28", "userCode": "K7Q2M9", "avatarId": "a03", "gamesPlayed": 128, "gamesWon": 47 }
```
- `userCode`: the user's **unique** 6-character ID that friends type to add
  them, from `ABCDEFGHJKLMNPQRSTUVWXYZ23456789` (no 0/O or 1/I). Assigned
  by the server at sign-up, never changes. `username` and `displayName`
  are not unique.
- `gamesPlayed`, `gamesWon`: counted by the server over all matches.
- `FriendSummary` carries the same `username`, `userCode` and `avatarId`.
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

## City Bazaar match additions

- **Actions** (`GameAction` on the match channel):
  - `RollDice`
  - `BuyTile` / `DeclineTile`
  - `BuildOnTile(tile)` / `SkipBuild`
  - `SellBuilding(tile)` / `SellTile(tile)`
  - `PayJailFine` / `UseJailPass`
  - `DeclareBankruptcy`
  - `SetAutoplay`, `SetMic`, `SetPresence` and `LeaveSeat` work as for
    every game.
- **Snapshot:**
  - Per player: `cash`, `position`, `inJail`, `jailTries`, `jailPasses`,
    `bankrupt`.
  - Table: `owners` (tile to seat) and `levels` (tile to 0–4).
  - Turn: `phase` (`ROLL`/`JAIL`/`BUY`/`BUILD`/`DEBT`/`FINISHED`),
    `die1`/`die2`, `pendingTile`, `debt(amount, reason, tile)`, `buildable`
    (the current seat's cities).
  - Match: `round`/`roundLimit`, `netWorth` per seat, and `ranking` once
    finished.
- **Events:** `DiceRolled`, `TokenMoved(steps, jump)`,
  `MoneyMoved(from?, to?, amount, reason, tile?)` (null means the bank),
  `CardDrawn(cardId)`, `TileBought`, `BuildingBuilt`, `BuildingSold`,
  `TileSold`, `SentToJail`, `WentBankrupt`, `TurnSkipped`, `TurnPassed`,
  `MatchEnded`.
- **Timeouts:** a seat whose 20 s runs out is played by the engine's
  cautious bot (rolls, never buys or builds). CPU seats, Autoplay and away
  seats use the full bot.

## Presence and seat handover (every game)

Messages live in `protocol/SeatHandoverMessages.kt`; the rules are in
`features/seathandover/domain/SeatHandoverRules.kt`. Spectators and every
handover step are for **room matches only**; in Quick Play a left, idle or
away seat goes to the bot.

- **`SetPresence(matchId, away)`:** the app sends `away = true` 5 s after
  going to the background, and `false` when it comes back. A dropped
  connection counts as away after the same grace. While away, the bot plays
  that seat's turns at once. The snapshot carries each player's `presence`:
  `PLAYING`, `AWAY` or `BOT`.
- **Snapshot:** `roomId` (null for Quick Play) and `seatOffer(seat,
  leaverName, stage, endsAt)`. Stages:
  - **`ConfirmingAbsence(userId)`:** the "still here?" check. Play goes on,
    and a roll or move by them also ends it.
  - **`ToSpectator(userId, round)`:** a named spectator decides. The match
    waits.
  - **`LeaderChoosing(leaderUserId)`:** the leader picks on the owner's
    behalf. The match waits.
  - **`OpenToAll`:** the owner is away. The bot plays meanwhile.
- **Leaving:** `LeaveSeat(matchId, giveToUserId)`, where null means the bot.
  If a spectator is named, they get `ToSpectator` for 30 s and answer with
  `AnswerSeatOffer(matchId, accept)`.
- **Leader fallback:** a decline or timeout in round 1 moves to
  `LeaderChoosing` for 10 s. The decider is the room leader, or, if the
  leader is gone or is the one leaving, whoever has been online longest.
  They answer with `ChooseSeatTaker(matchId, giveToUserId)`, where null means
  the bot. Their pick gets `ToSpectator` round 2. A decline, timeout or no
  pick means the bot.
- **Idle check:** after 3 whole turns in a row the bot played on timeout
  (the owner didn't act and had Autoplay off; extra rolls count once), the
  server opens `ConfirmingAbsence` for 15 s. `ConfirmPresence(matchId)`
  cancels it. Otherwise the leader is asked; in Quick Play the bot takes the
  seat. Turning Autoplay off reclaims a seat the bot holds, unless someone
  else took it.
- **Away seat:** after `openSeatAfterSeconds` (30) away, or
  `openSeatAfterMissedTurns` (2), an `OpenToAll` offer runs for
  `seatClaimSeconds` (30). A spectator takes it with `TakeSeat(matchId,
  seat)`: the first one wins, and the rest get `SEAT_TAKEN`. If nobody takes
  it, the bot does. `SetPresence(away = false)` gives the seat back unless a
  stand-in took it.
- **Hold:** only `LeaderChoosing` and `ToSpectator` pause play, because
  someone is deciding on a seat for someone else. While either lasts,
  nobody plays. When it ends, the player whose
  turn it is gets a fresh turn timer.
- **Stand-in:** keeps the seat's colour and pieces, and gets reduced rewards.
  The person replaced watches as a spectator; their match is a loss and an
  abandonment incident (moderation penalties apply).
- **Push:** if it becomes someone's turn while they're away, one "Your turn"
  notification is sent per match.
- **Timings:** all of them live in `game.presence` / `game.seatHandover`
  config.

## Moderation channel (all games and rooms)

- `ReportPlayer(contextId, reportedUserId, reason="voice_chat")` from the
  reporter. Voters = everyone present (players and spectators) except the
  reporter and the reported person; fewer than 2 → rejected.
- Each voter gets `ReportVoteRequested(reportId, reporterName,
  reportedName, expiresAt)` and answers `CastReportVote(reportId, agree)`
  (shown as Agree / Disagree). No answer by `expiresAt` counts as no vote.
- **Every voter is recorded:** the server stores a `ReportBallot(reportId,
  voterUserId, response, recordedAt)` for each person asked. The response is
  `AGREE`, `DISAGREE` or `NO_RESPONSE`; `NO_RESPONSE` is written at
  `expiresAt` for anyone who didn't answer. Only the first answer counts.
  Ballots are kept for review and abuse checks, such as someone who always
  agrees or never answers, and are never sent to other clients.
- Required agreements: 2 when there are 4 or fewer voters, otherwise 3.
- `ReportResolved(reportId, upheld, reportedName, agreed, disagreed,
  noResponse, required)` to the reporter. Upheld → incident against the reported person; otherwise
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
