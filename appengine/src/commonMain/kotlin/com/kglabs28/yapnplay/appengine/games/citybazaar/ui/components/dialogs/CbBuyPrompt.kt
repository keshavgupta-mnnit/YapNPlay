package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tileHeaderTint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbModal

/**
 * "LANDED ON GOA" as in the design: every rent and cost on the left (land
 * and 1-house rent lit), the city's name card and picture with the balance
 * box on the right, then "Interested in buying?" big in the middle and
 * glowing NO / YES.
 */
@Composable
fun CbBuyPrompt(modal: CbModal.Buy, onNo: () -> Unit, onYes: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    val tint = tileHeaderTint(modal.group, modal.kind)
    CbPromptFrame(
        title = buildAnnotatedString {
            withStyle(SpanStyle(color = colors.Warning)) { append(Strings.cbLandedOn) }
            withStyle(SpanStyle(color = colors.Cyan)) { append(modal.name.uppercase()) }
        },
        accent = colors.Cyan,
        countdown = modal.countdown,
        width = YapNPlayTheme.dimens.promptWideWidth,
        modifier = modifier,
        buttons = {
            CbNeonButton(text = Strings.cbNoCaps, color = colors.Error, onClick = onNo, modifier = Modifier.weight(1f))
            CbNeonButton(text = Strings.cbYesCaps, color = colors.CbGain, onClick = onYes, enabled = modal.canAfford, icon = Icons.Rounded.Check, modifier = Modifier.weight(1f))
        },
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            CbPromptCityCard(tint = tint, rates = modal.rates, costs = modal.costs)
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                CbPromptArt(tile = modal.tile, name = modal.name, price = modal.price, tint = tint, withHouse = false)
                CbBalanceStrip(cash = modal.cash, changeLabel = Strings.cbPropertyCostLabel, change = modal.cost, remaining = modal.remaining)
            }
        }
        BasicText(
            text = if (modal.canAfford) Strings.cbInterestedInBuying else Strings.cbCantAfford,
            style = typography.title.copy(color = if (modal.canAfford) colors.TextPrimary else colors.CbLoss, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, shadow = Shadow(color = colors.Cyan, blurRadius = QUESTION_GLOW)),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private const val QUESTION_GLOW = 14f
