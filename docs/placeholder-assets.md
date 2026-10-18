# Placeholder assets — art to provide

Every entry below is a single-letter glyph standing in for real art.
Replace the glyph with an Icon/Avatar composable; delete the row here.

## Avatars — done
The 18 preset avatars (`a01`…`a18`, `androidMain/res/drawable/ic_avatar_NN.webp`)
are real art and show everywhere a user's avatar is drawn: Home top bar,
Profile, friend rows, friend and request cards, the lobby member list
and the game player cards. The spectator tray still uses the person glyph.

## HomeScreen (`ui/screens/home/`)
| # | File / Component | Meaning | Real art needed |
|---|---|---|---|
| 1 | `HomeUserProfilePill.kt` | User avatar | Real user avatar profile image |
| 2 | `HomeCenterLogo.kt` | YapNPlay header logo | Vector logo asset with mascot |
| 3 | `GameZoneGraphic.kt` | 3D Gaming Orbit | 3D Ludo board, UNO cards, playing cards & 3D dice model |
| 4 | `SocialNetworkOrbitGraphic.kt` | Social Friend Avatars | Real friend avatar images & +Invite Friends node |
| 5 | `HomeHeaderBar.kt` | Notification & Settings | Vector Bell & Gear icons |

## GameZoneScreen (`ui/screens/gamezone/`)
| # | File / Component | Meaning | Real art needed |
|---|---|---|---|
| 1 | `GameArt.kt` (`royal-ludo`) | Royal Ludo carousel art | Dedicated Royal Ludo art (uses the home game-zone art today) |
| 2 | `GameArt.kt` (other keys) | Card Clash, Island Quest, Block Merge, Carrom Strike, Doodle Dash, Quiz Quest, Word Wars | Art per game when each game is built |

## RoomsScreen (`ui/screens/rooms/`)
| # | File / Component | Meaning | Real art needed |
|---|---|---|---|
| 1 | `CreateRoomGraphic.kt` | Create-room hero art | Arcade machine with crown, dice and stars |
| 3 | `RoomListItem.kt` | Room badges | Per-room icon art (group, dice, family, gamepad) |

## GalleryScreen (`ui/screens/gallery/`)
| # | Glyph | Meaning | Real art needed |
|---|---|---|---|
| 1 | `M` (toggle on) | Microphone on | Mic-on icon, cyan |
| 2 | `S` (toggle off) | Speaker off | Speaker-off icon, dim |
| 3 | `M` (large toggle) | Big mic toggle | Same mic icon, large |
| 4 | `?` (icon button) | Settings placeholder | Gear icon |
| 5 | `V` (speaking ring demo) | Sample avatar | Sample avatar image |

## Coming with real screens (no placeholders yet)
- Home: profile avatar, zone card art, bottom-bar icons (settings, mic, play)
- Lobby: 4 seat avatars, settings icons
- Party Play: room/chat avatars, arcade illustration
- Royal Ludo: 4 player avatars, crown/level badges

Rule: glyphs never ship to users. When a screen goes real, its art lands
in the same change, and this file loses the row.

## Game sounds (appengine/src/androidMain/res/raw)

Synthesised placeholders — replace with recorded audio of the same names:

| File | Used for |
|---|---|
| `music_loop.wav` | Quiet background music on the match screen (loops) |
| `dice_roll.wav` | Every dice roll |
| `piece_hop.wav` | Each square a piece hops |
| `piece_capture.wav` | A capture, or a turn undone by a third six |
| `winner.wav` | A player bringing all pieces home |

## City Bazaar

- **Game art:** `ic_game_city_bazaar.png` (androidMain drawable), shown
  wherever games are picked (carousel, grid, lobby). Royal Ludo uses
  `ic_game_royal_ludo.png` the same way.
- **Board:** drawn in code (tiles, group headers, owner borders, houses,
  hotel, Shagun Chest and Luck decks, tokens). No image assets.
- **Sounds:** reuses Royal Ludo's dice, hop, capture and win sounds. A
  cash register and a card flip would be good additions.

