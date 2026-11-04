package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleCardMark
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** At a card's pill end: a brush while they draw, a tick on a green circle once they guessed it. */
@Composable
fun DoodleCardMarkIcon(mark: DoodleCardMark, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val size = YapNPlayTheme.dimens.iconMedium
    when (mark) {
        DoodleCardMark.DRAWING -> AppIcon(imageVector = Icons.Rounded.Brush, tint = colors.ArenaCrown, size = size, contentDescription = DoodleStrings.dkDrawingMark, modifier = modifier)
        DoodleCardMark.GUESSED -> Box(modifier = modifier.size(size).background(colors.ReadyGreen, CircleShape), contentAlignment = Alignment.Center) {
            AppIcon(imageVector = Icons.Rounded.Check, tint = colors.TextPrimary, size = size * TICK_SHARE, contentDescription = DoodleStrings.dkGuessedMark)
        }
        DoodleCardMark.NONE -> Unit
    }
}

private const val TICK_SHARE = 0.75f
