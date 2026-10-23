# Project Specification & Full Implementation Guide: "MONO" & "MONO: Complete Chaos"

## 1. Executive Summary & Relationship Between the Two Games
We are building two multiplayer shedding card games inside our 16:9 landscape mobile social gaming platform, **YapNPlay**:
1. **MONO** (Standard Mode)
2. **MONO: Complete Chaos** (Brutal / No-Mercy Mode)

**Core Architectural Principle:** 
`MONO` and `MONO: Complete Chaos` are selectable as two distinct game modes/variants, but they share the **exact same core engine, 8-player table UI, turn lifecycle, and programmatic vector card rendering system**. `MONO` is the classic tactical shedding game, whereas `MONO: Complete Chaos` injects brutal, high-volatility rules on top of that foundation (168-card deck, penalty stacking up to +10, 25-card knockout elimination, 7-0 hand swapping/passing, and Wild Roulette).

---

## 2. Brand Terminology & Visual Design System

### 2.1 Copyright-Safe Terminology Mapping
Never use trademarked terms like "UNO", "No Mercy" in the UI or codebase displays:
- **"UNO!" Callout** $\rightarrow$ **`MONO!`**
- **"Wild" Card Category** $\rightarrow$ **`WILD`** (e.g., *Wild Card*, *Wild +4*, *Wild Reverse +4*, *Wild +6*, *Wild +10*, *Wild Roulette*).
- **Color Palette (Dark Jewel-Tone Glass — No Bright Neon Backgrounds):**
  - **Outer Card Base:** Smoked Obsidian Black Glass (`#101218` at 90% opacity) with a metallic bevel edge.
  - **Dark Ruby Red:** `#7A1224` (Fill) / `#D92B4B` (Inner Octagon Rim & Text Glow)
  - **Dark Forest Green:** `#0F5232` (Fill) / `#25B86E` (Inner Octagon Rim & Text Glow)
  - **Dark Cobalt Blue:** `#103466` (Fill) / `#2E77D0` (Inner Octagon Rim & Text Glow)
  - **Dark Amber Gold:** `#6E4B0E` (Fill) / `#E09F24` (Inner Octagon Rim & Text Glow)

### 2.2 Card Front & Back Geometry (100% Vector / Code-Renderable — NO Fire/Sparks)
All cards are vertical portrait (`2:3` aspect ratio) and must be rendered natively on Android/mobile using clean vector shapes, gradients, and crisp typography—**do not use particle fire, sparks, or raster textures**.
1. **Front Outer Frame & Top Notch:** Every card front has an outer colored border box that wraps around the perimeter and dips inward at the top-center to form a clean trapezoid notch containing the label `MONO`.
2. **Strictly 2 Corner Indices:** Card values/icons must appear in **ONLY TWO CORNERS: Top-Left and Bottom-Right** (inverted in bottom-right). Keep Top-Right and Bottom-Left corners completely empty!
3. **Centerpiece Shape:** A bold **Flat-Top Geometric Octagon** sits in the center of the card housing the primary number or action icon.
4. **Multi-Color (`WILD`) Card Fronts (NO Ribbons on Front!):** 
   - Do NOT use diagonal ribbons on the front of multi-color cards.
   - Instead, the inner glass background of all `WILD` cards (`Wild`, `Wild +4`, `Wild Reverse +4`, `Wild +6`, `Wild +10`, `Wild Roulette`) uses a **clean 2x2 4-Quadrant Split** of the 4 dark colors (Top-Left: Dark Ruby Red, Top-Right: Dark Forest Green, Bottom-Left: Dark Cobalt Blue, Bottom-Right: Dark Amber Gold) behind the central flat-top octagon.
5. **Official Card Back Design (Prismatic Ribbon Back):**
   - Only the **Back of the Card** uses the 4 converging diagonal dark metallic ribbons (`X` pattern) behind the center flat-top octagon.
   - Across the center octagon sits the bold 3D metallic silver **`MONO`** logo, with the letters `M` and `O` breaking wide outside the left and right edges of the octagon ring. (In *Complete Chaos*, a dark crimson sub-plate beneath `MONO` reads `COMPLETE CHAOS`).

---

