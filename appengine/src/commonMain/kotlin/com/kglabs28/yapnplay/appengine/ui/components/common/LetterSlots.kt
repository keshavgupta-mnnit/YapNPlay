package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * A word as letter slots, in capitals: one underlined box per letter of
 * [pattern] (`_` an unknown letter, a letter a given one, a space a gap
 * between words, a hyphen as it is). [typed] fills the letter slots in
 * order, over the given letters. Slots shrink to fit the width.
 */
@Composable
fun LetterSlots(
    pattern: String,
    modifier: Modifier = Modifier,
    typed: String = "",
    letterColor: Color = YapNPlayTheme.colors.TextPrimary,
    lineColor: Color = YapNPlayTheme.colors.ArenaCrown,
    maxSlot: Dp = YapNPlayTheme.dimens.letterSlotMax,
) {
    val dimens = YapNPlayTheme.dimens
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        if (pattern.isEmpty()) return@BoxWithConstraints
        val gap = dimens.letterSlotGap
        val slot = min(maxSlot, (maxWidth - gap * (pattern.length - 1)) / pattern.length)
        val fontSize = with(LocalDensity.current) { (slot * LETTER_SHARE).toSp() }
        var next = 0
        Row(horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = Alignment.Bottom) {
            pattern.forEach { c ->
                val isSlot = c == '_' || c.isLetterOrDigit()
                val shown = when {
                    !isSlot -> if (c == ' ') "" else c.toString()
                    next < typed.length -> typed[next].uppercaseChar().toString()
                    c == '_' -> ""
                    else -> c.uppercaseChar().toString()
                }
                if (isSlot) next++
                Box(modifier = Modifier.width(slot), contentAlignment = Alignment.BottomCenter) {
                    BasicText(
                        text = shown,
                        style = YapNPlayTheme.typography.label.copy(color = letterColor, fontWeight = FontWeight.Bold, fontSize = fontSize, textAlign = TextAlign.Center),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (isSlot) Box(Modifier.fillMaxWidth().height(dimens.letterSlotLine).background(lineColor))
                }
            }
        }
    }
}

/** How much of a slot's width its letter takes. */
private const val LETTER_SHARE = 0.8f
