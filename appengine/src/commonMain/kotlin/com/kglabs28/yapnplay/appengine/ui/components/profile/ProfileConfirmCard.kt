package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** "Remove Neha?" / "Block Neha?" / "Log out?": what happens, then Cancel and the tinted action. */
@Composable
fun ProfileConfirmCard(
    title: String,
    body: String,
    confirmLabel: String,
    confirmTint: Color,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalCard(
        title = title,
        subtitle = body,
        onClose = onCancel,
        modifier = modifier,
        actions = {
            GlassPillButton(text = Strings.profileCancel, onClick = onCancel, accent = YapNPlayTheme.colors.IconMuted, height = YapNPlayTheme.dimens.ctaHeight)
            TintedGlassButton(text = confirmLabel, tint = confirmTint, onClick = onConfirm)
        },
    )
}