## 3. 16:9 Landscape Screen Layout (2 to 8 Players)

The UI must remain clean and uncrowded on mobile screens by following our established **YapNPlay** HUD architecture:
1. **Top Bar:**
   - **Top-Left:** Frosted-glass pill showing Game Title (`MONO` or `MONO: Complete Chaos`) and room name.
   - **Top-Center:** Wide frosted-glass **Spectator Bar** showing spectator avatars and count.
   - **Top-Right:** Turn Timer pill (`0:15`), Settings Gear icon, and Red Exit button.
2. **Perimeter Opponent Seating (Up to 7 Opponents):**
   - Arranged in a U-shape around the center arena (3 pods on the left column, 2 at top-center, 2 on the right column).
   - Each opponent sits in a compact horizontal frosted-glass pod (Avatar + Level badge + Name + Live Voice Waveform + Mute toggle).
   - **Attached Card Fan:** Directly docked to the right edge of each opponent's pod is a miniature fan of face-down cards + a high-contrast numeric card count badge (e.g., `4`). If an opponent has `1` card left and called `MONO!`, a glowing `MONO!` badge pulses on their pod.
3. **Center Play Arena:**
   - Compact dark matte oval/rounded-rectangle arena in the middle of the screen.
   - Houses **only two large elements**: the **Face-Down Draw Deck** (left) and the **Face-Up Discard Pile** (right).
   - Surrounding the center piles is a **Directional Light Ring** that glows in the **Active Table Color** (Red, Green, Blue, or Gold) and animates with directional arrows showing whether turn order is **Clockwise** or **Counter-Clockwise**.
   - In *Complete Chaos*, when draw cards are stacked, a bold **Penalty Stack Counter** (`STACK: +12 CARDS`) floats above the Discard Pile.
4. **Bottom Local Player Action Row (`Left -> Center -> Right`):**
   - **Far Left:** Local player compact pod (Avatar, Name, Mic toggle, Autoplay toggle).
   - **Mid-Left:** Large tactile **`MONO!`** callout button.
   - **Center:** Local player's fanned-out hand of vertical cards (horizontally scrollable if holding 10+ cards, with playable cards slightly elevated/highlighted during the player's turn).
   - **Far Right:** Dedicated **`DRAW CARD`** button.

---

## 4. Game Setup, Dealing & Core Turn Rules (Shared by Both Modes)

### 4.1 Match Initialization & Dealing
- **Player Count:** 2 to 8 players.
- **Starting Hand Size:** Every player is dealt **7 cards** face-down at the start of the match.
- **Starting Discard Card:**
  - Flip 1 card from the Draw Deck to start the Discard Pile.
  - If the first flipped card is a Multi-Color (`WILD`) penalty card, reshuffle it into the deck and flip another card until a standard colored card appears.
- **Turn Timer:** 15 seconds per turn. If the timer expires (or `Autoplay` is ON), the system automatically plays a valid card or draws from the deck.

### 4.2 Core Turn Lifecycle
On their turn, a player must either:
1. **Play 1 matching card** from their hand onto the Discard Pile if it matches the top card by **Color**, **Number**, or **Symbol/Action**—OR play any **Multi-Color (`WILD`) card** at any time.
2. **Tap `DRAW CARD`** if they have no playable card (or choose not to play).
   - *Deck Reshuffle Rule:* If the Draw Deck runs out of cards, keep the top card of the Discard Pile in place, shuffle all other discarded cards face-down to form a new Draw Deck, and continue.

### 4.3 The `"MONO!"` Callout Rule & Penalty
- When a player has **2 cards** in their hand and plays 1 card (leaving them with **1 final card**), the **`MONO!`** button activates for a **2.5-second window**.
- **Success:** If the player taps **`MONO!`** within the window, their pod displays a protected `MONO!` shield badge.
- **Failure (Penalty):** If they fail to tap **`MONO!`** before the window closes (or before another player taps a "Catch" prompt), they are automatically penalized:
  - In **Standard MONO:** Auto-draw **+2 penalty cards**.
  - In **MONO: Complete Chaos:** Auto-draw **+4 penalty cards**.

---

## 5. Game 1 Specifics: Standard "MONO" (108-Card Deck & Rules)

