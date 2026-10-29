package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCard
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCard

/**
 * The order a hand is shown in, as indexes into it (taps still send the
 * real index). One group per colour (red, green, blue, gold): its 0–9,
 * then that colour's power cards (+2, +4, Skip, Reverse, …). After every
 * colour, the wild (prism) cards: Wild, +4, Reverse +4, +6, +10, Roulette.
 */
object MonoHandOrder {

    fun mono(hand: List<MonoCard>): List<Int> = order(hand, { it.playColor()?.ordinal }) { card ->
        when (card) {
            is MonoCard.NumberCard -> card.number
            is MonoCard.DrawTwoCard -> DRAW_TWO
            is MonoCard.SkipCard -> SKIP
            is MonoCard.ReverseCard -> REVERSE
            MonoCard.PrismColorWild -> WILD
            MonoCard.PrismDrawFour -> WILD_DRAW_FOUR
        }
    }

    fun chaos(hand: List<MonoChaosCard>): List<Int> = order(hand, { it.playColor()?.ordinal }) { card ->
        when (card) {
            is MonoChaosCard.NumberCard -> card.number
            is MonoChaosCard.DrawTwoCard -> DRAW_TWO
            is MonoChaosCard.ColoredDrawFourCard -> DRAW_FOUR
            is MonoChaosCard.SkipCard -> SKIP
            is MonoChaosCard.ReverseCard -> REVERSE
            is MonoChaosCard.SkipEveryoneCard -> SKIP_EVERYONE
            is MonoChaosCard.DiscardAllCard -> DISCARD_ALL
            MonoChaosCard.PrismColorWild -> WILD
            MonoChaosCard.PrismDrawFour -> WILD_DRAW_FOUR
            MonoChaosCard.PrismReverseDrawFour -> WILD_REVERSE_DRAW_FOUR
            MonoChaosCard.PrismDrawSix -> WILD_DRAW_SIX
            MonoChaosCard.PrismDrawTen -> WILD_DRAW_TEN
            MonoChaosCard.PrismColorRoulette -> WILD_ROULETTE
        }
    }

    /** By [colour] (wilds, with none, last), then [rank] inside it. */
    private fun <C> order(hand: List<C>, colour: (C) -> Int?, rank: (C) -> Int): List<Int> =
        hand.indices.sortedWith(compareBy<Int>({ colour(hand[it]) ?: Int.MAX_VALUE }, { rank(hand[it]) }))

    // Numbers rank 0–9; a colour's power cards follow them, in this order.
    private const val DRAW_TWO = 10
    private const val DRAW_FOUR = 11
    private const val SKIP = 12
    private const val REVERSE = 13
    private const val SKIP_EVERYONE = 14
    private const val DISCARD_ALL = 15
    private const val WILD = 20
    private const val WILD_DRAW_FOUR = 21
    private const val WILD_REVERSE_DRAW_FOUR = 22
    private const val WILD_DRAW_SIX = 23
    private const val WILD_DRAW_TEN = 24
    private const val WILD_ROULETTE = 25
}
