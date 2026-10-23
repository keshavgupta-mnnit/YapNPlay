package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.network.DeviceAccountOffer
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar

/** First launch on a phone that already has an account: continue it (name, avatar, level shown) or start a new one. */
@Composable
fun DeviceAccountCard(offer: DeviceAccountOffer, onContinue: () -> Unit, onStartNew: () -> Unit) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = Strings.deviceAccountTitle,
        subtitle = Strings.deviceAccountBody(offer.name, offer.level),
        actions = {
            GlassPillButton(text = Strings.deviceAccountNew, onClick = onStartNew)
            PillButton(
                text = Strings.deviceAccountContinue,
                onClick = onContinue,
                gradient = listOf(colors.CtaGlassGreen),
                contentColor = colors.TextPrimary,
                rimColor = colors.CtaGlassGreenRim,
                glassFill = colors.CtaGlassGreen,
                leadingIcon = Icons.Rounded.PlayArrow,
                trailingIcon = null,
                height = YapNPlayTheme.dimens.smallButtonHeight,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        },
    ) {
        PresetAvatar(avatarId = offer.avatarId, size = YapNPlayTheme.dimens.profileAvatar, rimColor = colors.Cyan)
    }
}
