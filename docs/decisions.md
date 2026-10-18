# YapNPlay — Decisions that override the project report

`docs/project-report.md` is the original plan. These later decisions win
where they disagree. Newest first. Every AI agent reads this file too.

## 2026-10-18 — profile and friends (contract 0.3.1)

- **Friends are in scope** (the report listed them as post-POC). The
  Profile screen opens from the Home avatar: avatar, name, @username,
  the user's ID, stats, friends, add a friend, friend requests.
- **Names and usernames are not unique.** Each user has a unique
  6-character ID (`userCode`, no look-alike characters) and friends are
  added **only by that ID**.
- **Avatars are presets** (`a01`…), never photos, so nothing to store or
  moderate. The user's own webp set replaces the placeholder icons.
- **Block** sits on a friend's card next to Remove: it ends the
  friendship and stops their friend requests and room invites. A blocked-
  users list and request privacy go under Settings later.
- **Stats:** games played, games won, friends (server-counted).
- **Logout is hidden** while accounts are guest-only (logging out would
  lose the account); its confirmation dialog is built and switches on
  with sign-in.

## 2026-10-18 — backend: separate repo, free hosting, no shared code

- **The backend lives in its own repository** (`MyDocs\BP\YapNPlay`), not in
  this Android repo (the report planned a `server` module here). This repo
  keeps only app code and stays a single `:appengine` library.
- **App and server share no code.** Only JSON crosses the network, so each
  side has its own message classes. `docs/backend-api-contract.md` is the
  contract; the server keeps one golden JSON file per message and tests
  against them, and the app will do the same when its tests resume.
- **The server owns the game rules.** The engines in `games/*/engine` here
  only drive the fake servers; they go away as each fake is replaced by the
  real server (Phases 12 and 15). The app draws what the server sends.
- **Everything stays free** until the app is live and outgrows free tiers.
  The server never runs on the owner's laptop for testing; it must work
  while the laptop is off. Hosting: Oracle Cloud Always Free if signup
  succeeds, otherwise Render + Neon (+ an uptime ping). Voice: LiveKit
  Cloud free plan.

## 2026-10-17 — City Bazaar v0.2 rules (owner's answers)

The full rules are in `docs/city-bazaar-rules.md` and replace v0.1's
"Changed from the spec" table. What changed:

- **Board and prices:**
  - The spec's board, groups and prices, with the original game's card
    values where known.
  - One build price per city, for each house and the hotel.
- **Rent and building:**
  - 3 of a colour doubles rent at any tier.
  - Landing on your own city offers one house, up to 3 houses and then a
    hotel. A full colour group isn't needed.
- **Doubles** mean nothing, except as the way out of jail.
- **Jail:** landing on JAIL (or a card) puts you in. Each turn: 3 rolls for
  doubles, then pay ₹500 or take half rent until your next turn.
- **Debt:**
  - Buildings are sold in reverse (the hotel back to 3 houses), then cities
    go to the bank or to auction.
  - Auction: other players only, opening at half the price, steps growing
    with the price, a 5-second timer that restarts on each bid. The winner
    pays the seller, with no backing out; with no bids, the bank buys it.
- **Timeouts:**
  - Timer runs out: the bot rolls and moves only, plus a ₹50 fine if the
    player didn't roll and Autoplay is off.
  - Autoplay: the same, with no fine.
  - CPU players: buy, build and bid.
- **AppManager values:**
  - Starting balance (₹20,000), round limit (30) and time limit (30 min).
  - The two limits each have an on/off room toggle.
- **Players and name:**
  - Rooms 3–6 players, random tables 4.
  - Name "City Bazaar". The image file is still `ic_game_city_bazaar.png`
    until it's replaced.
- **UI:**
  - Your cities stacked by colour at the bottom.
  - Full title cards: current tier highlighted, rent now (2X), next sale
    value, and all costs.
  - Tiles glow on the board while their cards are open.
  - Names drawn at the board's angle.
  - "My Cities" replaces "Build House/Hotel", since building happens on
    landing.
  - Trade explains that selling and auctions happen when you're short of
    money.

## 2026-10-17 — City Bazaar v0.1 (built while the owner was away)