### 5.1 Standard MONO Deck Composition (108 Cards Total)
| Card Type | Colors | Distribution | Total Cards |
| :--- | :--- | :--- | :--- |
| **Number `0`** | Red, Green, Blue, Gold | 1 per color | 4 |
| **Numbers `1` to `9`** | Red, Green, Blue, Gold | 2 of each number per color | 72 |
| **Reverse (`⇄`)** | Red, Green, Blue, Gold | 2 per color | 8 |
| **Skip / Block (`⊘`)** | Red, Green, Blue, Gold | 2 per color | 8 |
| **Draw Two (`+2`)** | Red, Green, Blue, Gold | 2 per color | 8 |
| **Wild (Color Wild)** | 4-Color Quadrant Front | 4 total | 4 |
| **Wild +4 (Wild Draw 4)**| 4-Color Quadrant Front | 4 total | 4 |

### 5.2 Standard MONO Rules
1. **Numbers `0`–`9`:** Standard numeric cards. (**No special effect on `0` or `7` in Standard MONO**).
2. **Reverse (`⇄`):** Flips play direction between Clockwise and Counter-Clockwise. *(In a 2-player match, Reverse acts identically to a Skip card, giving the player another immediate turn).*
3. **Skip (`⊘`):** The next player in turn order loses their turn.
4. **Draw Two (`+2`):** The next player draws 2 cards from the deck and loses their turn. *(No stacking in Standard MONO).*
5. **Wild (Color Wild):** Player selects the new active color via a 4-color radial modal (Red, Green, Blue, Gold). Play moves to the next player.
6. **Wild +4:** Player selects the new active color, and the next player draws 4 cards and loses their turn.
7. **Drawing When Unable to Play:** Tapping `DRAW CARD` draws **strictly 1 card**. If that drawn card is playable, the player has 3 seconds to tap and play it; otherwise, the turn passes to the next player.
8. **Winning:** The first player to play their final card (`0 cards remaining`) immediately wins the match!

---

## 6. Game 2 Specifics: "MONO: Complete Chaos" (168-Card Deck & Brutal Rules)

`MONO: Complete Chaos` uses a massive **168-card deck** and replaces standard rules with high-stakes mechanics. Note that in *Complete Chaos*, standard `Wild` and `Wild +4` are replaced by colored `+4` cards and 4 extreme `Wild` action cards.

### 6.1 Complete Chaos Deck Composition (168 Cards Total)
| Card Type | Colors | Distribution | Total Cards |
| :--- | :--- | :--- | :--- |
| **Numbers `0` to `9`** | Red, Green, Blue, Gold | 2 of each number (`0`–`9`) per color | 80 |
| **Reverse (`⇄`)** | Red, Green, Blue, Gold | 3 per color | 12 |
| **Skip (`⊘`)** | Red, Green, Blue, Gold | 3 per color | 12 |
| **Draw Two (`+2`)** | Red, Green, Blue, Gold | 2 per color | 8 |
| **Colored Draw Four (`+4`)**| Red, Green, Blue, Gold | 2 per color *(Colored, NOT Multi-Color)* | 8 |
| **Discard All (`+ALL`)** | Red, Green, Blue, Gold | 3 per color | 12 |
| **Skip Everyone (`⊘ ALL`)**| Red, Green, Blue, Gold | 2 per color | 8 |
| **Wild Reverse +4 (`⇄+4`)**| 4-Color Quadrant Front | 8 total | 8 |
| **Wild +6** | 4-Color Quadrant Front | 4 total | 4 |
| **Wild +10** | 4-Color Quadrant Front | 4 total | 4 |
| **Wild Roulette** | 4-Color Quadrant Front | 8 total | 8 |
| *(Optional Extra Number/Action Padding)* | Red, Green, Blue, Gold | 1 extra `+2` per color | 4 |

### 6.2 Exclusive Rules of "MONO: Complete Chaos"

