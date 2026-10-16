package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Centered muted line shown where a list has nothing to show yet
 * ("No groups joined till now").
 */
@Composable
fun EmptyStateText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        BasicText(
            text = text,
            style = YapNPlayTheme.typography.caption.copy(
                color = YapNPlayTheme.colors.TextSecondary,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
