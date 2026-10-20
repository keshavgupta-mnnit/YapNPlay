package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** Green for money in, red for money out, cyan for news. */
@Composable
fun TableFeedTone.accent(): Color = when (this) {
    TableFeedTone.GAIN -> YapNPlayTheme.colors.LiveGreen
    TableFeedTone.LOSS -> YapNPlayTheme.colors.Error
    TableFeedTone.INFO -> YapNPlayTheme.colors.Cyan
}
