package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.screens.welcome.WelcomeState

/**
 * First launch of a brand-new account: "Welcome to YapNPlay!", the name to
 * show (pre-filled) and every avatar to pick from, the picked one large;
 * Skip keeps what they got, LET'S PLAY saves.
 */
@Composable
fun WelcomeCard(
    state: WelcomeState,
    onNameChange: (String) -> Unit,
    onPickAvatar: (String) -> Unit,
    onSave: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    ModalCard(
        scrollable = true,
        surfaceBrush = AppBrushes.dialogGeneric,
        title = ProfileStrings.welcomeTitle,
        subtitle = ProfileStrings.welcomeSubtitle,
        modifier = modifier.width(dimens.avatarPickerWidth),
        actions = {
            ProfileActionButton(text = ProfileStrings.welcomeSkip, kind = ProfileButtonKind.NEUTRAL, onClick = onSkip, enabled = !state.saving, modifier = Modifier.weight(1f))
            ProfileActionButton(text = ProfileStrings.welcomePlay, kind = ProfileButtonKind.CONFIRM, onClick = onSave, enabled = !state.saving, modifier = Modifier.weight(1f))
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Md)) {
            PresetAvatar(avatarId = state.avatarId, size = dimens.avatarPickerPreview, rimColor = colors.Cyan)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                BasicText(text = ProfileStrings.welcomeNameLabel, style = typography.label.copy(color = colors.TextSecondary))
                ProfileTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    hint = ProfileStrings.profileNameHint,
                    maxLength = AppConstants.MAX_NAME_LENGTH,
                    isError = state.error != null,
                    onDone = { if (!state.saving) onSave() },
                )
                state.error?.let { BasicText(text = it, style = typography.micro.copy(color = colors.Error)) }
                BasicText(text = ProfileStrings.welcomeAvatarLabel, style = typography.label.copy(color = colors.TextSecondary))
                AvatarGrid(selected = state.avatarId, onPick = onPickAvatar)
            }
        }
    }
}
