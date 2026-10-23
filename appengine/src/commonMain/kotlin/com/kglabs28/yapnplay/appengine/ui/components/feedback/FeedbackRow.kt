package com.kglabs28.yapnplay.appengine.ui.components.feedback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass
import com.kglabs28.yapnplay.appengine.ui.screens.feedback.FeedbackRowUi

/** One message to the team: what it's about and its first line on the left, the date and Replied / Sent on the right. */
@Composable
fun FeedbackRow(row: FeedbackRowUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    Row(
        modifier = modifier
            .fillMaxWidth()
            .arenaGlass(YapNPlayTheme.shapes.panel)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Md),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            ShrinkText(text = row.kind, style = typography.sectionTitle.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
            ShrinkText(text = row.preview, style = typography.label.copy(color = colors.TextSecondary))
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            BasicText(text = row.date, style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1)
            BasicText(
                text = row.status,
                style = typography.label.copy(color = if (row.answered) colors.LiveGreen else colors.Gold, fontWeight = FontWeight.Bold),
                maxLines = 1,
            )
        }
    }
}