1. **Rule of `7` (Mandatory Hand Swap) & Rule of `0` (Mandatory Hand Pass):**
   - **Playing a `7` (Any Color):** Whenever a player plays a `7`, a modal immediately pops up prompting them to **select 1 active opponent**. The player and the chosen opponent **swap their entire hands of cards**!
   - **Playing a `0` (Any Color):** Whenever a player plays a `0`, **every active player at the table automatically passes their entire hand** of cards to the next player in the current direction of play!
   - *(Note: If a `7` or `0` is played as a player's very last card, the game ends immediately in their victory before any hand swap/pass occurs).*

2. **Ruthless Penalty Stacking (`+2`, `+4`, `+6`, `+10`):**
   - When any Draw card (`+2`, Colored `+4`, `Wild Reverse +4`, `Wild +6`, or `Wild +10`) is played, the next player does NOT have to draw immediately if they hold a Draw card of **equal or greater penalty value**.
   - **Valid Stack Chain:**
     - On a `+2`: Can stack `+2`, `+4`, `Wild Reverse +4`, `Wild +6`, or `Wild +10`.
     - On a `+4` or `Wild Reverse +4`: Can stack `+4`, `Wild Reverse +4`, `Wild +6`, or `Wild +10` *(cannot stack a `+2`)*.
     - On a `Wild +6`: Can stack `Wild +6` or `Wild +10` *(cannot stack `+2` or `+4`)*.
     - On a `Wild +10`: Can ONLY stack another `Wild +10`.
   - Each stacked card adds its value to the central **Active Stack Counter** (e.g., `+4` $\rightarrow$ `+6` $\rightarrow$ `+10` = **20 Cards**). The first player in sequence who cannot stack must draw the **entire accumulated total** and loses their turn!

3. **The 25-Card "Mercy / Chaos Knockout" Rule:**
   - If any player's hand count reaches **25 or more cards** at any moment (whether from drawing, penalty stacks, or Wild Roulette), they are **INSTANTLY ELIMINATED (Knocked Out)**!
   - *(Note: Even if a player holds 26 cards via a `7`/`0` hand swap, they are immediately knocked out).*
   - When knocked out, their player pod dims with an `ELIMINATED` badge, and their cards are placed at the bottom of the Draw Deck.

4. **Draw Until You Can Play (Relentless Draw):**
   - Unlike Standard MONO (where you draw only 1 card), if a player has no playable card on their turn and taps `DRAW CARD`, the game **automatically draws cards one by one** (0.25s per card animation) until they draw a valid playable card—OR until their hand hits **25 cards** and they are Knocked Out!

5. **New Colored Action Cards:**
   - **Colored Draw Four (`+4`):** Matches by color or another `+4`. Adds `+4` to the draw stack.
   - **Discard All (`+ALL` / Downward Arrow Icon):** When played, **every other card in the player's hand of that exact same color** is automatically discarded underneath it in a single turn!
   - **Skip Everyone (`⊘ ALL`):** Skips **all other players at the table** and immediately grants the player who played it **another turn right away**.

6. **New Multi-Color (`WILD`) Cards:**
   - **Wild Reverse +4 (`⇄ +4`):** The player picks the new active color, the direction of play reverses, AND the new next player in that reversed direction must face the `+4` penalty (or stack a `+4`/`+6`/`+10` on top). *In a 2-player match, playing `Wild Reverse +4` reverses the penalty right back onto yourself, meaning YOU must stack another `+4`/`+6`/`+10` or draw the cards!*
   - **Wild +6:** Player picks the active color; next player faces a `+6` stackable penalty.
   - **Wild +10:** Player picks the active color; next player faces a `+10` stackable penalty.
   - **Wild Roulette (4-Color Wheel Spinner Icon):**
     - Player plays this card onto any color.
     - The **next player in turn order** is prompted with a 4-color modal to **choose 1 color** (Red, Green, Blue, or Gold).
     - Once chosen, the game automatically flips cards face-up one by one from the Draw Deck onto a reveal tray until a card of that **chosen color** appears *(Multi-Color `WILD` cards do not count as a match and keep the streak going!)*.
     - All flipped cards are added into that victim player's hand, the active table color becomes the color they picked, and their turn is skipped! *(Cannot be stacked).*

7. **Winning "MONO: Complete Chaos":**
   - A player wins immediately by either:
     1. Emptying their hand (`0 cards remaining`), OR
     2. Being the **last player standing** after all other opponents have been Knocked Out via the **25-Card Elimination Rule**!