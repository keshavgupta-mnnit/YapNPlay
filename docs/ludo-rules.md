# Royal Ludo — Rules Document

**Version:** 1.0.0
**Last updated:** 2026-10-15

This document describes every rule encoded in the `:games:ludo` engine. Each rule has a number, a description, and a worked example.

---

## 1. Board layout

The board has 52 shared squares indexed 0 to 51, arranged in a clockwise loop.

| Colour | Start square | Direction |
|---|---|---|
| Red | 0 | Clockwise |
| Green | 13 | Clockwise |
| Yellow | 26 | Clockwise |
| Blue | 39 | Clockwise |

**Safe squares** (no capture possible): the four start squares (0, 13, 26, 39) and the four star squares (8, 21, 34, 47).

**Example:** A red piece at progress 4 sits on square 4. A blue piece at progress 23 sits on square (39+23)%52 = 10. If the red piece moves 6 squares to progress 10 (square 10), it lands on the blue piece's square.

---

## 2. Piece progress

Each piece has a progress value:

| Progress | Meaning |
|---|---|
| -1 | In base (not yet on the board) |
| 0 | Just entered the track (on the start square) |
| 1-49 | Moving along the shared track |
| 50 | Last shared square before the home column |
| 51-55 | In the private home column (5 squares) |
| 56 | Finished (reached the centre) |

**Absolute square** = (colour start + progress) mod 52.

**Example:** A green piece at progress 10 sits on square (13+10)%52 = 23. A yellow piece at progress 10 sits on square (26+10)%52 = 36.

---

## 3. Colours by player count

- **2 players:** Use opposite colours (e.g., red vs yellow, or green vs blue).
- **3 players:** Use any three colours.
- **4 players:** Use all four colours.

Seats take colours in board order, clockwise from a random colour, so turns go round the board (2 players sit opposite).

---

## 4. Rules configuration

| Rule | Default | Description |
|---|---|---|
| baseExitRolls | {6} | Rolls that let a piece leave base |
| extraTurnOnSix | true | Extra turn after rolling a 6 |
| extraTurnOnCapture | true | Extra turn after capturing |
| extraTurnOnHome | true | Extra turn after a piece reaches home |
| threeSixesForfeit | true | Third consecutive six forfeits the turn |
| pairProtection | true | Two or more same-colour pieces protect each other |
| exactFinish | true | Need an exact roll to finish |
| autoMoveWhenSingleOption | false | Autoplay picks the only legal move immediately |

---

## 5. Rolling and leaving base

**Rule:** A piece can leave base only when the roll matches one of the `baseExitRolls`. With traditional rules, only a 6 lets a piece leave base.

**Assumption confirmed:** A 6 with no legal move passes the turn without an extra roll.

**Example:** Red has all pieces in base. Red rolls a 6. One piece can now leave base (progress becomes 0). Red gets an extra turn. Red rolls a 2. No piece can move (all others still in base). Turn passes to the next player.

---

## 6. Moving

**Rule:** A piece moves forward by the roll value. If the piece is in base and the roll is a 6, it enters the track at progress 0.

**Example:** Red has a piece at progress 10. Red rolls a 4. The piece moves to progress 14 (square 14).

---

## 7. Exact finish

**Rule:** A piece needs an exact roll to reach progress 56 (finished). If the roll would overshoot, the piece cannot move.

**Assumption confirmed:** Smaller rolls move a piece closer; larger rolls cannot move that piece. If no piece can move, the turn passes.

**Example:** A piece is at progress 54 (2 squares from finish). Roll 2 → piece finishes. Roll 1 → piece moves to 55. Roll 3-6 → piece cannot move.

---

## 8. Extra turns

**Rule:** An extra turn is granted when:
1. The player rolls a 6 (extraTurnOnSix).
2. The player captures an opponent's piece (extraTurnOnCapture).
3. The player's piece reaches home (extraTurnOnHome).

**Example:** Red rolls a 6, moves a piece, and gets another turn. Red rolls a 3, moves a different piece, and the turn passes.

---

## 9. Three consecutive sixes

**Rule:** If a player rolls three 6s in a row, the third roll forfeits the turn. Nothing moves, and the turn passes to the next player.

**Assumption confirmed:** The consecutive-sixes counter counts rolls by the same seat in a row and resets when the turn passes to another seat or when a non-6 is rolled.

