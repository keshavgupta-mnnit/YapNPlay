package com.kglabs28.yapnplay.appengine.preview

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.ui.placeholders.BuiltInImage
import com.kglabs28.yapnplay.appengine.ui.placeholders.builtInPainter
import com.kglabs28.yapnplay.appengine.ui.components.table.rememberMonoAnchors
import com.kglabs28.yapnplay.appengine.ui.components.table.rememberMonoFlightState
import com.kglabs28.yapnplay.appengine.ui.screens.mono.MatchMonoContent
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.rememberGlassDiceState
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.RoyalLudoTable
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameArenaBackground

/** The screens the renderer draws, each with sample data. */
object PreviewScreens {
    val all: List<Pair<String, @Composable () -> Unit>> = listOf(
        "leaderboard" to { Leaderboard() },
        "mono_my_turn" to { MonoTable(myTurn = true, handSize = 9) },
        "mono_big_hand" to { MonoTable(myTurn = false, handSize = 18) },
        "mono_6_players" to { MonoTable(myTurn = true, handSize = 7, seats = 6) },
        "mono_8_players" to { MonoTable(myTurn = true, handSize = 7, seats = 8) },
        "ludo" to { LudoTable(myTurn = true) },
        "ludo_waiting" to { LudoTable(myTurn = false) },
    )

    @Composable
    private fun LudoTable(myTurn: Boolean) {
        GameArenaBackground {
            RoyalLudoTable(state = PreviewLudo.state(myTurn), send = {}, dice = rememberGlassDiceState(), micGranted = { true })
        }
    }

    @Composable
    private fun MonoTable(myTurn: Boolean, handSize: Int, seats: Int = 4) {
        val state = PreviewMono.state(myTurn, handSize, seats)
        MatchMonoContent(
            state = state,
            send = {},
            onClose = {},
            anchors = rememberMonoAnchors(),
            flights = rememberMonoFlightState(),
        )
    }

    @Composable
    private fun Leaderboard() {
        MatchLeaderboard(
            board = sampleBoard,
            gameArt = { Image(painter = builtInPainter(BuiltInImage.CITY_BAZAAR_ART), contentDescription = null, modifier = it) },
            onPlayAgain = {},
        )
    }

    private fun entry(place: Int, name: String, title: String, coins: String, isMe: Boolean = false, avatar: String = "") = LeaderboardEntryUi(
        place = place,
        placeLabel = listOf("1st", "2nd", "3rd", "4th", "5th")[place - 1],
        name = name,
        avatarId = avatar.ifEmpty { "a0$place" },
        title = title,
        score = "",
        exp = "+100 EXP",
        tint = Color.Unspecified,
        isMe = isMe,
        micOn = place % 2 == 1,
        coins = coins,
    )

    private val sampleBoard = LeaderboardUi(
        game = "CITY ROYALE",
        duration = "3:26",
        entries = listOf(
            entry(1, "CPU 1", "👑 Baadshah", "+300 coins"),
            entry(2, "Keshav Gupta Long Name", "🦈 Shark", "+150 coins", isMe = true),
            entry(3, "CPU 3", "💸 Vasooli", "+50 coins"),
            entry(4, "CPU 2", "🧾 Rent Payer", "0 coins"),
        ),
        myPlace = "2nd",
        myExp = "+300",
    )
}
