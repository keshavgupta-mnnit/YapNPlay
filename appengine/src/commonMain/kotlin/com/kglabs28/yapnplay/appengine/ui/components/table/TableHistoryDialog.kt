package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar

/** Everything that happened in the match, newest first (for snackbars the player missed). */
@Composable
fun TableHistoryDialog(title: String, lines: List<TableFeedLineUi>, onClose: () -> Unit, modifier: Modifier = Modifier, playerTints: List<Color> = emptyList()) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    ModalCard(
        surfaceBrush = Brush.verticalGradient(listOf(colors.GlassDialogTop, colors.GlassDialogBottom)),
        title = title,
        onClose = onClose,
        modifier = modifier,
    ) {
        if (lines.isEmpty()) {
            EmptyStateText(text = Strings.tableHistoryEmpty, modifier = Modifier.fillMaxWidth())
        } else {
            val scroll = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = dimens.historyListMaxHeight).glassScrollbar(scroll).verticalScroll(scroll).padding(end = dimens.scrollbarWidth * 2),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            ) {
                lines.forEach { line ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(dimens.cbRowRadius)).background(colors.ListRow).padding(horizontal = Spacing.Xs, vertical = Spacing.Xxs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                    ) {
                        Box(Modifier.size(dimens.statusDotSmall).background(line.tone.accent(), CircleShape))
                        BasicText(text = feedText(line, playerTints), style = YapNPlayTheme.typography.label.copy(color = colors.TextSecondary))
                    }
                }
            }
        }
    }
}
