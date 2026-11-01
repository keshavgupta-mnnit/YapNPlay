package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText

/**
 * The word as a row of letter slots: `_` for each hidden letter, a given
 * letter in its place, a wider gap between words. Long words shrink to fit.
 */
@Composable
fun DoodleHintText(hint: String, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val spaced = hint.map { if (it == ' ') WORD_GAP else it.toString() }.joinToString(LETTER_GAP)
    ShrinkText(
        text = spaced,
        style = YapNPlayTheme.typography.sectionTitle.copy(color = colors.TextPrimary),
        modifier = modifier,
    )
}

private const val LETTER_GAP = " "
private const val WORD_GAP = "  "
