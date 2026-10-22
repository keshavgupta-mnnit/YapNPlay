package com.kglabs28.yapnplay.appengine.preview

import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCard
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoPlayer
import com.kglabs28.yapnplay.appengine.games.mono.domain.isPlayableOn
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants.Color
import com.kglabs28.yapnplay.appengine.ui.screens.mono.MatchMonoState

/** A sample Standard MONO table: four players, the user in seat 1. */
object PreviewMono {
    private val deck = listOf(
        MonoCard.NumberCard(Color.RED, 0), MonoCard.NumberCard(Color.RED, 1), MonoCard.NumberCard(Color.BLUE, 8),
        MonoCard.NumberCard(Color.GOLD, 0), MonoCard.NumberCard(Color.GOLD, 3), MonoCard.SkipCard(Color.RED),
        MonoCard.DrawTwoCard(Color.GREEN), MonoCard.PrismDrawFour, MonoCard.NumberCard(Color.BLUE, 5),
        MonoCard.ReverseCard(Color.GREEN), MonoCard.NumberCard(Color.GREEN, 7), MonoCard.PrismColorWild,
        MonoCard.NumberCard(Color.RED, 9), MonoCard.NumberCard(Color.BLUE, 2), MonoCard.DrawTwoCard(Color.BLUE),
        MonoCard.NumberCard(Color.GOLD, 6), MonoCard.SkipCard(Color.GOLD), MonoCard.NumberCard(Color.GREEN, 4),
    )

    fun state(myTurn: Boolean, handSize: Int, seats: Int = 4): MatchMonoState {
        val hand = List(handSize) { deck[it % deck.size] }
        val top = MonoCard.NumberCard(Color.BLUE, 9)
        val players = listOf(player(1, "Keshav", isMe = true, hand = hand, cards = hand.size, avatar = "a05")) +
            (2..seats).map { seat -> player(seat, "CPU ${seat - 1}", cards = 8 - seat % 3, avatar = "a0${(seat - 2) % 9 + 1}") }
        val match = MonoMatch(
            matchId = "m1",
            title = "Quick Play",
            gameId = "mono",
            players = players,
            spectators = emptyList(),
            spectatorCount = 0,
            currentSeat = if (myTurn) 1 else 3,
            phase = "AWAITING_PLAY",
            topCard = top,
            topCardColor = Color.BLUE,
            drawPileCount = 60,
            discardPileCount = 12,
            viewerUserId = "u1",
        )
        return MatchMonoState(
            loaded = true,
            match = match,
            myTurn = myTurn,
            playableIndexes = if (myTurn) hand.indices.filter { hand[it].isPlayableOn(top, Color.BLUE) }.toSet() else emptySet(),
            timerFraction = 0.6f,
            topColour = Color.BLUE,
            title = "Quick Play",
            elapsed = "0:06",
        )
    }

    private fun player(seat: Int, name: String, isMe: Boolean = false, hand: List<MonoCard> = emptyList(), cards: Int, avatar: String) = MonoPlayer(
        seat = seat,
        userId = "u$seat",
        name = name,
        level = if (isMe) 5 else 0,
        isMe = isMe,
        micOn = false,
        autoplay = false,
        removed = false,
        isCpu = !isMe,
        avatarId = avatar,
        hand = hand,
        cardCount = cards,
        presence = "PRESENT",
    )
}