- **New game:** City Bazaar (`city-bazaar`), our Indian city-trading board
  game. The rules are in `docs/city-bazaar-rules.md`. The spec it came from
  was checked rule by rule; its **"Changed from the spec"** table lists 15
  decisions that need review (groups, prices, jail, doubles, end of match,
  timeouts, and more).
- **Code layout**, the same as Royal Ludo:
  - `engine`: pure, with its bot and a cautious bot for timed-out turns.
  - `utils`: board, cards, constants and rupee formatting.
  - `domain` and `data`: models, the fake server, client and repository.
  - `ui`: the board painter and projection, cards, dialogs, mappers, and
    the screen's contract, view model and screen.
- **Shared table UI:** the top bar, metal title, match clock, audience
  strip, glass button, frosted glass, turn-timer border, card avatar,
  mute button and spectator mic moved from Royal Ludo to
  `ui/components/table`, so every game's table uses the same pieces.
  `ReportPersonUi` moved to `ui/components/moderation`.
- **Routing:** opening a match now carries the game id (Game Zone and room
  lobby), and `NavController.openMatch` picks the game's screen.
- **Fake server:** Quick Play seats 4 (you plus 3 CPU players); a room
  match seats 6 with one spectator. CPU players are the bot; your
  timed-out decisions use the cautious bot.
- **Not in v0.1:**
  - Trading (the button says "coming soon").
  - Seat handover, the idle check and spectating flows (Royal Ludo has
    them; City Bazaar gives a left seat to the bot).
  - Room house-rule toggles reaching the engine.
  - Game-specific sounds (it reuses dice, hop, capture and win).
  - Real art: the catalog uses a glyph; the board is drawn in code.

## 2026-10-17 — seat handover (every game, room matches only)

- **Pausing:** play pauses only while someone decides on a seat for
  someone else: the leader choosing, or a named spectator answering an
  offer. Exiting and the "still here?" check never pause anyone.
- **For every game:** seat handover lives in shared code, not in Royal
  Ludo: `features/seathandover` (rules, offer and stages, texts) and
  `ui/components/seat` (banner, exit dialog with picker, spectator prompt,
  leader picker, "still here?" check). A game only wires it into its table.
- **Rooms only:** spectators, the spectator strip in the top bar and every
  handover step exist only in room matches. In Quick Play (random players)
  there are no spectators, so a seat that is left, idle or away goes to the
  bot.
- **Exit:** the leave dialog asks "Give your seat to": the bot (first, and
  the default) or any spectator.
  - **Bot:** the bot takes the seat at once.
  - **A spectator:** they get a 30 s accept/decline prompt while the match
    waits, and everyone sees "Paused · Riya's seat offered to Karan · 24s".
- **Leader fallback:** if that spectator declines or doesn't answer, the
  room leader gets 10 s to pick someone else from the same list (the match
  waits for that too). Their pick gets the same 30 s prompt. A decline, no answer,
  or no pick from the leader hands the seat to the bot.
- **No leader present:** if the leader has left (or is the one leaving), the
  person online longest in the session decides. This is the lobby's own
  leader rule.
- **Idle player:** if the bot played someone's last 3 whole turns on
  timeout (Autoplay off; extra rolls from a six count as one turn), they
  get a 15 s "Looks like you left" dialog with an "I'm still here" button.
  The match keeps going, and playing a turn counts as still here.
  - **If they don't answer in a room:** the leader picks (then spectator,
    then bot, as above).
  - **If they don't answer in Quick Play:** the bot takes the seat.
  - **Taking the seat back from the bot:** turn Autoplay off; the switch
    shows on while the bot holds the seat.
- **One dialog for all three cases** (`SeatPickerDialog`, from the user's
  "Seat Inactive" design): an amber warning title, a timer chip, the
  message, and a sideways row of spectator avatars; the picked one gets a
  tick and a cyan border.
  - **Leader:** spectator tiles, with **Offer to Bot** and **Invite to
    Seat**.
  - **Idle player** (3 turns): a Bot tile first (picked by default), then
    spectators, with **I'm Here** and **Spectate**. Spectate hands the seat
    to the pick, and the player keeps watching as a spectator.
  - **Exit:** a Bot tile first (picked by default), then spectators, with
    **Cancel** and **Exit**. Exit passes the seat to the pick.
