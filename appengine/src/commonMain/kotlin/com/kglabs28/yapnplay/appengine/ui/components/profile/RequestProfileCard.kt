package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
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
        surfaceBrush = AppBrushes.dialogGeneric,
        title = ProfileStrings.profileRequestProfileTitle,
        onClose = onClose,
        modifier = modifier,
        actions = {
            ProfileActionButton(text = ProfileStrings.profileCancel, kind = ProfileButtonKind.NEUTRAL, onClick = onClose, modifier = Modifier.weight(1f))
            ProfileActionButton(text = ProfileStrings.profileAccept, kind = ProfileButtonKind.CONFIRM, onClick = onAccept, modifier = Modifier.weight(1f))
        },
    ) {
        PersonHeader(person = request.person, onCopyCode = onCopyCode)
        BasicText(text = ProfileStrings.profileWantsToBeFriend, style = YapNPlayTheme.typography.label.copy(color = YapNPlayTheme.colors.TextSecondary))
    }
}
