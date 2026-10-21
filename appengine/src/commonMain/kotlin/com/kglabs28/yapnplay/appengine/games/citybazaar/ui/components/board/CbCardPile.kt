package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

/**
 * The Chest and Luck piles: cards stacked a little untidily, bottom first.
 * Each card is turned [degrees] and nudged ([dx], [dy] board units); the
 * last one is the face-up top card.
 */
object CbCardPile {
    data class Card(val degrees: Float, val dx: Float, val dy: Float)

    val cards = listOf(
        Card(-9f, -0.06f, 0.08f),
        Card(6f, 0.08f, 0.05f),
        Card(-3f, 0f, 0.03f),
        Card(0f, 0f, 0f),
    )

    /** Width ÷ height of a card (portrait). */
    const val ASPECT = 0.72f
}
