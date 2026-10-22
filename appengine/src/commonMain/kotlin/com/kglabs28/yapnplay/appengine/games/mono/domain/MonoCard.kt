package com.kglabs28.yapnplay.appengine.games.mono.domain

import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import kotlinx.serialization.Serializable

@Serializable
sealed class MonoCard {
    data class NumberCard(val color: MonoConstants.Color, val number: Int) : MonoCard()
    data class ReverseCard(val color: MonoConstants.Color) : MonoCard()
    data class SkipCard(val color: MonoConstants.Color) : MonoCard()
    data class DrawTwoCard(val color: MonoConstants.Color) : MonoCard()
    object PrismColorWild : MonoCard()
    object PrismDrawFour : MonoCard()

    fun colorName(): String = when (this) {
        is NumberCard -> when (color) { MonoConstants.Color.RED -> "Red"; MonoConstants.Color.GREEN -> "Green"; MonoConstants.Color.BLUE -> "Blue"; MonoConstants.Color.GOLD -> "Gold" }
        is ReverseCard -> when (color) { MonoConstants.Color.RED -> "Red"; MonoConstants.Color.GREEN -> "Green"; MonoConstants.Color.BLUE -> "Blue"; MonoConstants.Color.GOLD -> "Gold" }
        is SkipCard -> when (color) { MonoConstants.Color.RED -> "Red"; MonoConstants.Color.GREEN -> "Green"; MonoConstants.Color.BLUE -> "Blue"; MonoConstants.Color.GOLD -> "Gold" }
        is DrawTwoCard -> when (color) { MonoConstants.Color.RED -> "Red"; MonoConstants.Color.GREEN -> "Green"; MonoConstants.Color.BLUE -> "Blue"; MonoConstants.Color.GOLD -> "Gold" }
        is PrismColorWild -> "Prism"
        is PrismDrawFour -> "Prism"
    }

    fun playColor(): MonoConstants.Color? = when (this) {
        is NumberCard -> color
        is ReverseCard -> color
        is SkipCard -> color
        is DrawTwoCard -> color
        is PrismColorWild -> null
        is PrismDrawFour -> null
    }

    fun isPrism(): Boolean = this is PrismColorWild || this is PrismDrawFour

    /** A power card is every card that is not a number (No Power Finish: never the last card played). */
    fun isPower(): Boolean = this !is NumberCard

    /** Cards this one makes the next player draw; 0 when it is not a draw card. */
    fun penaltyValue(): Int = when (this) {
        is DrawTwoCard -> 2
        is PrismDrawFour -> 4
        else -> 0
    }

    fun glyph(): String = when (this) {
        is NumberCard -> number.toString()
        is ReverseCard -> "⇄"
        is SkipCard -> "⊘"
        is DrawTwoCard -> "+2"
        is PrismColorWild -> "PRISM"
        is PrismDrawFour -> "+4"
    }
}
