package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar

/** Every preset avatar in rows, the [selected] one ringed green; scrolls when taller than the room. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarGrid(selected: String, onPick: (String) -> Unit, modifier: Modifier = Modifier) {
    val listScroll = rememberScrollState()
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Column(modifier = modifier.heightIn(max = dimens.avatarPickerGridMaxHeight).glassScrollbar(listScroll).verticalScroll(listScroll).padding(end = dimens.scrollbarWidth * 2)) {
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