**Example:** Red rolls 6 (extra turn), rolls 6 again (extra turn), rolls 6 a third time — forfeit! Nothing moves on the third roll, and the turn passes.

---

## 10. Capture

**Rule:** Landing on a lone opposing piece on a non-safe square sends it to base. The capturing player gets an extra turn.

**Assumptions confirmed:**
- Captures happen only on the shared track, never in the home column.
- A lander captures every lone opposing piece on a non-safe square, once, with a single extra turn.

**Example:** Red lands on square 10 where a lone blue piece sits. Blue's piece goes to base. Red gets an extra turn.

---

## 11. Pair protection

**Rule:** Two or more same-colour pieces on one unsafe square are protected. An opponent may land there but captures nothing and gets no extra turn.

**Assumptions confirmed:**
- A protected group of two or more same-colour pieces is never captured.
- When a group drops to one piece and an opposing piece is on that square, the remaining piece goes to base with no extra turn for the opponent.

**Example:** Red has two pieces on square 15. Blue lands on square 15. Blue captures nothing and gets no extra turn. Red's pair is safe.

---

## 12. Pair break

**Rule:** When a protected pair breaks (one piece moves away) and an opposing piece stands on that square, the piece left behind goes to base. The opponent gets no extra turn.

**Example:** Red has two pieces on square 20. Blue is also on square 20 (landed safely). Red moves one piece away. The remaining red piece goes to base. Blue does not get an extra turn.

---

## 13. Finishing

**Rule:** A piece finishes when it reaches progress 56. When all four pieces of a colour finish, that seat is FINISHED and gets a place based on finishing order.

**Example:** Red finishes all four pieces first → Red is 1st. Green finishes all four pieces second → Green is 2nd.

---

## 14. Removal

**Rule:** A seat can be removed (away player). Its pieces disappear from the board. If it was the current seat, play passes on.

**Ranking after removal:**
- Finishers rank first (1st, 2nd, ...).
- Active non-finishers rank above removed seats.
- Removed seats rank last, later-removed above earlier-removed.
- If every seat is removed, the outcome is CANCELLED.

**Example:** Seat 1 finishes (1st). Seat 2 is removed. Seat 3 is removed (after seat 2). Ranking: 1st = seat 1, 2nd = seat 3 (later removal), 3rd = seat 2 (earlier removal).

---

## 15. Automatic play (the bot)

**Rule:** Every game ships a bot (`GameBot` in `games/base`). Royal Ludo's
is `RoyalLudoBot`; it plays CPU seats, a seat whose time runs out, and a
player who turned Autoplay on. It only ever picks a legal piece.

**Scoring (each legal piece; highest wins):**
1. Finish a piece: +1000.
2. Capture a lone opponent: +800 + the victim's progress.
3. Leave base: +600.
4. Enter the home lane: +350.
5. Land on a safe square or form a protected pair: +400.
6. Land where an opponent can hit with one roll (unsafe, alone): -300.
7. Move a piece that is currently under threat to safety: +250.
8. Plus the new progress (prefer the piece that gets furthest).

**Tie-break:** Lowest piece number.

**Example:** Red can finish a piece (1000+) or capture (800 + 30). The bot finishes.

---

## 16. Match end

**Rule:** The match ends when one active seat remains. The sole remaining player ranks above removed players. If everyone is removed, the match is cancelled.

**Example:** 4 players. Red finishes (1st). Green finishes (2nd). Blue is removed. Yellow is removed (after blue). Final ranking: 1st = Red, 2nd = Green, 3rd = Yellow (later removal), 4th = Blue (earlier removal).

---

## Assumptions confirmed

| # | Assumption |
|---|---|
| a | A 6 with no legal move passes the turn without an extra roll. |
| b | With exactly one legal piece the player still chooses it (the app may offer one-tap confirm). |
| c | The consecutive-sixes counter counts rolls by the same seat in a row and resets when the turn passes to another seat. |
| d | Captures happen only on the shared track, never in the home column. |
| e | A lander captures every lone opposing piece on a non-safe square, once, with a single extra turn. |
| f | A protected group of two or more same-colour pieces is never captured. |
| g | When a group drops to one piece and an opposing piece is on that square, the remaining piece goes to base with no extra turn for the opponent. |
