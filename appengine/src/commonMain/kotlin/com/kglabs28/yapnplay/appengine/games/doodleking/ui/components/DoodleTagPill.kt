package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/**
 * The word's tag from the server ("Hinglish · Food") in a glass pill of its own,
 * in cyan, at the end of the board's bottom line, so it is easier to guess.
 */
@Composable
fun DoodleTagPill(tag: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(YapNPlayTheme.dimens.doodleStatusHeight)
            .arenaGlass(RoundedCornerShape(percent = PILL))
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(text = tag, style = YapNPlayTheme.typography.label.copy(color = YapNPlayTheme.colors.Cyan, fontWeight = FontWeight.Bold), maxLines = 1)
    }
}

private const val PILL = 50
