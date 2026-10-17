package com.kglabs28.yapnplay.appengine.core.common

import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRuleOption
import com.kglabs28.yapnplay.appengine.protocol.FriendSummary
import com.kglabs28.yapnplay.appengine.protocol.Friends
import com.kglabs28.yapnplay.appengine.protocol.Me
import com.kglabs28.yapnplay.appengine.protocol.MyRooms
import com.kglabs28.yapnplay.appengine.protocol.OnlineStats
import com.kglabs28.yapnplay.appengine.protocol.Presence
import com.kglabs28.yapnplay.appengine.protocol.RoomInfo
import com.kglabs28.yapnplay.appengine.protocol.RoomMember
import com.kglabs28.yapnplay.appengine.protocol.JoinRoomResult
import com.kglabs28.yapnplay.appengine.protocol.JoinStatus
import com.kglabs28.yapnplay.appengine.protocol.RoomPreview
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot
import com.kglabs28.yapnplay.appengine.protocol.RoomSummary

/**
 * The single source of mock data for the whole app until the backend
 * lands. One file on purpose: every fake object lives here, sectioned
 * per feature, so nothing hides fakes in its own corner. Shapes are the
 * shared contract types, exactly what the server will send.
 */
object FakeDataProvider {

    /** How long every fake API call waits, so the UI loads like production. */
    const val NETWORK_DELAY_MS = 600L

    object Profile {
        val me = Me(
            userId = "user-keshav",
            displayName = "Keshav",
            xp = 2450,
            level = 12,
            coins = 4250,
            gems = 120,
        )

        val friends = Friends(
            friends = listOf(
                FriendSummary(userId = "user-neha", displayName = "Neha", online = true),
                FriendSummary(userId = "user-arjun", displayName = "Arjun", online = true),
                FriendSummary(userId = "user-riya", displayName = "Riya", online = true),
                FriendSummary(userId = "user-karan", displayName = "Karan", online = true),
                FriendSummary(userId = "user-sneha", displayName = "Sneha", online = true),
                FriendSummary(userId = "user-vikram", displayName = "Vikram", online = true),
                FriendSummary(userId = "user-rohan", displayName = "Rohan", online = true),
                FriendSummary(userId = "user-meera", displayName = "Meera", online = false),
                FriendSummary(userId = "user-dev", displayName = "Dev", online = false),
            ),
        )
    }

    object PlayRooms {
        val myRooms = MyRooms(
            rooms = listOf(
                RoomSummary(
                    roomId = "room-college-crew",
                    code = "482193",
                    name = "College Crew",
                    memberCount = 10,
                    onlineCount = 6,
                    isLeader = true,
                    icon = "group",
                ),
                RoomSummary(
                    roomId = "room-game-night",
                    code = "190734",
                    name = "Game Night Squad",
                    memberCount = 8,
                    onlineCount = 4,
                    icon = "dice",
                ),
                RoomSummary(
                    roomId = "room-family-fun",
                    code = "552810",
                    name = "Family Fun",
                    memberCount = 9,
                    onlineCount = 2,
                    icon = "family",
                ),
                RoomSummary(
                    roomId = "room-weekend-warriors",
                    code = "906614",
                    name = "Weekend Warriors",
                    memberCount = 5,
                    onlineCount = 1,
                    icon = "gamepad",
                ),
            ),
        )

        /** Names for rooms you're not in, picked by code so they stay stable. */
        private val OTHER_ROOM_NAMES = listOf("Chill Gamers", "Ludo Legends", "Night Owls", "Dice Squad", "Weekend Crew")

        private fun otherRoomName(code: String) = OTHER_ROOM_NAMES[code.sumOf { it.digitToIntOrNull() ?: 0 } % OTHER_ROOM_NAMES.size]

        /** Preview for a code: one of your rooms if it matches, else a made-up room. */
        fun preview(code: String) = myRooms.rooms.firstOrNull { it.code == code }
            ?.let { RoomPreview(it.roomId, it.code, it.name, Lobby.DESCRIPTION, it.memberCount, it.onlineCount, isMember = true) }
            ?: RoomPreview("room-$code", code, otherRoomName(code), Lobby.DESCRIPTION, memberCount = 3, onlineCount = 2)

        /** Your room: joined; anyone else's: the request waits for its admin. */
        fun joinResult(code: String): JoinRoomResult {
            val mine = myRooms.rooms.firstOrNull { it.code == code }
            return if (mine != null) {
                JoinRoomResult(JoinStatus.JOINED, mine.name, mine)
            } else {
                JoinRoomResult(JoinStatus.PENDING_APPROVAL, otherRoomName(code))
            }
        }
    }

    /**
     * Games that are not built yet, so the carousel looks like the real
     * catalog. Each one is replaced by its engine's registration when it
     * lands; Royal Ludo is already real and comes from the registry.
     */
    object Games {
        /** Shared toggles for placeholder games (real games declare their own). */
        val placeholderHouseRules = listOf(
            HouseRuleOption("fast_turns", "Fast Turns", "Make your move quickly!", true),
            HouseRuleOption("special_cards", "Special Cards", "Use special cards for extra fun!", true),
            HouseRuleOption("mulligans", "Mulligans", "Get a re-roll if you're stuck", false),
            HouseRuleOption("power_ups", "Power-ups", "Unlock power-up cards", true),
        )

        val placeholderFixedRules = listOf(
            "Game rules arrive with the game",
            "Turns pass clockwise",
            "Leaving mid-match hands your seat to a CPU",
        )

