package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * One-line message bar with an optional action. Message text comes from
 * the caller (resources at the call site).
 */
@Composable
fun AppSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Row(
        modifier = modifier
            .clip(YapNPlayTheme.shapes.button)
            .background(colors.Raised)
            .border(dimens.borderThin, colors.Cyan.copy(alpha = 0.6f), YapNPlayTheme.shapes.button)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = message,
            style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (actionLabel != null && onAction != null) {
            TextActionButton(text = actionLabel, onClick = onAction)
        }
    }
}
