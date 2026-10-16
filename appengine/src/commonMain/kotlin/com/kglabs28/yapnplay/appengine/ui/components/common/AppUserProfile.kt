package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot

/**
 * Top bar user profile avatar on far right with name below matching topbar.png.
 */
@Composable
fun AppUserProfile(
    userName: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
        ) {
            Box(
                modifier = Modifier
                    .size(dimens.avatarList)
                    .clip(CircleShape)
                    .background(colors.Panel)
                    .border(dimens.avatarRing, colors.GameZoneAccent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = Strings.iconDefaultAvatar,
                    style = YapNPlayTheme.typography.body,
                )
            }
            PresenceDot(online = true, onlineColor = colors.Success)
        }

        BasicText(
            text = userName,
            style = YapNPlayTheme.typography.caption.copy(
                fontFamily = HeadlineFontFamily,
                fontWeight = FontWeight.Bold,
                color = colors.TextPrimary,
            ),
        )
    }
}
