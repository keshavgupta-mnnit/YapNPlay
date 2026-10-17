package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbDeckRowUi

/**
 * One Luck / Shagun Chest result: the dice total in a badge of the pile's
 * [tint], what happens, and the amount on its own line at the right
 * (green to gain, red to pay).
 */
@Composable
fun CbDeckRow(row: CbDeckRowUi, tint: Color, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Xs), verticalAlignment = Alignment.Top) {
        Box(Modifier.size(YapNPlayTheme.dimens.cbRollBadge).background(tint, CircleShape), contentAlignment = Alignment.Center) {
            BasicText(text = row.roll, style = typography.micro.copy(color = colors.Background, fontWeight = FontWeight.Bold))
        }
        Column(modifier = Modifier.weight(1f)) {
            BasicText(text = row.text, style = typography.label.copy(color = colors.TextPrimary))
            if (row.amount.isNotEmpty()) {
                BasicText(
                    text = row.amount,
                    style = typography.label.copy(color = if (row.gain) colors.CbGain else colors.CbLoss, fontWeight = FontWeight.Bold, textAlign = TextAlign.End),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
