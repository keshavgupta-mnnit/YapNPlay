package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** "Remove Friend" / "Block User" / "Unblock User": their avatar beside the question and what it means; Cancel and the action (red, or green to unblock). */
@Composable
fun ConfirmPersonCard(
    title: String,
    avatarId: String,
    question: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    confirmKind: ProfileButtonKind = ProfileButtonKind.DANGER,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    ModalCard(
        surfaceBrush = Brush.verticalGradient(listOf(YapNPlayTheme.colors.GlassDialogTop, YapNPlayTheme.colors.GlassDialogBottom)),
        title = title,
        onClose = onCancel,
        modifier = modifier,
        actions = {
            ProfileActionButton(text = Strings.profileCancel, kind = ProfileButtonKind.NEUTRAL, onClick = onCancel, modifier = Modifier.weight(1f))
            ProfileActionButton(text = confirmLabel, kind = confirmKind, onClick = onConfirm, modifier = Modifier.weight(1f))
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            PresetAvatar(avatarId = avatarId, size = YapNPlayTheme.dimens.personRowAvatar)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
                BasicText(text = question, style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
                BasicText(text = body, style = typography.micro.copy(color = colors.TextSecondary))
            }
        }
    }
}
