# Placeholder assets — art to provide

Every entry below is a single-letter glyph standing in for real art.
Replace the glyph with an Icon/Avatar composable; delete the row here.

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
| 2 | `FriendAvatarItem.kt` | Friend avatars | Real friend avatar images |
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
