package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar

/** An avatar in a glowing ring ([medal] on the podium, multicolour otherwise), with a green mic badge for players who talked. */
@Composable
fun LeaderboardAvatar(avatarId: String, size: Dp, micOn: Boolean, modifier: Modifier = Modifier, medal: Color? = null) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val ring = medal?.let { Brush.sweepGradient(listOf(it, Color.White, it, it.copy(alpha = MEDAL_DEEP), it)) }
        ?: Brush.sweepGradient(listOf(colors.LiveGreen, colors.Cyan, colors.SocialZoneAccent, colors.Gold, colors.LiveGreen))
    val glow = medal ?: colors.SocialZoneAccent
    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .size(size)
                .shadow(elevation = dimens.promptGlow, shape = CircleShape, ambientColor = glow, spotColor = medal ?: colors.Cyan)
                .background(ring, CircleShape)
                .padding(dimens.boardRing),
            contentAlignment = Alignment.Center,
        ) {
            PresetAvatar(avatarId = avatarId, size = size - dimens.boardRing * 2, rimColor = Color.Transparent)
        }
        if (micOn) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(dimens.boardMicBadge)
                    .background(colors.LiveGreen, CircleShape)
                    .border(dimens.hairline, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(imageVector = Icons.Rounded.Mic, tint = Color.White, size = dimens.iconSmall)
            }
        }
    }
}

private const val MEDAL_DEEP = 0.7f
