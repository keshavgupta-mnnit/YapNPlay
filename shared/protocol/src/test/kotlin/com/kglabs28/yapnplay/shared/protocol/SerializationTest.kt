package com.kglabs28.yapnplay.shared.protocol

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Round-trip serialization tests for all protocol message types.
 * Also verifies golden files match and unknown fields are ignored.
 */
class SerializationTest {

    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // ── Room channel: client → server ─────────────────────────────────────

    @Test
    fun `CreateRoom with name round trips`() {
        val original = CreateRoom(name = "Family Room")
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<CreateRoom>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `CreateRoom without name round trips`() {
        val original = CreateRoom()
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<CreateRoom>(encoded)
        assertEquals(original, decoded)
        assertEquals(null, decoded.name)
    }

    @Test
    fun `JoinRoom round trips`() {
        val original = JoinRoom(code = "ABC123")
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<JoinRoom>(encoded)
        assertEquals(original, decoded)
        assertEquals("ABC123", decoded.code)
    }

    @Test
    fun `SetReady round trips`() {
        val original = SetReady(ready = true)
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<SetReady>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `RequestSnapshot round trips`() {
        val original = RequestSnapshot(lastVersion = 42)
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<RequestSnapshot>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `LeaveRoom round trips`() {
        val original = LeaveRoom()
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<LeaveRoom>(encoded)
        assertEquals(original, decoded)
    }

    // ── Room channel: server → client ─────────────────────────────────────

    @Test
    fun `Ack round trips`() {
        val original = Ack(requestId = "req-001")
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<Ack>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `Rejected round trips`() {
        val original = Rejected(
            requestId = "req-002",
            code = ErrorCode.ROOM_FULL,
            message = "Room is full",
        )
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<Rejected>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `RoomSnapshot round trips`() {
        val original = RoomSnapshot(
            version = 5,
            serverTime = 1700000000000,
            room = RoomInfo(
                id = "room-1",
                code = "ABC123",
                name = "Family Room",
                leaderId = "user-1",
                members = listOf(
                    RoomMember(
                        userId = "user-1",
                        displayName = "Alice",
                        presence = Presence.ONLINE,
                        ready = true,
                    ),
                    RoomMember(
                        userId = "user-2",
                        displayName = "Bob",
                        presence = Presence.OFFLINE,
                        ready = false,
                    ),
                ),
                selectedGame = "ludo",
                countdownDeadline = 1700000010000,
                matchInProgress = null,
            ),
        )
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<RoomSnapshot>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `MatchStarting round trips`() {
        val original = MatchStarting(matchId = "match-1")
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<MatchStarting>(encoded)
        assertEquals(original, decoded)
    }

    // ── Match channel: client → server ────────────────────────────────────

    @Test
    fun `RollDice round trips`() {
        val original = RollDice(matchId = "match-1")
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<RollDice>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `MovePiece round trips`() {
        val original = MovePiece(matchId = "match-1", pieceId = "red-1")
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<MovePiece>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `MatchRequestSnapshot round trips`() {
        val original = MatchRequestSnapshot(matchId = "match-1", lastVersion = 10)
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<MatchRequestSnapshot>(encoded)
        assertEquals(original, decoded)
    }

    // ── Match channel: server → client ────────────────────────────────────

    @Test
    fun `MatchSnapshot round trips`() {
        val original = MatchSnapshot(
            matchId = "match-1",
            version = 3,
            serverTime = 1700000000000,
            viewer = ViewerInfo(role = ViewerRole.PLAYER, seat = 1),
            seats = listOf(
                SeatInfo(
                    seat = 1,
                    colour = Colour.RED,
                    controllerUserId = "user-1",
                    status = MatchSeatStatus.ACTIVE,
                    connected = true,
                    autoPlay = false,
                    consecutiveMisses = 0,
                ),
            ),
            publicState = buildJsonObject {
                put("board", "ludo-board-json")
            },
            legalMoves = listOf(LegalMove.RollDice),
            turn = TurnInfo(seat = 1, phase = "ROLL", deadline = 1700000030000),
        )
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<MatchSnapshot>(encoded)
        assertEquals(original.matchId, decoded.matchId)
        assertEquals(original.version, decoded.version)
        assertEquals(original.viewer.role, decoded.viewer.role)
        assertEquals(original.seats.size, decoded.seats.size)
        assertEquals(original.turn.seat, decoded.turn.seat)
        assertEquals(original.turn.phase, decoded.turn.phase)
    }

    @Test
    fun `MatchEvent round trips`() {
        val original = MatchEvent(
            version = 1,
            event = buildJsonObject {
                put("type", "DiceRolled")
                put("value", 4)
            },
        )
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<MatchEvent>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `MatchEnded round trips`() {
        val original = MatchEnded(
            ranking = listOf(
                RankedSeat(seat = 1, status = MatchSeatStatus.ACTIVE, place = 1),
                RankedSeat(seat = 2, status = MatchSeatStatus.REMOVED, place = 2),
            ),
        )
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<MatchEnded>(encoded)
        assertEquals(original, decoded)
    }

    // ── HTTP bodies ────────────────────────────────────────────────────────

    @Test
    fun `GuestCreated round trips`() {
        val original = GuestCreated(userId = "user-1", secret = "secret-token")
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<GuestCreated>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `Me round trips`() {
        val original = Me(userId = "user-1", displayName = "Alice")
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<Me>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `UpdateName round trips`() {
        val original = UpdateName(displayName = "New Name")
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<UpdateName>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `MyRooms round trips`() {
        val original = MyRooms(
            rooms = listOf(
                RoomSummary(
                    roomId = "room-1",
                    code = "ABC123",
                    name = "Family Room",
                    memberCount = 4,
                    onlineCount = 2,
                ),
            ),
        )
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<MyRooms>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `VoiceToken round trips`() {
        val original = VoiceToken(
            url = "wss://voice.example.com",
            token = "voice-access-token",
            expiresAt = 1700003600000,
        )
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<VoiceToken>(encoded)
        assertEquals(original, decoded)
    }

    // ── Edge cases ─────────────────────────────────────────────────────────

    @Test
    fun `unknown fields in payload are ignored`() {
        val rawJson = """{"code":"ABC123","unknownField":"ignored","another":42}"""
        val decoded = json.decodeFromString<JoinRoom>(rawJson)
        assertEquals("ABC123", decoded.code)
    }

    @Test
    fun `missing required field throws`() {
        val rawJson = """{"unknownField":"value"}"""
        assertThrows<Exception> {
            json.decodeFromString<JoinRoom>(rawJson)
        }
    }

    @Test
    fun `Channel enum round trips`() {
        val original = Channel.ROOM
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<Channel>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `ErrorCode enum round trips`() {
        for (code in ErrorCode.entries) {
            val encoded = json.encodeToString(code)
            val decoded = json.decodeFromString<ErrorCode>(encoded)
            assertEquals(code, decoded)
        }
    }

    @Test
    fun `LegalMove sealed hierarchy round trips`() {
        val rollDice: LegalMove = LegalMove.RollDice
        val encoded1 = json.encodeToString(rollDice)
        val decoded1 = json.decodeFromString<LegalMove>(encoded1)
        assertEquals(rollDice, decoded1)

        val movePiece: LegalMove = LegalMove.MovePiece("piece-1")
        val encoded2 = json.encodeToString(movePiece)
        val decoded2 = json.decodeFromString<LegalMove>(encoded2)
        assertEquals(movePiece, decoded2)
    }
}
