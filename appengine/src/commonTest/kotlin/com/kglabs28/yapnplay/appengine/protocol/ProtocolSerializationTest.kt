package com.kglabs28.yapnplay.appengine.protocol

import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import com.kglabs28.yapnplay.appengine.protocol.GuestCreated
import com.kglabs28.yapnplay.appengine.protocol.Me
import com.kglabs28.yapnplay.appengine.protocol.MyRooms
import com.kglabs28.yapnplay.appengine.protocol.RoomSummary
import com.kglabs28.yapnplay.appengine.protocol.UpdateName
import com.kglabs28.yapnplay.appengine.protocol.VoiceToken
import com.kglabs28.yapnplay.appengine.protocol.Colour
import com.kglabs28.yapnplay.appengine.games.elements.board.protocol.LegalMove
import com.kglabs28.yapnplay.appengine.games.elements.board.protocol.MovePiece as WireMovePiece
import com.kglabs28.yapnplay.appengine.games.elements.board.protocol.RollDice as WireRollDice
import com.kglabs28.yapnplay.appengine.protocol.MatchEnded
import com.kglabs28.yapnplay.appengine.protocol.MatchEvent
import com.kglabs28.yapnplay.appengine.protocol.MatchRequestSnapshot
import com.kglabs28.yapnplay.appengine.protocol.MatchSeatStatus
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot

import com.kglabs28.yapnplay.appengine.protocol.RankedSeat

import com.kglabs28.yapnplay.appengine.protocol.SeatInfo
import com.kglabs28.yapnplay.appengine.protocol.TurnInfo
import com.kglabs28.yapnplay.appengine.protocol.ViewerInfo
import com.kglabs28.yapnplay.appengine.protocol.ViewerRole
import com.kglabs28.yapnplay.appengine.protocol.Ack
import com.kglabs28.yapnplay.appengine.protocol.CancelCountdown
import com.kglabs28.yapnplay.appengine.protocol.CreateRoom
import com.kglabs28.yapnplay.appengine.protocol.ExitLobby
import com.kglabs28.yapnplay.appengine.protocol.JoinRoom
import com.kglabs28.yapnplay.appengine.protocol.LeaveRoom
import com.kglabs28.yapnplay.appengine.protocol.MatchStarting
import com.kglabs28.yapnplay.appengine.protocol.Presence
import com.kglabs28.yapnplay.appengine.protocol.Rejected
import com.kglabs28.yapnplay.appengine.protocol.RequestSnapshot
import com.kglabs28.yapnplay.appengine.protocol.RoomInfo
import com.kglabs28.yapnplay.appengine.protocol.RoomMember
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SetReady
import com.kglabs28.yapnplay.appengine.protocol.StartCountdown
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Round-trip serialization tests for all protocol message types.
 * Inline golden strings fail the build if the wire format drifts.
 */
class ProtocolSerializationTest {

    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private fun <T> assertGolden(value: T, serializer: (T) -> String, golden: String) {
        assertEquals(golden, serializer(value))
    }

    // ── Room channel: client to server ─────────────────────────────────────

    @Test
    fun `JoinRoom golden`() {
        assertGolden(JoinRoom(code = "ABC123"), { json.encodeToString(it) }, """{"code":"ABC123"}""")
    }

    @Test
    fun `CreateRoom round trips`() {
        val withName = CreateRoom(name = "Family Room")
        assertEquals(withName, json.decodeFromString<CreateRoom>(json.encodeToString(withName)))
        val withoutName = CreateRoom()
        assertEquals(withoutName, json.decodeFromString<CreateRoom>(json.encodeToString(withoutName)))
        assertEquals(null, json.decodeFromString<CreateRoom>(json.encodeToString(withoutName)).name)
    }

    @Test
    fun `SetReady golden`() {
        assertGolden(SetReady(ready = true), { json.encodeToString(it) }, """{"ready":true}""")
    }

    @Test
    fun `RequestSnapshot golden`() {
        assertGolden(RequestSnapshot(lastVersion = 42), { json.encodeToString(it) }, """{"lastVersion":42}""")
    }

