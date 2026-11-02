package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** The small round smiley worn on another player's avatar (like an online dot): opens the emoji picker for them. */
@Composable
fun EmojiGiftButton(
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = YapNPlayTheme.dimens.ludoMuteButton,
    /** Its picker is open: lit. */
    open: Boolean = false,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (open) colors.Gold.copy(alpha = OPEN_FILL_ALPHA) else colors.ArenaGlassBottom)
            .border(dimens.hairline, if (open) colors.Gold else colors.ArenaGlassRim, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = Icons.Rounded.EmojiEmotions, tint = colors.Gold, size = size * ICON_SHARE)
    }
}

private const val OPEN_FILL_ALPHA = 0.25f
private const val ICON_SHARE = 0.85f
