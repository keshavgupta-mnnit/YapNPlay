package com.kglabs28.yapnplay.appengine.ui.components.coins

import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CoinsStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/** "Watch an ad for coins": WATCH AD (green), and PLAY STARTER when [CoinsOfferUi.offerFree]. */
@Composable
fun CoinsOfferCard(
    offer: CoinsOfferUi,
    onWatchAd: () -> Unit,
    onPlayFree: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = offer.title,
        subtitle = offer.body,
        onClose = onClose,
        modifier = modifier,
        actions = {
            if (offer.offerFree) {
                GlassPillButton(text = CoinsStrings.playFree, onClick = onPlayFree, leadingIcon = Icons.Rounded.SportsEsports, height = YapNPlayTheme.dimens.ctaHeight)
            }
            PillButton(
                text = if (offer.watchingAd) CoinsStrings.watchingAd else CoinsStrings.watchAd,
                onClick = onWatchAd,
                enabled = !offer.watchingAd,
                gradient = listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd),
                contentColor = colors.CtaGreenContent,
                leadingIcon = Icons.Rounded.OndemandVideo,
                trailingIcon = null,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        },
    ) {
        offer.message?.let {
            BasicText(text = it, style = YapNPlayTheme.typography.label.copy(color = colors.Gold, fontWeight = FontWeight.SemiBold))
        }
    }
}
