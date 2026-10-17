package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/** An incident was recorded against the user: points lost and play blocked; a rewarded ad lifts it. */
@Composable
fun PenaltyCard(
    title: String,
    body: String,
    watchingAd: Boolean,
    onWatchAd: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = title,
        subtitle = body,
        modifier = modifier,
        actions = {
            GlassPillButton(text = Strings.ludoOk, onClick = onClose, leadingIcon = Icons.Rounded.Close, height = YapNPlayTheme.dimens.ctaHeight)
            PillButton(
                text = if (watchingAd) Strings.ludoWatchingAd else Strings.ludoWatchAd,
                onClick = onWatchAd,
                enabled = !watchingAd,
                gradient = listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd),
                contentColor = colors.CtaGreenContent,
                leadingIcon = Icons.Rounded.OndemandVideo,
                trailingIcon = null,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        },
    )
}
