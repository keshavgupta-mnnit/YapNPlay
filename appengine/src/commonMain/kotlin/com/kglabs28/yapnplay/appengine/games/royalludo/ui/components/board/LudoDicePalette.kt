package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.lerp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.DicePalette
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players.tint

/**
 * The glass die tinted with the colour of the player whose turn it is
 * (cream glass when nobody's colour applies), so everyone sees at a
 * glance whose roll it is.
 */
@Composable
fun ludoDicePalette(colour: LudoColour?): DicePalette {
    val colors = YapNPlayTheme.colors
    if (colour == null) {
        return DicePalette(listOf(colors.DiceGold, colors.DiceIvory, colors.DiceAmber), colors.DiceDeep, colors.DicePip)
    }
    val tint = colour.tint()
    return DicePalette(
        tints = listOf(lerp(tint, colors.DiceIvory, LIGHT), colors.DiceIvory, lerp(tint, colors.DiceIvory, MID)),
        deep = lerp(tint, colors.Background, DEEP),
        pip = lerp(tint, colors.Background, PIP),
    )
}

private const val LIGHT = 0.55f
private const val MID = 0.3f
private const val DEEP = 0.35f
private const val PIP = 0.75f
