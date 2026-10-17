package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.DropdownPicker
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/**
 * Report someone for voice chat: a dropdown of everyone present
 * ([options], e.g. "Neha · Player"), the warning that others must
 * confirm or the incident goes against the reporter, and Report.
 */
@Composable
fun ReportPlayerCard(
    options: List<String>,
    selectedIndex: Int?,
    warning: String,
    error: String?,
    onSelect: (Int) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography

    ModalCard(
        title = Strings.ludoReportTitle,
        subtitle = Strings.ludoReportBody,
        onClose = onClose,
        modifier = modifier,
        actions = {
            PillButton(
                text = Strings.ludoReportSend,
                onClick = onSubmit,
                enabled = selectedIndex != null && error == null,
                gradient = listOf(colors.Error, colors.Error.copy(alpha = GRADIENT_END)),
                contentColor = colors.TextPrimary,
                leadingIcon = Icons.Rounded.Flag,
                trailingIcon = null,
                textStyle = typography.sectionTitle,
            )
        },
    ) {
        DropdownPicker(options = options, selectedIndex = selectedIndex, placeholder = Strings.ludoReportPick, onSelect = onSelect)
        BasicText(text = error ?: warning, style = typography.label.copy(color = if (error != null) colors.Error else colors.TextSecondary))
    }
}

private const val GRADIENT_END = 0.8f
