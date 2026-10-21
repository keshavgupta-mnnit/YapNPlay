package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** "Log out of YapNPlay?" as the heading, what it means under it; Cancel / red Log Out. */
@Composable
fun LogoutConfirmCard(onConfirm: () -> Unit, onCancel: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    ModalCard(
        surfaceBrush = AppBrushes.dialogGeneric,
        title = ProfileStrings.profileLogoutQuestion,
        onClose = onCancel,
        modifier = modifier,
        actions = {
            ProfileActionButton(text = ProfileStrings.profileCancel, kind = ProfileButtonKind.NEUTRAL, onClick = onCancel, modifier = Modifier.weight(1f))
            RedGlassButton(text = ProfileStrings.profileLogOutButton, icon = Icons.AutoMirrored.Rounded.Logout, onClick = onConfirm, modifier = Modifier.weight(1f))
        },
    ) {
        BasicText(text = ProfileStrings.profileLogoutBody, style = typography.label.copy(color = colors.TextSecondary))
    }
}
