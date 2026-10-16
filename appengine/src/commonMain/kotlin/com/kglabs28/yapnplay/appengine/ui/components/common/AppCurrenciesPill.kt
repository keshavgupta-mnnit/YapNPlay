package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Top bar glass pill combining Gold Coins, Diamonds, and Level badge in one layout matching topbar.png.
 */
@Composable
fun AppCurrenciesPill(
    coinBalance: String,
    diamondBalance: String,
    level: String,
    modifier: Modifier = Modifier,
    coinIcon: String = Strings.iconCoin,
    diamondIcon: String = Strings.iconDiamond,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dimens.cardRadius))
            .background(colors.GlassSurface)
            .border(dimens.hairline, colors.TextDisabled.copy(alpha = 0.3f), RoundedCornerShape(dimens.cardRadius))
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            // Gold Coin Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.Panel)
                    .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
            ) {
                BasicText(
                    text = coinIcon,
                    style = YapNPlayTheme.typography.caption,
                )
                BasicText(
                    text = coinBalance,
                    style = YapNPlayTheme.typography.caption.copy(
                        fontFamily = HeadlineFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = colors.TextPrimary,
                    ),
                )
            }

            // Diamond Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.Panel)
                    .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
            ) {
                BasicText(
                    text = diamondIcon,
                    style = YapNPlayTheme.typography.caption,
                )
                BasicText(
                    text = diamondBalance,
                    style = YapNPlayTheme.typography.caption.copy(
                        fontFamily = HeadlineFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = colors.TextPrimary,
                    ),
                )
            }

            // Hexagon Level Badge inside Currency Pill
            Box(
                modifier = Modifier
                    .size(dimens.chipHeight)
                    .clip(YapNPlayTheme.shapes.hexagon)
                    .background(colors.Panel)
                    .border(dimens.hairline, colors.GameZoneAccent, YapNPlayTheme.shapes.hexagon),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = level,
                    style = YapNPlayTheme.typography.caption.copy(
                        fontFamily = HeadlineFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = colors.GameZoneAccent,
                    ),
                )
            }
        }
    }
}
