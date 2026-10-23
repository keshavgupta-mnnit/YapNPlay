package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbRateRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbSheetRowUi
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The city's numbers on a dark card rimmed in its colour: every rent level
 * (the ones that matter highlighted) and the costs.
 */
@Composable
fun CbPromptCityCard(tint: Color, rates: List<CbRateRowUi>, costs: List<CbSheetRowUi>, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)
    Column(
        modifier = modifier
            .width(dimens.promptCityCardWidth)
            // A drawn glow (a coloured elevation shadow can show as a box behind the card).
            .cbRimGlow(tint, dimens.cardRadius)
            .clip(shape)
            .background(colors.Background)
            .border(dimens.promptRim, tint, shape),
    ) {
        Column(modifier = Modifier.padding(horizontal = Spacing.Sm, vertical = Spacing.Sm), verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            rates.forEach { row ->
                val on = row.highlighted
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimens.cbRowRadius))
                        .background(if (on) tint.copy(alpha = ROW_ALPHA) else Color.Transparent)
                        .padding(horizontal = Spacing.Xs),
                ) {
                    BasicText(text = row.label, style = typography.micro.copy(color = if (on) Color.White else colors.TextSecondary), modifier = Modifier.weight(1f))
                    BasicText(text = row.rent, style = typography.micro.copy(color = if (on) Color.White else colors.TextPrimary, fontWeight = FontWeight.Bold))
                }
            }
            costs.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.Xs)) {
                    BasicText(text = row.label, style = typography.micro.copy(color = colors.Gold), modifier = Modifier.weight(1f))
                    BasicText(text = row.amount, style = typography.micro.copy(color = colors.Gold, fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

private const val ROW_ALPHA = 0.45f