    @Test
    fun `empty requests round trip`() {
        assertEquals(LeaveRoom, json.decodeFromString<LeaveRoom>(json.encodeToString(LeaveRoom)))
        assertEquals(ExitLobby, json.decodeFromString<ExitLobby>(json.encodeToString(ExitLobby)))
        assertEquals(StartCountdown, json.decodeFromString<StartCountdown>(json.encodeToString(StartCountdown)))
        assertEquals(CancelCountdown, json.decodeFromString<CancelCountdown>(json.encodeToString(CancelCountdown)))
    }

    // ── Room channel: server to client ─────────────────────────────────────

    @Test
    fun `Ack golden`() {
        assertGolden(Ack(requestId = "req-001"), { json.encodeToString(it) }, """{"requestId":"req-001"}""")
    }

    @Test
    fun `Rejected golden`() {
        val original = Rejected(requestId = "req-002", code = ErrorCode.ROOM_FULL, message = "Room is full")
        assertGolden(original, { json.encodeToString(it) }, """{"requestId":"req-002","code":"ROOM_FULL","message":"Room is full"}""")
    }

    @Test
    fun `RoomSnapshot golden`() {
        val original = RoomSnapshot(
            version = 5,
            serverTime = 1700000000000,
            room = RoomInfo(
                id = "room-1",
                code = "ABC123",
                name = "Family Room",
                leaderId = "user-1",
                members = listOf(
                    RoomMember(userId = "user-1", displayName = "Alice", presence = Presence.ONLINE, ready = true),
                    RoomMember(userId = "user-2", displayName = "Bob", presence = Presence.OFFLINE, ready = false),
                ),
                selectedGame = "royal-ludo",
                countdownDeadline = 1700000010000,
                matchInProgress = null,
            ),
        )
        val golden = """{"version":5,"serverTime":1700000000000,"room":{"id":"room-1","code":"ABC123","name":"Family Room","leaderId":"user-1","members":[{"userId":"user-1","displayName":"Alice","presence":"ONLINE","ready":true},{"userId":"user-2","displayName":"Bob","presence":"OFFLINE","ready":false}],"selectedGame":"royal-ludo","countdownDeadline":1700000010000,"matchInProgress":null}}"""
        assertGolden(original, { json.encodeToString(it) }, golden)
        assertEquals(original, json.decodeFromString<RoomSnapshot>(golden))
    }

    @Test
    fun `MatchStarting golden`() {
        assertGolden(MatchStarting(matchId = "match-1"), { json.encodeToString(it) }, """{"matchId":"match-1"}""")
    }

    // ── Match channel: client to server ────────────────────────────────────

    @Test
    fun `RollDice golden`() {
        assertGolden(WireRollDice(matchId = "match-1"), { json.encodeToString(it) }, """{"matchId":"match-1"}""")
    }

    @Test
    fun `MovePiece golden`() {
        assertGolden(WireMovePiece(matchId = "match-1", pieceId = "red-1"), { json.encodeToString(it) }, """{"matchId":"match-1","pieceId":"red-1"}""")
    }

    @Test
    fun `MatchRequestSnapshot round trips`() {
        val original = MatchRequestSnapshot(matchId = "match-1", lastVersion = 10)
        assertEquals(original, json.decodeFromString<MatchRequestSnapshot>(json.encodeToString(original)))
    }

    // ── Match channel: server to client ────────────────────────────────────

    @Test
    fun `MatchSnapshot golden`() {
        val original = MatchSnapshot(
            matchId = "match-1",
            version = 3,
            serverTime = 1700000000000,
            viewer = ViewerInfo(role = ViewerRole.PLAYER, seat = 1),
            seats = listOf(
                SeatInfo(seat = 1, colour = Colour.RED, controllerUserId = "user-1", status = MatchSeatStatus.ACTIVE, connected = true, autoPlay = false, consecutiveMisses = 0),
            ),
            publicState = buildJsonObject { put("board", "royal-ludo-board-json") },
            legalMoves = listOf(LegalMove.RollDice),
            turn = TurnInfo(seat = 1, phase = "ROLL", deadline = 1700000030000),
        )
        val golden = """{"matchId":"match-1","version":3,"serverTime":1700000000000,"viewer":{"role":"PLAYER","seat":1},"seats":[{"seat":1,"colour":"RED","controllerUserId":"user-1","status":"ACTIVE","connected":true,"autoPlay":false,"consecutiveMisses":0}],"publicState":{"board":"royal-ludo-board-json"},"legalMoves":[{"type":"RollDice"}],"turn":{"seat":1,"phase":"ROLL","deadline":1700000030000}}"""
        assertGolden(original, { json.encodeToString(it) }, golden)
    }

