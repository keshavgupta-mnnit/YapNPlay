package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tileHeaderTint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbModal

/**
 * "AUCTION · GOA", laid out like the buy prompt: every rent and cost on
 * the left, the city's name card (its price crossed out, the current bid
 * beside it) and picture on the right with who owns it, the highest
 * bidder and the user's balance after bidding; then the question big in
 * the middle and glowing PASS / BID. Only players who can bid higher see it.
 */
@Composable
fun CbAuctionPrompt(modal: CbModal.Auction, onPass: () -> Unit, onBid: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    val tint = tileHeaderTint(modal.group, modal.kind)
    CbPromptFrame(
        title = buildAnnotatedString {
            withStyle(SpanStyle(color = colors.Warning)) { append(CityBazaarStrings.cbAuctionOn) }
            withStyle(SpanStyle(color = colors.Cyan)) { append(modal.name.uppercase()) }
        },
        accent = colors.CbCash,
        countdown = modal.countdown,
        width = YapNPlayTheme.dimens.promptWideWidth,
        modifier = modifier,
        buttons = {
            CbNeonButton(text = CityBazaarStrings.cbPass.uppercase(), color = colors.Error, onClick = onPass, modifier = Modifier.weight(1f))
            CbNeonButton(text = modal.bidLabel, color = colors.CbGain, onClick = onBid, icon = Icons.Rounded.Gavel, modifier = Modifier.weight(1f))
        },
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            CbPromptCityCard(tint = tint, rates = modal.rates, costs = modal.costs)
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                CbPromptArt(tile = modal.tile, name = modal.name, price = modal.price, oldPrice = modal.oldPrice, tint = tint, withHouse = false)
                BasicText(text = modal.owner, style = typography.label.copy(color = colors.TextSecondary, textAlign = TextAlign.Center))
                BasicText(text = modal.leader, style = typography.label.copy(color = colors.CbCash, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
                CbBalanceStrip(cash = modal.cash, changeLabel = CityBazaarStrings.cbYourBidLabel, change = modal.cost, remaining = modal.remaining)
            }
        }
        BasicText(
            text = modal.body,
            style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, shadow = Shadow(color = colors.CbCash, blurRadius = QUESTION_GLOW)),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private const val QUESTION_GLOW = 14f