        val placeholders = listOf(
            GameCatalogItem(
                id = "card-clash",
                name = "Card Clash",
                taglines = listOf("Match", "Play", "Win"),
                artKey = "card-clash",
                minPlayers = 4,
                maxPlayers = 8,
                quickPlaySeats = 4,
                houseRuleOptions = placeholderHouseRules,
                fixedRules = placeholderFixedRules,
            ),
            GameCatalogItem(
                id = "island-quest",
                name = "Island Quest",
                taglines = listOf("Build", "Trade", "Conquer"),
                artKey = "island-quest",
                minPlayers = 2,
                maxPlayers = 4,
                quickPlaySeats = 4,
                houseRuleOptions = placeholderHouseRules,
                fixedRules = placeholderFixedRules,
            ),
            GameCatalogItem(
                id = "block-merge",
                name = "Block Merge",
                taglines = listOf("Combine", "Score", "Reach"),
                artKey = "block-merge",
                minPlayers = 2,
                maxPlayers = 2,
                quickPlaySeats = 2,
                houseRuleOptions = placeholderHouseRules,
                fixedRules = placeholderFixedRules,
            ),
            GameCatalogItem(
                id = "carrom-strike",
                name = "Carrom Strike",
                taglines = listOf("Aim", "Flick", "Pocket"),
                artKey = "carrom-strike",
                minPlayers = 2,
                maxPlayers = 4,
                quickPlaySeats = 4,
                houseRuleOptions = placeholderHouseRules,
                fixedRules = placeholderFixedRules,
            ),
            GameCatalogItem(
                id = "doodle-dash",
                name = "Doodle Dash",
                taglines = listOf("Draw", "Guess", "Laugh"),
                artKey = "doodle-dash",
                minPlayers = 2,
                maxPlayers = 8,
                quickPlaySeats = 6,
                houseRuleOptions = placeholderHouseRules,
                fixedRules = placeholderFixedRules,
            ),
            GameCatalogItem(
                id = "quiz-quest",
                name = "Quiz Quest",
                taglines = listOf("Think", "Answer", "Win"),
                artKey = "quiz-quest",
                minPlayers = 2,
                maxPlayers = 6,
                quickPlaySeats = 4,
                houseRuleOptions = placeholderHouseRules,
                fixedRules = placeholderFixedRules,
            ),
            GameCatalogItem(
                id = "word-wars",
                name = "Word Wars",
                taglines = listOf("Spell", "Stack", "Score"),
                artKey = "word-wars",
                minPlayers = 2,
                maxPlayers = 4,
                quickPlaySeats = 4,
                houseRuleOptions = placeholderHouseRules,
                fixedRules = placeholderFixedRules,
            ),
        )
    }

    /**
     * Fake room lobby (see FakeLobbyServer). Rooms where you are the
     * leader start in "choose a game"; the others start with Royal Ludo
     * picked by Neha, so both lobby views can be tried.
     */
    object Lobby {
        const val SPEAKER_ROTATION_MS = 1_500L
        const val DESCRIPTION = "Good vibes • Great games • Better together"
        private const val OTHER_LEADER = "user-neha"
        private const val OTHER_ADMIN = "user-arjun"
        private const val PRESET_GAME = "royal-ludo"

        fun snapshot(roomId: String, now: Long): RoomSnapshot {
            val summary = PlayRooms.myRooms.rooms.firstOrNull { it.roomId == roomId }
            val iLead = summary?.isLeader ?: true
            val myId = Profile.me.userId
            return RoomSnapshot(
                version = 1,
                serverTime = now,
                room = RoomInfo(
                    id = roomId,
                    code = summary?.code ?: "000000",
                    name = summary?.name ?: "Room",
                    leaderId = if (iLead) myId else OTHER_LEADER,
                    adminId = if (iLead) myId else OTHER_ADMIN,
                    description = DESCRIPTION,
                    icon = summary?.icon,
                    selectedGame = if (iLead) null else PRESET_GAME,
                    members = members(myId, now),
                ),
            )
        }

        private fun members(myId: String, now: Long): List<RoomMember> {
            fun member(id: String, name: String, online: Boolean, ready: Boolean, mic: Boolean, speaker: Boolean, friend: Boolean, minutes: Long) =
                RoomMember(
                    userId = id,
                    displayName = name,
                    presence = if (online) Presence.ONLINE else Presence.OFFLINE,
                    ready = ready,
                    micOn = mic,
                    speakerOn = speaker,
                    isFriend = friend,
                    onlineSince = if (online) now - minutes * 60_000 else null,
                )
            return listOf(
                member(myId, Profile.me.displayName, true, false, false, false, false, 30),
                member("user-neha", "Neha", true, true, true, true, true, 42),
                member("user-arjun", "Arjun", true, true, true, true, true, 25),
                member("user-riya", "Riya", true, true, false, true, true, 18),
                member("user-karan", "Karan", true, false, true, true, true, 12),
                member("user-sneha", "Sneha", true, false, false, false, false, 9),
                member("user-vikram", "Vikram", true, false, true, false, true, 5),
                member("user-rohan", "Rohan", false, false, false, false, true, 0),
                member("user-meera", "Meera", false, false, false, false, false, 0),
            )
        }
    }

    /** Fake server behaviour for Quick Play (see FakeMatchmakingClient). */
    object Matchmaking {
        val onlineStats = OnlineStats(online = 1248)

        /** Fake search window (shorter than real games' 30 s so demos are quick). */
        const val SEARCH_SECONDS = 10L

        /** Other humans "arrive" this long after the search starts. */
        val otherPlayersJoinAfterMs = listOf(2_000L, 5_000L)

        /** How often the fake server re-sends QueueStatus. */
        const val STATUS_INTERVAL_MS = 1_000L
    }
}
