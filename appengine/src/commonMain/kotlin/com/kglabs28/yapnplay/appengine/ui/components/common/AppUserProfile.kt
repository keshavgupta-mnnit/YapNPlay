package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.ui.components.profile.AvatarPresets
import com.kglabs28.yapnplay.appengine.ui.placeholders.PresetAvatarArtwork
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
 * Shows the user's preset avatar (person icon if none); a tap opens the profile.
 */
@Composable
fun AppUserProfile(
    online: Boolean,
    statusVisible: Boolean,
    modifier: Modifier = Modifier,
    /** The user's preset avatar ("" = person icon). */
    avatarId: String = "",
    onClick: () -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.avatar

    // The tap (and its highlight) is the round avatar itself, not the square around it.
    Box(
        modifier = modifier.size(dimens.topBarAvatar),
        contentAlignment = Alignment.BottomEnd,
    ) {
        Box(
            modifier = Modifier
                .size(dimens.topBarAvatar)
                .clip(shape)
                .clickable(onClick = onClick)
                .background(colors.Panel)
                .border(dimens.avatarRing * RING_SHARE, colors.GameZoneAccent, shape)
                .padding(dimens.avatarRing * RING_SHARE),
            contentAlignment = Alignment.Center,
        ) {
            val preset = AvatarPresets.index(avatarId)
            if (preset >= 0) {
                PresetAvatarArtwork(index = preset, modifier = Modifier.fillMaxSize().clip(shape), contentDescription = Strings.topBarProfile)
            } else {
                AppIcon(
                    imageVector = Icons.Rounded.Person,
                    tint = colors.TextSecondary,
                    size = dimens.iconMedium,
                    contentDescription = Strings.topBarProfile,
                )
            }
        }
        PresenceDot(
            online = online,
            modifier = Modifier.revealWhen(statusVisible),
            size = dimens.statusDotSmall,
        )
    }
}

/** The top bar avatar's ring, a little thinner than the usual avatar ring. */
private const val RING_SHARE = 0.9f
