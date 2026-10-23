package com.kglabs28.yapnplay.appengine.games.monochaos.domain

import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import kotlinx.serialization.Serializable

@Serializable
sealed class MonoChaosCard {
    data class NumberCard(val color: MonoChaosConstants.Color, val number: Int) : MonoChaosCard()
    data class ReverseCard(val color: MonoChaosConstants.Color) : MonoChaosCard()
    data class SkipCard(val color: MonoChaosConstants.Color) : MonoChaosCard()
    data class DrawTwoCard(val color: MonoChaosConstants.Color) : MonoChaosCard()
    data class ColoredDrawFourCard(val color: MonoChaosConstants.Color) : MonoChaosCard()
    data class DiscardAllCard(val color: MonoChaosConstants.Color) : MonoChaosCard()
    data class SkipEveryoneCard(val color: MonoChaosConstants.Color) : MonoChaosCard()
    object PrismColorWild : MonoChaosCard()
    object PrismDrawFour : MonoChaosCard()
    object PrismReverseDrawFour : MonoChaosCard()
    object PrismDrawSix : MonoChaosCard()
    object PrismDrawTen : MonoChaosCard()
    object PrismColorRoulette : MonoChaosCard()

    fun colorName(): String = when (this) {
        is NumberCard -> when (color) { MonoChaosConstants.Color.RED -> "Red"; MonoChaosConstants.Color.GREEN -> "Green"; MonoChaosConstants.Color.BLUE -> "Blue"; MonoChaosConstants.Color.GOLD -> "Gold" }
        is ReverseCard -> when (color) { MonoChaosConstants.Color.RED -> "Red"; MonoChaosConstants.Color.GREEN -> "Green"; MonoChaosConstants.Color.BLUE -> "Blue"; MonoChaosConstants.Color.GOLD -> "Gold" }
        is SkipCard -> when (color) { MonoChaosConstants.Color.RED -> "Red"; MonoChaosConstants.Color.GREEN -> "Green"; MonoChaosConstants.Color.BLUE -> "Blue"; MonoChaosConstants.Color.GOLD -> "Gold" }
        is DrawTwoCard -> when (color) { MonoChaosConstants.Color.RED -> "Red"; MonoChaosConstants.Color.GREEN -> "Green"; MonoChaosConstants.Color.BLUE -> "Blue"; MonoChaosConstants.Color.GOLD -> "Gold" }
        is ColoredDrawFourCard -> when (color) { MonoChaosConstants.Color.RED -> "Red"; MonoChaosConstants.Color.GREEN -> "Green"; MonoChaosConstants.Color.BLUE -> "Blue"; MonoChaosConstants.Color.GOLD -> "Gold" }
        is DiscardAllCard -> when (color) { MonoChaosConstants.Color.RED -> "Red"; MonoChaosConstants.Color.GREEN -> "Green"; MonoChaosConstants.Color.BLUE -> "Blue"; MonoChaosConstants.Color.GOLD -> "Gold" }
        is SkipEveryoneCard -> when (color) { MonoChaosConstants.Color.RED -> "Red"; MonoChaosConstants.Color.GREEN -> "Green"; MonoChaosConstants.Color.BLUE -> "Blue"; MonoChaosConstants.Color.GOLD -> "Gold" }
        is PrismColorWild -> "Prism"
        is PrismDrawFour -> "Prism"
        is PrismReverseDrawFour -> "Prism"
        is PrismDrawSix -> "Prism"
        is PrismDrawTen -> "Prism"
        is PrismColorRoulette -> "Prism"
    }

    fun playColor(): MonoChaosConstants.Color? = when (this) {
        is NumberCard -> color
        is ReverseCard -> color
        is SkipCard -> color
        is DrawTwoCard -> color
        is ColoredDrawFourCard -> color
        is DiscardAllCard -> color
        is SkipEveryoneCard -> color
        is PrismColorWild -> null
        is PrismDrawFour -> null
        is PrismReverseDrawFour -> null
        is PrismDrawSix -> null
        is PrismDrawTen -> null
        is PrismColorRoulette -> null
    }

    fun isPrism(): Boolean = when (this) {
        is PrismColorWild, is PrismDrawFour, is PrismReverseDrawFour,
        is PrismDrawSix, is PrismDrawTen, is PrismColorRoulette -> true
        else -> false
    }

    fun penaltyValue(): Int = when (this) {
        is DrawTwoCard -> 2
        is ColoredDrawFourCard -> 4
        is PrismDrawFour -> 4
        is PrismReverseDrawFour -> 4
        is PrismDrawSix -> 6
        is PrismDrawTen -> 10
        else -> 0
    }

    fun glyph(): String = when (this) {
        is NumberCard -> number.toString()
        is ReverseCard -> "⇄"
        is SkipCard -> "⊘"
        is DrawTwoCard -> "+2"
        is ColoredDrawFourCard -> "+4"
        is DiscardAllCard -> "+ALL"
        is SkipEveryoneCard -> "⊘ ALL"
        is PrismColorWild -> "PRISM"
        is PrismDrawFour -> "+4"
        is PrismReverseDrawFour -> "⇄+4"
        is PrismDrawSix -> "+6"
        is PrismDrawTen -> "+10"
        is PrismColorRoulette -> "ROULETTE"
    }
}