    @Test
    fun `MatchEvent golden`() {
        val original = MatchEvent(
            version = 1,
            event = buildJsonObject { put("type", "DiceRolled"); put("value", 4) },
        )
        val golden = """{"version":1,"event":{"type":"DiceRolled","value":4}}"""
        assertGolden(original, { json.encodeToString(it) }, golden)
        assertEquals(original, json.decodeFromString<MatchEvent>(golden))
    }

    @Test
    fun `MatchEnded golden`() {
        val original = MatchEnded(
            ranking = listOf(
                RankedSeat(seat = 1, status = MatchSeatStatus.ACTIVE, place = 1),
                RankedSeat(seat = 2, status = MatchSeatStatus.REMOVED, place = 2),
            ),
        )
        val golden = """{"ranking":[{"seat":1,"status":"ACTIVE","place":1},{"seat":2,"status":"REMOVED","place":2}]}"""
        assertGolden(original, { json.encodeToString(it) }, golden)
        assertEquals(original, json.decodeFromString<MatchEnded>(golden))
    }

    // ── HTTP bodies ────────────────────────────────────────────────────────

    @Test
    fun `GuestCreated golden`() {
        assertGolden(GuestCreated(userId = "user-1", secret = "secret-token"), { json.encodeToString(it) }, """{"userId":"user-1","secret":"secret-token"}""")
    }

    @Test
    fun `Me golden`() {
        assertGolden(Me(userId = "user-1", displayName = "Alice"), { json.encodeToString(it) }, """{"userId":"user-1","displayName":"Alice"}""")
    }

    @Test
    fun `UpdateName golden`() {
        assertGolden(UpdateName(displayName = "New Name"), { json.encodeToString(it) }, """{"displayName":"New Name"}""")
    }

    @Test
    fun `MyRooms golden`() {
        val original = MyRooms(
            rooms = listOf(
                RoomSummary(roomId = "room-1", code = "ABC123", name = "Family Room", memberCount = 4, onlineCount = 2),
            ),
        )
        val golden = """{"rooms":[{"roomId":"room-1","code":"ABC123","name":"Family Room","memberCount":4,"onlineCount":2}]}"""
        assertGolden(original, { json.encodeToString(it) }, golden)
        assertEquals(original, json.decodeFromString<MyRooms>(golden))
    }

    @Test
    fun `VoiceToken golden`() {
        assertGolden(
            VoiceToken(url = "wss://voice.example.com", token = "voice-access-token", expiresAt = 1700003600000),
            { json.encodeToString(it) },
            """{"url":"wss://voice.example.com","token":"voice-access-token","expiresAt":1700003600000}""",
        )
    }

    // ── Edge cases ─────────────────────────────────────────────────────────

    @Test
    fun `unknown fields in payload are ignored`() {
        val decoded = json.decodeFromString<JoinRoom>("""{"code":"ABC123","unknownField":"ignored","another":42}""")
        assertEquals("ABC123", decoded.code)
    }

    @Test
    fun `missing required field throws`() {
        assertFailsWith<IllegalArgumentException> {
            json.decodeFromString<JoinRoom>("""{"unknownField":"value"}""")
        }
    }

    @Test
    fun `Channel enum round trips`() {
        assertEquals(Channel.ROOM, json.decodeFromString<Channel>(json.encodeToString(Channel.ROOM)))
    }

    @Test
    fun `ErrorCode enum round trips`() {
        for (code in ErrorCode.entries) {
            assertEquals(code, json.decodeFromString<ErrorCode>(json.encodeToString(code)))
        }
    }

    @Test
    fun `LegalMove sealed hierarchy round trips`() {
        val rollDice: LegalMove = LegalMove.RollDice
        assertEquals(rollDice, json.decodeFromString<LegalMove>(json.encodeToString(rollDice)))
        val movePiece: LegalMove = LegalMove.MovePiece("piece-1")
        assertEquals(movePiece, json.decodeFromString<LegalMove>(json.encodeToString(movePiece)))
    }
}
