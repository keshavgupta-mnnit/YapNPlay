package com.kglabs28.yapnplay.appengine.preview

import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.BoardCorner
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.RoyalLudoMatchState
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry

/** A sample Royal Ludo table: the user blue at the bottom, three CPUs round the board. */
object PreviewLudo {
    fun state(myTurn: Boolean): RoyalLudoMatchState {
        val me = LudoBoardGeometry.rotationFor(LudoColour.BLUE.ordinal)
        fun card(seat: Int, name: String, colour: LudoColour, avatar: String, isMe: Boolean = false) = LudoPlayerCardUi(
            seat = seat,
            userId = "u$seat",
            name = name,
            avatarId = avatar,
            level = if (isMe) "5" else "",
            colour = colour,
            corner = BoardCorner.entries[LudoBoardGeometry.cornerOf(colour.ordinal, me)],
            isMe = isMe,
            isTurn = isMe == myTurn && (isMe || seat == 2),
            talking = false,
            timerFraction = 0.6f,
            left = false,
            placeLabel = null,
            mutedForMe = false,
            canSpeak = isMe,
        )
        val mine = card(1, "Keshav", LudoColour.BLUE, "a05", isMe = true)
        return RoyalLudoMatchState(
            loaded = true,
            title = "ROYAL LUDO",
            subtitle = "Quick Play",
            rotation = me,
            myCard = mine,
            opponents = listOf(
                card(2, "CPU 1", LudoColour.RED, "a01"),
                card(3, "CPU 2", LudoColour.GREEN, "a02"),
                card(4, "CPU 3", LudoColour.YELLOW, "a03"),
            ),
            elapsed = "0:07",
            canRoll = myTurn,
        )
    }
}
