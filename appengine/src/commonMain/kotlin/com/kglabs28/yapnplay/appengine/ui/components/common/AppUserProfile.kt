package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot

/**
 * Top bar avatar on the far right with an online dot at its corner;
 * the dot fades in once [statusVisible] (after the profile loads).
 * Person icon stands in until real avatar art lands.
 */
@Composable
fun AppUserProfile(
    online: Boolean,
    statusVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.avatar

    Box(
        modifier = modifier.size(dimens.topBarAvatar),
        contentAlignment = Alignment.BottomEnd,
    ) {
        Box(
            modifier = Modifier
                .size(dimens.topBarAvatar)
                .clip(shape)
                .background(colors.Panel)
                .border(dimens.avatarRing, colors.SocialZoneAccent, shape),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                imageVector = Icons.Rounded.Person,
                tint = colors.TextSecondary,
                size = dimens.iconMedium,
                contentDescription = Strings.topBarProfile,
            )
        }
        PresenceDot(
            online = online,
            modifier = Modifier.revealWhen(statusVisible),
            size = dimens.statusDot,
        )
    }
}
