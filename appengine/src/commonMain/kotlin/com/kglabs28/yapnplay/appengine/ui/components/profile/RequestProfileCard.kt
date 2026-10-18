package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileRequestUi

/** One friend request up close: their header and "Wants to be your friend"; Cancel / Accept. */
@Composable
fun RequestProfileCard(
    request: ProfileRequestUi,
    onCopyCode: () -> Unit,
    onAccept: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalCard(
        surfaceBrush = Brush.verticalGradient(listOf(YapNPlayTheme.colors.GlassDialogTop, YapNPlayTheme.colors.GlassDialogBottom)),
        title = Strings.profileRequestProfileTitle,
        onClose = onClose,
        modifier = modifier,
        actions = {
            ProfileActionButton(text = Strings.profileCancel, kind = ProfileButtonKind.NEUTRAL, onClick = onClose, modifier = Modifier.weight(1f))
            ProfileActionButton(text = Strings.profileAccept, kind = ProfileButtonKind.CONFIRM, onClick = onAccept, modifier = Modifier.weight(1f))
        },
    ) {
        PersonHeader(person = request.person, onCopyCode = onCopyCode)
        BasicText(text = Strings.profileWantsToBeFriend, style = YapNPlayTheme.typography.label.copy(color = YapNPlayTheme.colors.TextSecondary))
    }
}
