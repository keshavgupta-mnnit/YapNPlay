package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.headlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Shows a room code as one box per character. Missing characters render dim.
 */
@Composable
fun RoomCodeDisplay(
    code: String,
    modifier: Modifier = Modifier,
    length: Int = AppConstants.ROOM_CODE_LENGTH,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(length) { index ->
            val char = code.getOrNull(index)?.toString() ?: ""
            Box(
                modifier = Modifier
                    .size(dimens.touchMin)
                    .clip(YapNPlayTheme.shapes.button)
                    .background(colors.Panel)
                    .border(dimens.borderThin, colors.Cyan.copy(alpha = 0.6f), YapNPlayTheme.shapes.button),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = char,
                    style = YapNPlayTheme.typography.title.copy(
                        fontFamily = headlineFontFamily(),
                        fontWeight = FontWeight.SemiBold,
                        color = if (char.isEmpty()) colors.TextDisabled else colors.TextPrimary,
                    ),
                )
            }
        }
    }
}
