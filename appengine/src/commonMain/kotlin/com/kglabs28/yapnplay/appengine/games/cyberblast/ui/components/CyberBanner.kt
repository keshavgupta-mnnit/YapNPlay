package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/** Over the board: the countdown, "GO!", a warning. */
@Composable
fun CyberBanner(text: String, modifier: Modifier = Modifier) {
    BasicText(
        text = text,
        style = YapNPlayTheme.typography.headline.copy(color = YapNPlayTheme.colors.TextPrimary),
        modifier = modifier
            .arenaGlass(RoundedCornerShape(YapNPlayTheme.dimens.blastBoardRadius))
            .padding(horizontal = Spacing.Lg, vertical = Spacing.Xs),
    )
}
