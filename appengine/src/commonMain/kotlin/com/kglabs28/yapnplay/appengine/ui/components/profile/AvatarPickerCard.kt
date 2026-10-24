package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** "Change Avatar": the picked avatar large on the left, every avatar beside it (picked one ringed); Cancel / Save. */
@Composable
fun AvatarPickerCard(
    selected: String,
    saving: Boolean,
    onPick: (String) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    ModalCard(
        surfaceBrush = AppBrushes.dialogGeneric,
        title = ProfileStrings.profileAvatarTitle,
        onClose = onClose,
        modifier = modifier.width(dimens.avatarPickerWidth),
        actions = {
            ProfileActionButton(text = ProfileStrings.profileCancel, kind = ProfileButtonKind.NEUTRAL, onClick = onClose, modifier = Modifier.weight(1f))
            ProfileActionButton(text = ProfileStrings.profileSave, kind = ProfileButtonKind.CONFIRM, onClick = onSave, enabled = !saving, modifier = Modifier.weight(1f))
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Md)) {
            PresetAvatar(avatarId = selected, size = dimens.avatarPickerPreview, rimColor = colors.Cyan)
            AvatarGrid(selected = selected, onPick = onPick, modifier = Modifier.weight(1f))
        }
    }
}
