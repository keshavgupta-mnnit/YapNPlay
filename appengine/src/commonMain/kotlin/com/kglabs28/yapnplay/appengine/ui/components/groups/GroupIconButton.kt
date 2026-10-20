package com.kglabs28.yapnplay.appengine.ui.components.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.badge

/** The group's icon in its badge colour; a tap switches to the next icon (small arrows in the corner). */
@Composable
fun GroupIconButton(icon: PlayRoomIcon, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val badge = icon.badge()
    Box(
        modifier = modifier
            .size(dimens.groupIconButton)
            .clip(RoundedCornerShape(dimens.cbRowRadius))
            .background(badge.fill)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = Strings.groupChangeIcon },
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = badge.icon, tint = colors.TextPrimary, size = dimens.iconMedium)
        AppIcon(imageVector = Icons.Rounded.Autorenew, tint = colors.TextPrimary, size = dimens.iconTiny, modifier = Modifier.align(Alignment.BottomEnd))
    }
}
