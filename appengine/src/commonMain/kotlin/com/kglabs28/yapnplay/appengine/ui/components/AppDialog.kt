package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Modal dialog with a title, body and up to two actions.
 * Title and body come from the caller (resources at the call site).
 */
@Composable
fun AppDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmLabel: String? = null,
    onConfirm: (() -> Unit)? = null,
    dismissLabel: String? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .clip(YapNPlayTheme.shapes.panel)
                .background(colors.Panel)
                .border(dimens.borderThin, colors.Cyan.copy(alpha = 0.6f), YapNPlayTheme.shapes.panel)
                .padding(Spacing.Lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.Md),
        ) {
            BasicText(
                text = title,
                style = YapNPlayTheme.typography.title.copy(color = colors.TextPrimary),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            BasicText(
                text = body,
                style = YapNPlayTheme.typography.body.copy(color = colors.TextSecondary),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (dismissLabel != null) {
                    TextActionButton(text = dismissLabel, onClick = onDismiss)
                }
                if (confirmLabel != null && onConfirm != null) {
                    PrimaryButton(text = confirmLabel, onClick = onConfirm)
                }
            }
        }
    }
}
