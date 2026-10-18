package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** "Choose your avatar": every preset, the picked one ringed; Save keeps it. */
@OptIn(ExperimentalLayoutApi::class)
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
        title = Strings.profileAvatarTitle,
        onClose = onClose,
        modifier = modifier,
        actions = {
            GlassPillButton(text = Strings.profileSave, onClick = onSave, leadingIcon = Icons.Rounded.Check, enabled = !saving, height = dimens.ctaHeight)
        },
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            AvatarPresets.ids.forEach { id ->
                PresetAvatar(
                    avatarId = id,
                    size = dimens.avatarPickerItem,
                    modifier = Modifier
                        .border(dimens.borderThin, if (id == selected) colors.Cyan else Color.Transparent, CircleShape)
                        .padding(dimens.borderThin * 2)
                        .clickable { onPick(id) },
                )
            }
        }
    }
}
