package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** "Change Avatar": the picked avatar large on the left, every avatar beside it (picked one ringed); Cancel / Save. */
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
    val listScroll = rememberScrollState()
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
            Column(modifier = Modifier.weight(1f).heightIn(max = dimens.avatarPickerGridMaxHeight).glassScrollbar(listScroll).verticalScroll(listScroll).padding(end = YapNPlayTheme.dimens.scrollbarWidth * 2)) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = AppConstants.AVATAR_PICKER_COLUMNS,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                AvatarPresets.ids.forEach { id ->
                    PresetAvatar(
                        avatarId = id,
                        size = dimens.avatarPickerItem,
                        modifier = Modifier
                            .border(dimens.borderThin, if (id == selected) colors.CtaGreenStart else Color.Transparent, CircleShape)
                            .padding(dimens.borderThin * 2)
                            .clickable { onPick(id) },
                    )
                }
            }
            }
        }
    }
}
