package com.kglabs28.yapnplay.appengine.ui.components.common

import com.kglabs28.yapnplay.appengine.core.ui.Strings
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen

/**
 * Top bar glass pill with coin and diamond balances, each followed
 * by a "+" top-up mark. Numbers keep a minimum width and fade in
 * once [balancesVisible], so loading never resizes the pill.
 */
@Composable
fun AppCurrenciesPill(
    coinBalance: String,
    level: String,
    levelProgress: Float,
    xpLabel: String,
    balancesVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Row(
        modifier = modifier
            .height(dimens.topBarControlHeight)
            .glassPill(glowColor = colors.TopBarIcon, surface = colors.TopBarCurrencyPill)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Md),
    ) {
        CurrencyBalance(
            icon = Icons.Rounded.MonetizationOn,
            iconTint = colors.Gold,
            balance = coinBalance,
            visible = balancesVisible,
        )
        // Level: a star, "Lv 3" and a thin bar filling towards the next level.
        Column(modifier = Modifier.revealWhen(balancesVisible), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
                // The level number inside the app's hexagon badge.
                Box(
                    modifier = Modifier
                        .size(dimens.levelHexagon)
                        .background(Brush.verticalGradient(listOf(colors.Gold, colors.Warning)), YapNPlayTheme.shapes.hexagon),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(text = level, style = YapNPlayTheme.typography.micro.copy(fontWeight = FontWeight.Black, color = colors.Background))
                }
                BasicText(text = xpLabel, style = YapNPlayTheme.typography.micro.copy(fontWeight = FontWeight.SemiBold, color = colors.TextPrimary))
            }
            Box(Modifier.fillMaxWidth().height(dimens.levelBarHeight).clip(CircleShape).background(colors.TopBarIcon.copy(alpha = LEVEL_TRACK))) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(levelProgress).background(Brush.horizontalGradient(listOf(colors.Gold, colors.LiveGreen)), CircleShape))
            }
        }
    }
}

@Composable
private fun CurrencyBalance(
    icon: ImageVector,
    iconTint: Color,
    balance: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        AppIcon(
            imageVector = icon,
            tint = iconTint,
        )
        BasicText(
            text = balance,
            modifier = Modifier
                .widthIn(min = dimens.balanceMinWidth)
                .revealWhen(visible),
            style = YapNPlayTheme.typography.label.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = colors.TextPrimary,
            ),
        )
        AppIcon(
            imageVector = Icons.Rounded.Add,
            tint = colors.TopBarIcon,
            size = dimens.iconSmall,
        )
    }
}

private const val LEVEL_TRACK = 0.3f
