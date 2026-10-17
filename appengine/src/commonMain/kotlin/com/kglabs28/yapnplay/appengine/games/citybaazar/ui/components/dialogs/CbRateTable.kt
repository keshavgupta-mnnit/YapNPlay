package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbRateRowUi

/** The rate card: rent per tier; the highlighted tier glows green. */
@Composable
fun CbRateTable(rows: List<CbRateRowUi>, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cbRowRadius)
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (row.highlighted) colors.CbGain.copy(alpha = GLOW_ALPHA) else Color.Transparent, shape)
                    .border(dimens.hairline, if (row.highlighted) colors.CbGain else Color.Transparent, shape)
                    .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(text = row.label, style = typography.label.copy(color = colors.TextSecondary), modifier = Modifier.weight(1f))
                BasicText(text = row.rent, style = typography.label.copy(color = if (row.highlighted) colors.CbGain else colors.TextPrimary, fontWeight = FontWeight.Bold))
            }
        }
    }
}

private const val GLOW_ALPHA = 0.15f
