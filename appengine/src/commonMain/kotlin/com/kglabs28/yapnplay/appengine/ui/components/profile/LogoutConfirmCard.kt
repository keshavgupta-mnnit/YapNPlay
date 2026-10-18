package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** "Log out of YapNPlay?" as the heading, what it means under it; Cancel / red Log Out. */
@Composable
fun LogoutConfirmCard(onConfirm: () -> Unit, onCancel: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    ModalCard(
        surfaceBrush = Brush.verticalGradient(listOf(YapNPlayTheme.colors.GlassDialogTop, YapNPlayTheme.colors.GlassDialogBottom)),
        title = Strings.profileLogoutQuestion,
        onClose = onCancel,
        modifier = modifier,
        actions = {
            ProfileActionButton(text = Strings.profileCancel, kind = ProfileButtonKind.NEUTRAL, onClick = onCancel, modifier = Modifier.weight(1f))
            RedGlassButton(text = Strings.profileLogOutButton, icon = Icons.AutoMirrored.Rounded.Logout, onClick = onConfirm, modifier = Modifier.weight(1f))
        },
    ) {
        BasicText(text = Strings.profileLogoutBody, style = typography.label.copy(color = colors.TextSecondary))
    }
}
