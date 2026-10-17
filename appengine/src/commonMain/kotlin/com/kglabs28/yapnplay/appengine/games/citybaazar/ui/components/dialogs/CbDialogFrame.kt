package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatTimerChip

/**
 * City Baazar's dialog: frosted glass with an [accent] glow, the title (with
 * the tile's group colour as a chip), the time left, a divider, the body
 * and the buttons (Royal Ludo's glass, side by side).
 */
@Composable
fun CbDialogFrame(
    title: String,
    accent: Color,
    modifier: Modifier = Modifier,
    chip: Color? = null,
    countdown: String? = null,
    buttons: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Column(
        modifier = modifier
            .width(dimens.cbDialogWidth)
            .glassPanel(glowColor = accent, surfaceBrush = Brush.verticalGradient(listOf(colors.MemberCardTop, colors.MemberCardBottom)))
            .padding(Spacing.Md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart).padding(end = dimens.cutButtonMinWidth / 2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                chip?.let { Box(Modifier.size(dimens.cbHeaderChip).background(it, RoundedCornerShape(dimens.cbRowRadius / 2))) }
                BasicText(text = title, style = YapNPlayTheme.typography.title.copy(color = accent, fontWeight = FontWeight.Bold), maxLines = 2)
            }
            countdown?.let { SeatTimerChip(text = it, modifier = Modifier.align(Alignment.CenterEnd)) }
        }
        Box(Modifier.fillMaxWidth().height(dimens.hairline).background(accent.copy(alpha = DIVIDER_ALPHA)))
        content()
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
            buttons()
        }
    }
}

/** A grey explanatory line inside a City Baazar dialog. */
@Composable
fun CbDialogNote(text: String, modifier: Modifier = Modifier) {
    BasicText(
        text = text,
        style = YapNPlayTheme.typography.label.copy(color = YapNPlayTheme.colors.TextSecondary, textAlign = TextAlign.Center),
        modifier = modifier.fillMaxWidth(),
    )
}

private const val DIVIDER_ALPHA = 0.35f
