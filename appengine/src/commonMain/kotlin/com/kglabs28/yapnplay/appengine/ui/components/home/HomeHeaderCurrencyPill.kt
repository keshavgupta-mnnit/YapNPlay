package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Top header currency pill (Gold Coin or Diamond) matching mockup.
 */
@Composable
fun HomeHeaderCurrencyPill(
    iconGlyph: String,
    valueText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dimens.panelRadius))
            .background(colors.GlassSurface)
            .border(dimens.hairline, colors.TextDisabled.copy(alpha = 0.3f), RoundedCornerShape(dimens.panelRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            BasicText(
                text = iconGlyph,
                style = YapNPlayTheme.typography.body,
            )
            BasicText(
                text = valueText,
                style = YapNPlayTheme.typography.body.copy(
                    fontFamily = HeadlineFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = colors.TextPrimary,
                ),
            )
        }
    }
}
