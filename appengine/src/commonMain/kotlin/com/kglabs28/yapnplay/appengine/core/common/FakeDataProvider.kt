package com.kglabs28.yapnplay.appengine.core.common

import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.protocol.FriendSummary
import com.kglabs28.yapnplay.appengine.protocol.Friends
import com.kglabs28.yapnplay.appengine.protocol.Me
import com.kglabs28.yapnplay.appengine.protocol.MyRooms
import com.kglabs28.yapnplay.appengine.protocol.OnlineStats
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

        /** Pretend the server accepted [code] and returned a fresh room. */
        fun joinedRoom(code: String) = RoomSummary(
            roomId = "room-$code",
            code = code,
            name = "Room $code",
            memberCount = 3,
            onlineCount = 2,
            icon = "group",
        )
    }

    /**
     * Games that are not built yet, so the carousel looks like the real
     * catalog. Each one is replaced by its engine's registration when it
     * lands; Royal Ludo is already real and comes from the registry.
     */
    object Games {
        val placeholders = listOf(
            GameCatalogItem(
                id = "card-clash",
                name = "Card Clash",
                taglines = listOf("Match", "Play", "Win"),
                artKey = "card-clash",
                minPlayers = 4,
                maxPlayers = 8,
                quickPlaySeats = 4,
            ),
            GameCatalogItem(
                id = "island-quest",
                name = "Island Quest",
                taglines = listOf("Build", "Trade", "Conquer"),
                artKey = "island-quest",
                minPlayers = 2,
                maxPlayers = 4,
                quickPlaySeats = 4,
            ),
            GameCatalogItem(
                id = "block-merge",
                name = "Block Merge",
                taglines = listOf("Combine", "Score", "Reach"),
                artKey = "block-merge",
                minPlayers = 2,
                maxPlayers = 2,
                quickPlaySeats = 2,
            ),
            GameCatalogItem(
                id = "carrom-strike",
                name = "Carrom Strike",
                taglines = listOf("Aim", "Flick", "Pocket"),
                artKey = "carrom-strike",
                minPlayers = 2,
                maxPlayers = 4,
                quickPlaySeats = 4,
            ),
            GameCatalogItem(
                id = "doodle-dash",
                name = "Doodle Dash",
                taglines = listOf("Draw", "Guess", "Laugh"),
                artKey = "doodle-dash",
                minPlayers = 2,
                maxPlayers = 8,
                quickPlaySeats = 6,
            ),
            GameCatalogItem(
                id = "quiz-quest",
                name = "Quiz Quest",
                taglines = listOf("Think", "Answer", "Win"),
                artKey = "quiz-quest",
                minPlayers = 2,
                maxPlayers = 6,
                quickPlaySeats = 4,
            ),
            GameCatalogItem(
                id = "word-wars",
                name = "Word Wars",
                taglines = listOf("Spell", "Stack", "Score"),
                artKey = "word-wars",
                minPlayers = 2,
                maxPlayers = 4,
                quickPlaySeats = 4,
            ),
        )
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
