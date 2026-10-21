package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The turn countdown, in the same frosted glass as the rest of the top
 * bar. It burns teal while it is the local player's turn and steel while
 * they wait, so the player can read whose clock is running at a glance.
 */
@Composable
fun TableTurnPill(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.ludoGlossRadius)
    val tint = if (active) colors.CtaGreenStart else colors.LudoTeal

    Row(
        modifier = modifier
            .tableFrostedGlass(
                tint = tint,
                shape = shape,
                radius = dimens.ludoGlossRadius,
                glow = dimens.zero,
            )
            .padding(horizontal = Spacing.Md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        BasicText(
            text = text,
            style = YapNPlayTheme.typography.title.copy(
                color = if (active) tint else colors.TextPrimary,
                fontWeight = FontWeight.Black,
            ),
            maxLines = 1,
        )
    }
}
