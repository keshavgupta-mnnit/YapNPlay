package com.kglabs28.yapnplay.appengine.core.common

import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRuleOption
import com.kglabs28.yapnplay.appengine.protocol.BlockedUserSummary
import com.kglabs28.yapnplay.appengine.protocol.FriendRequestSummary
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
            username = "keshav_28",
            userCode = "K7Q2M9",
            avatarId = "a03",
            gamesPlayed = 128,
            gamesWon = 47,
        )

        val friends = Friends(
            friends = listOf(
                person("user-neha", "Neha", online = true, "neha.s", "N4HX2P", "a07"),
                person("user-arjun", "Arjun", online = true, "arjun_r", "A9RJ3K", "a02"),
                person("user-riya", "Riya", online = true, "riya", "R2YQ8M", "a10"),
                person("user-karan", "Karan", online = true, "karan.k", "K3RN7W", "a05"),
                person("user-sneha", "Sneha", online = true, "sneha_11", "S8NH4T", "a11"),
                person("user-vikram", "Vikram", online = true, "vikram", "V6KR2M", "a01"),
                person("user-rohan", "Rohan", online = true, "rohan.c", "R7HN5C", "a04"),
                person("user-meera", "Meera", online = false, "meera", "M3ER9D", "a08"),
                person("user-dev", "Dev", online = false, "dev_x", "D5VX2Q", "a12"),
            ),
        )

        /** Friend requests waiting for the user. */
        val requests = listOf(
            FriendRequestSummary("req-aarav", person("user-aarav", "Aarav", online = true, "aarav_k", "A2RV7K", "a06")),
            FriendRequestSummary("req-ishita", person("user-ishita", "Ishita", online = false, "ishita", "I5ST8H", "a13")),
        )

        /** Other people the user can find by ID (not friends yet). Try K8BR4M or T3NY6Q. */
        val directory = listOf(
            person("user-kabir", "Kabir", online = true, "kabir.m", "K8BR4M", "a02"),
            person("user-tanya", "Tanya", online = false, "tanya_t", "T3NY6Q", "a10"),
        )

        /** People the user already blocked (10 and 6 Oct 2026). */
        val blocked = listOf(
            BlockedUserSummary("user-rohit-s", "Rohit Singh", "rohit", "a14", blockedAt = 1_791_590_400_000),
            BlockedUserSummary("user-deepak", "Deepak Verma", "deepak", "a16", blockedAt = 1_791_244_800_000),
        )

        /** The user's avatar as last saved on the Profile screen (fake servers share it). */
        var myAvatarId: String = me.avatarId

        /** Anyone's avatar by user id (the user, friends, requests, people found by ID); "" if unknown. */
        fun avatarOf(userId: String): String =
            if (userId == me.userId) myAvatarId
            else (friends.friends + requests.map { it.from } + directory).firstOrNull { it.userId == userId }?.avatarId.orEmpty()

        private fun person(userId: String, name: String, online: Boolean, username: String, code: String, avatarId: String) =
            FriendSummary(userId = userId, displayName = name, online = online, username = username, userCode = code, avatarId = avatarId)
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
        /** This room already has a match running without the user (they can watch, and get offered a seat). */
        const val LIVE_MATCH_ROOM = "room-family-fun"
        const val LIVE_MATCH_ID = "match-room-family-fun-live"
        /** After the game changes (which clears ready), the usual players press ready again, one per step. */
        const val READY_AGAIN_STEP_MS = 1_500L
        /** In a room someone else leads, they start the match this long after the user is ready. */
        const val LEADER_START_AFTER_MS = 2_000L
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
                    matchInProgress = if (roomId == LIVE_MATCH_ROOM) LIVE_MATCH_ID else null,
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
                    avatarId = Profile.avatarOf(id),
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

        /**
         * Fake search window. TEMPORARY 0 while the Ludo table is being
         * tested: Quick Play opens the match at once (bots fill the seats).
         * Put back to 10 when testing is done.
         */
        const val SEARCH_SECONDS = 0L

        /** Other humans "arrive" this long after the search starts. */
        val otherPlayersJoinAfterMs = listOf(2_000L, 5_000L)

        /** How often the fake server re-sends QueueStatus. */
        const val STATUS_INTERVAL_MS = 1_000L
    }

    /** Fake Royal Ludo table (see FakeLudoMatchServer). */
    object LudoMatch {
        /** Opponents the fake server plays: user id, name, level, mic on. */
        val opponents = listOf(
            Opponent("user-neha", "Neha", 18, micOn = true, onlineMinutes = 42),
            Opponent("user-arjun", "Arjun", 23, micOn = true, onlineMinutes = 25),
            Opponent("user-riya", "Riya", 7, micOn = false, onlineMinutes = 18),
        )

        /** Room spectators (room matches only; Quick Play has none). */
        val spectators = listOf(
            Opponent("user-karan", "Karan", 9, micOn = true, onlineMinutes = 12),
            Opponent("user-sneha", "Sneha", 11, micOn = false, onlineMinutes = 9),
            Opponent("user-vikram", "Vikram", 4, micOn = true, onlineMinutes = 5),
        )
        const val QUICK_PLAY_TITLE = "Quick Play"
        /** Room leader when the user does not lead the room, and how long the user has been online. */
        const val OTHER_LEADER_ID = "user-neha"
        const val MY_ONLINE_MINUTES = 30L

        /** Pacing so the table feels human and animations finish first. */
        const val THINK_MS = 900L
        const val AFTER_ROLL_MS = 2_100L
        const val PER_STEP_MS = 230L
        const val AFTER_MOVE_MS = 500L
        const val AFTER_CAPTURE_MS = 800L

        /** Away handling (the real values live in the server's game config). */
        const val PRESENCE_TICK_MS = 1_000L
        /** Away this long, or this many turns missed, and the seat is offered. */
        const val OPEN_SEAT_AFTER_MS = 30_000L
        const val OPEN_SEAT_AFTER_MISSED_TURNS = 2
        /** How long an open seat waits for someone to take it. */
        const val SEAT_CLAIM_MS = 30_000L
        /**
         * Demo, this far into a match: in a room the last opponent leaves and
         * offers their seat to a spectator (who declines after
         * [NOMINEE_ANSWER_MS], so the leader picks; a fake leader picks after
         * [LEADER_ANSWER_MS], and that pick accepts). In Quick Play they go
         * away and the bot takes over.
         */
        const val DEMO_AFTER_MS = 45_000L
        const val NOMINEE_ANSWER_MS = 6_000L
        /** Watching the live room match: a player leaves and offers their seat to the user this far in. */
        const val WATCH_DEMO_AFTER_MS = 15_000L
        /** Joining the live room match lands at a random stage: these many plays in (start, middle, near the end). */
        val WATCH_STAGE_PLAYS = listOf(0, 120, 260)
        /** Rough real time per play, so the match clock fits the stage. */
        const val WATCH_SECONDS_PER_PLAY = 2L
        const val LEADER_ANSWER_MS = 4_000L
        /** A spectator takes a seat open to all this long after it opens. */
        const val STAND_IN_AFTER_MS = 10_000L

        data class Opponent(val userId: String, val name: String, val level: Int, val micOn: Boolean, val onlineMinutes: Long = 0)
    }

    /** Fake City Bazaar table (see FakeCityBazaarServer). */
    object CityBazaar {
        /** Opponents in seat order after the user (Quick Play uses the first three). */
        val opponents = listOf(
            LudoMatch.Opponent("user-neha", "Neha", 18, micOn = true),
            LudoMatch.Opponent("user-arjun", "Arjun", 23, micOn = true),
            LudoMatch.Opponent("user-riya", "Riya", 7, micOn = false),
            LudoMatch.Opponent("user-karan", "Karan", 9, micOn = true),
            LudoMatch.Opponent("user-sneha", "Sneha", 11, micOn = true),
        )
        /** Watching a room match. */
        val spectators = listOf(
            LudoMatch.Opponent("user-vikram", "Vikram", 4, micOn = true),
        )
        const val QUICK_PLAY_TITLE = "Quick Play"
        const val QUICK_PLAY_SEATS = 4
        const val ROOM_SEATS = 6

        /** Pacing so the table feels human and animations finish first. */
        const val THINK_MS = 1_000L
        const val AFTER_ROLL_MS = 2_100L
        const val PER_STEP_MS = 200L
        const val AFTER_MOVE_MS = 500L
        const val CARD_MS = 1_800L
        const val MONEY_MS = 700L
        /** Auction timers and CPU bidding are checked this often. */
        const val TICK_MS = 250L
        /** CPU players wait at least this long after a bid, then bid with this chance per check. */
        const val CPU_BID_DELAY_MS = 1_200L
        const val CPU_BID_CHANCE = 0.35
    }

    /** Fake moderation service (see FakeModerationServer). */
    object Moderation {
        /** How long the others take to vote on the user's report. */
        const val VOTING_MS = 6_000L
        /** Chance each fake voter agrees; of the rest, this share never answers. */
        const val AGREE_CHANCE = 0.6
        const val NO_RESPONSE_SHARE = 0.4
        /** Time the user has to answer a prompt. */
        const val VOTE_WINDOW_MS = 15_000L
        const val PENALTY_POINTS = 50
        const val BLOCK_MINUTES = 10
        const val AD_LENGTH_MS = 2_500L
    }
}
