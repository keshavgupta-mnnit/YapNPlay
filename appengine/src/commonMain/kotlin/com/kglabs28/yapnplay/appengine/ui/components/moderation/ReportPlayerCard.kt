package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
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
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.rowRadius)
    var open by remember { mutableStateOf(false) }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.RoomsSmallButton)
                .border(dimens.hairline, colors.IconMuted, shape),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { open = !open }
                    .padding(Spacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = selectedIndex?.let { options[it] } ?: Strings.ludoReportPick,
                    style = typography.sectionTitle.copy(color = if (selectedIndex == null) colors.TextSecondary else colors.TextPrimary),
                    modifier = Modifier.weight(1f),
                )
                AppIcon(imageVector = if (open) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, tint = colors.IconMuted)
            }
            if (open) {
                Column(
                    modifier = Modifier
                        .heightIn(max = dimens.reportListMaxHeight)
                        .verticalScroll(rememberScrollState()),
                ) {
                    options.forEachIndexed { index, label ->
                        BasicText(
                            text = label,
                            style = typography.label.copy(color = if (index == selectedIndex) colors.Gold else colors.TextPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelect(index)
                                    open = false
                                }
                                .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
                        )
                    }
                }
            }
        }
        BasicText(text = error ?: warning, style = typography.label.copy(color = if (error != null) colors.Error else colors.TextSecondary))
    }
}

private const val GRADIENT_END = 0.8f
