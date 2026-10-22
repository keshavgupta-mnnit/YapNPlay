package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * A choice the table is waiting on (a colour, the Roulette colour, a swap
 * target, the keep-playing vote), as a small glass card over the piles.
 * It never dims or covers the hand or the seats: the player can still see
 * their cards and everyone else while they choose.
 */
@Composable
fun MonoTablePrompt(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .widthIn(max = YapNPlayTheme.dimens.modalWidth)
            .arenaGlass(RoundedCornerShape(YapNPlayTheme.dimens.ludoCardRadius), YapNPlayTheme.colors.ArenaDrawRim)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        BasicText(
            text = title,
            style = YapNPlayTheme.typography.title.copy(color = YapNPlayTheme.colors.TextPrimary, fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
        content()
    }
}
