# YapNPlay — Decisions that override the project report

`docs/project-report.md` is the original plan. These later decisions win
where they disagree. Newest first. Every AI agent reads this file too.

## 2026-10-17 — permissions, invites, notifications, member card

- **Permissions:** mic + notifications are asked once, on first launch
  only. Turning the mic on asks again if it was denied; a permanent
  denial shows a dialog with "Open settings".
- **Invite links:** shared as `https://kglabs28-yapnplay.web.app/join/<code>`
  (tappable in every chat app), with the room code in the message too.
  Android opens the app directly once the host serves assetlinks.json
  (server task, see the backend contract). Opening one:
  already a member → straight to the room lobby; otherwise the "Join this
  room?" Yes/Cancel dialog (it names the room, never the code). Entering
  a code + JOIN shows the same dialog. Yes on someone else's room sends
  a join request: "Request sent — the admin has to approve"; the admin
  approves from the Waiting List. Room codes are typed on an in-app
  keypad that replaces Your Rooms + Online Friends while the code field
  is active.
- **Home:** Play With Friends card on the left, Game Zone on the right.
- **Notifications:** "<name> entered <room>" / "N players are in <room>";
  tapping one opens that room's lobby.
- **Notification history:** the last 10 room pushes are kept on the
  device (also when notifications are off). The top bar bell shows an
  unread badge and a drop-down list; tapping a row opens that room's
  lobby; closing the list marks everything read.
- **Member card:** admin = crown on the avatar (no "Admin" text), leader =
  gold border; mic/speaker off = crossed icons; voice wave fills the card.
- **Admin can remove a member** from the room (member menu).

## 2026-10-17 — room lobby: roles, ordering, buttons

- **Two roles, independent:**
  - **Admin** (crown icon) — created the room; only they manage membership
    and see the Waiting List button. If the admin leaves, the member on
    top of the room leaderboard becomes admin.
  - **Leader** (golden card border) — only they pick the game, toggle house
    rules and press START MATCH. If the leader leaves or drops, the member
    online longest in the current session (`onlineSince`) becomes leader.
  - Either role is handed over by tapping a member: the menu shows "Make
    Leader" if you are the leader, "Make Admin" if you are the admin, plus
    Add/Remove Friend for that person.
- **Member order:** you first, then online members (leader, admin, the
  rest), then offline members greyed out.
- **Member card:** talking wave only while that person speaks; otherwise
  "Mic off" / "Speaker off" / "Mic & speaker off" so everyone sees each
  other's voice state. Ready / Not Ready / Watching chip once a game is set.
- **Two phases:** (1) choosing a game — same carousel/grid as the Game
  Zone, SELECT GAME (or a grid tap) selects, never starts; (2) game chosen —
  game art in the middle, house rules on the right ("Fill with Bots" first,
  then the game's toggles; Rules button opens the fixed rule sheet), and
  Change Game below for the leader. Changing the game resets everyone's
  ready/watching and loads the new game's default rules.
- **Main button:** leader → START MATCH (enabled once a game is set, ≥ 2
  humans ready, and the minimum met or bot fill on). Everyone else →
  disabled until a game is set, then NOT READY ↔ READY; when every seat is
  taken, members without a seat get NOT WATCHING ↔ WATCHING instead, until a
  seat frees up again.
- **Defaults are off:** not ready, not watching, mic off, speaker off.
- Leave Room (bottom right) asks for confirmation; the back arrow only
  leaves the screen and keeps membership.

## 2026-10-17 — spectators

- **Who:** in a room, every present member without a seat spectates:
  members who didn't press ready, ready members beyond `maxPlayers`,
  players removed for being away, and members who join mid-match.
- **What they get:** the live table (`MatchSnapshot` with viewer role
  SPECTATOR, no legal moves) and the room's voice — they listen **and
  talk**, because voice follows the room, not the match.
- **What they don't get:** a seat, any action (the server rejects them),
  or hidden information. Each game's `viewFor` builds the spectator view
  from public data only — UNO-style games show card counts, never hands.
  A "spectators see all hands" house rule could come later; default off.
- **Counting:** spectators never count toward `minPlayers`, `maxPlayers`
  or bot fill. A game may cap them with `maxSpectators` (Royal Ludo:
  no cap; the room's 10-member limit applies anyway).
- **Random (Quick Play) matches have no spectators** for now — watching
  is a room feature. Watching a friend's random match can come later.
- A spectator can't take a seat in a running match; the report's
  replacement-invitation hook is where that will plug in later.

## 2026-10-17 — who plays, and when bots fill seats

Every game declares **minPlayers / maxPlayers** (rooms) and a separate
**Quick Play seat count** (`MatchmakingConfig.seats`, random matches).

| | Random (Game Zone Quick Play) | Room (play with friends) |
|---|---|---|
| Who asks | The player taps QUICK PLAY: app sends `JoinQueue(gameId)` | The leader picks the game and presses Start |
| Identity | From the signed-in connection — **no user id in the message** | Server uses the room's **ready members** — the app never sends a user-id list |
| Seats | Always exactly `seats` (Royal Ludo: 4) | Any count from `minPlayers` to `maxPlayers` humans |
| Bots | Empty seats at the search deadline become CPU | Only if the leader turned on **"Fill with bots"** (first house-rules option): bots fill up to `minPlayers`, never beyond |
| Start | Early when `seats` humans are found, else at the deadline | 10 s countdown for every seated player, then all go to the game window (`MatchStarting`) |
| Server reply | `MatchFound(matchId, …)` | `MatchStarting(matchId)` |

Examples: Royal Ludo is **4–4** (min = max = 4, Quick Play 4): every
table has 4 players — random → humans + CPU; two friends in a room →
they need bot fill on (2 bots join), otherwise Start isn't allowed. UNO-style (4–8): two
friends with bot fill on → 2 bots join (to reach 4); five friends → no
bots; eight friends → all play; two friends with bot fill off → can't
start (`NOT_ENOUGH_READY`).

## 2026-10-17

### Scope
- **Game Zone is now in scope** (the report listed public matchmaking as
  post-POC). Quick Play works like PUBG-style matchmaking: the player picks
  a game, the server runs a search timer (30 s by default, declared per
  game); the match starts **early** the moment the game's Quick Play
  `seats` are all taken by humans; when the timer ends, empty seats are **filled with CPU
  players** (the engine's `autoplay`) as long as the game allows it.
- The game carousel is one reusable component: the Game Zone uses it for
  Quick Play, the room lobby will use it for the leader's game choice.
- Only Royal Ludo is a real game. The other carousel entries are a
  hard-coded placeholder list in `FakeDataProvider.Games` until their
  engines land.

### Rooms
- A user may belong to **up to 5 rooms** (the report said 3). The limit is a
  remote flag in `AppManager` (`max_rooms_per_user`), enforced by the server
  and pre-checked by the app.
- Room codes are **6 digits** (0–9) for now. Letters may come back later.
- The rooms list shows how many members are **live** right now, never chat
  messages or unread counts.

### Profile and progression
- Coins, gems and levels are **not designed yet**; they stay as optional,
  provisional fields so the current UI keeps working.
- XP will be calculated by the server from the user's overall match
  results (random matches and room matches alike). Level derives from XP.

### Design
- The visual design follows the user's neon/glass mockups, not the
  report's Baloo/amber system. Mockup sizes win over the report's 48 dp
  touch / 12 sp text minimums; tappable controls stay at least 32 dp.

### Security (deferred, tracked)
- `androidApp/google-services.json` is committed. Firebase is linked; the
  API key restrictions are not set up yet. Security hardening is planned
  as its own task later.