- **Away** (app in the background) works as before: the seat is open to all
  spectators in a room, and goes to the bot in Quick Play.
- **Fake server:** 45 s into a room match, Riya leaves and offers her seat to
  Karan, who declines after 6 s. The leader then picks: you, if you lead the
  room (otherwise Neha picks after 4 s). The chosen spectator accepts after
  6 s. In Quick Play, Riya goes away and the bot takes over. Let three of
  your own turns time out to see the "still here?" check.

## 2026-10-17 — leaving the app mid-match (away, open seat, stand-in)

The server can't tell whether someone minimised the app, lost signal or took
a call, so every case is handled the same way, as "away". The server drives
it; the app only reports that it went to the background or came back.

- **On the phone:** in the background, sound and music stop at once and the
  mic turns off. After a 5 s grace (so pulling down the notification shade
  doesn't count), the app tells the server the user is away. Nothing
  animates in the background; on return the app jumps to the latest board
  and tells the server the user is back.
- **Away:** the server plays their turns at once (no waiting out the turn
  timer). Their card shows "Away".
- **Open seat:** after 30 s away, or 2 missed turns, the seat is offered to
  everyone watching for 30 s ("Riya left · seat open 24s"). The bot keeps
  playing it meanwhile.
- **Stand-in:** a spectator who takes it plays on from that position with
  the same colour and pieces, under their own name and voice. Rewards are
  reduced because they joined late. Only a real person's seat is offered,
  never a bot's.
- **Nobody takes it:** the bot plays it to the end, and the card shows
  "Bot".
- **Coming back:** before anyone takes the seat (including after it went to
  the bot), the seat is theirs again at once. After a stand-in took it, they
  rejoin as a spectator and the match counts as a loss.
- **Fairness:** a match left like this counts against the leaver the same
  way an upheld report does. Several in a short time trigger the existing
  penalty (points deducted, Quick Play blocked, an ad lifts it).
- **Push:** if it becomes their turn while they're away, one "Your turn —
  come back" notification is sent, never repeated.
- **Tuning:** all the timings live in the server's game config, so they can
  change without an app update.
- **Fake server:** 45 s into a match, the last opponent goes away. Their seat
  opens, and 10 s later a spectator takes it, so the whole flow can be seen.
  The user's own seat is never taken in the fake, so they can always come
  back. The spectator's invite dialog is built but only shows to a
  spectator, which the app can't be yet.

## 2026-10-17 — Royal Ludo table, sound, voice reports

- **Table layout:** the user's card bottom-left (Mic, Autoplay, Roll
  Dice), the other players on the right in turn order (clockwise from the
  user), the diamond board as large as fits. Tapping another player's
  card offers "Hear <name>" — mute them for yourself only.
- **Turn:** the top bar shows whose turn it is and the seconds left; the
  turn timer also runs round the player's card border. Turns are 10 s.
- **Dice:** takes the colour of the player rolling; hidden while a piece
  is being chosen and moved. Tapping it rolls on your turn.
- **Rules:** one legal move after a roll is played automatically; a third
  six undoes the whole turn. Finished players get their place in their
  base and as a badge on their card.
- **Sound:** background music, dice roll, hop, capture and winner sounds;
  effects and music each switchable in the match settings.
- **Voice reports (all games):** Settings → Report a player. Everyone else
  present confirms within a short time; 2 agreements needed with 4 or
  fewer voters, else 3. Upheld → incident against the reported person,
  otherwise against the reporter. An incident deducts points and blocks
  play for a while; a rewarded ad lifts the block.

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
- **Joining mid-match:** any room member can join the room at any time and
  watch the running match from wherever it is (start, middle or end). The
  lobby's main button becomes **WATCH MATCH** while a match is in progress
  (`RoomInfo.matchInProgress`). A spectator gets a seat only through the
  seat handover (see the seat handover entry).
- **Fake server:** Family Fun always has a match running without you. It
  lands at a random stage each time, and about 15 s in, the leaving player
  offers their seat to you.

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
