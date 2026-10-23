package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.clickable
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
 * Top bar balances, two glass pills side by side: coins (a tap offers free
 * coins) and the level (a purple shield with the level number, then
 * the XP text over a bar as wide as the text). Numbers keep a minimum
 * width and fade in once [balancesVisible], so loading never resizes them.
 */
@Composable
fun AppCurrenciesPill(
    coinBalance: String,
    level: String,
    levelProgress: Float,
    xpLabel: String,
    balancesVisible: Boolean,
    modifier: Modifier = Modifier,
    onAddCoins: () -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val pill = Modifier
        .height(dimens.homeBarControl)
        .glassPill(glowColor = colors.TopBarIcon, surface = colors.TopBarCurrencyPill)
        .padding(horizontal = Spacing.Xs)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
        Row(modifier = pill, verticalAlignment = Alignment.CenterVertically) {
            CurrencyBalance(
                icon = Icons.Rounded.MonetizationOn,
                iconTint = colors.Gold,
                balance = coinBalance,
                visible = balancesVisible,
                onAdd = onAddCoins,
            )
        }
        Row(
            modifier = pill.revealWhen(balancesVisible),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            Box(
                modifier = Modifier
                    .size(dimens.levelShield)
                    .background(Brush.verticalGradient(listOf(colors.LevelShieldTop, colors.LevelShieldBottom)), YapNPlayTheme.shapes.shield),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(text = level, style = YapNPlayTheme.typography.levelBadge.copy(color = colors.TextPrimary))
            }
            Column(modifier = Modifier.width(dimens.xpLabelMinWidth), verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
                // End-aligned, so the number grows to the left as XP climbs.
                BasicText(text = xpLabel, style = YapNPlayTheme.typography.xpLabel.copy(color = colors.TextPrimary, textAlign = TextAlign.End), maxLines = 1, modifier = Modifier.fillMaxWidth())
                Box(Modifier.fillMaxWidth().height(dimens.levelBarHeight).clip(CircleShape).background(colors.TopBarIcon.copy(alpha = LEVEL_TRACK))) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(levelProgress).background(Brush.horizontalGradient(listOf(colors.LevelShieldTop, colors.FriendsAccent)), CircleShape))
                }
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
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Row(
        modifier = modifier.clickable(role = Role.Button, onClick = onAdd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
    ) {
        AppIcon(
            imageVector = icon,
            tint = iconTint,
            size = dimens.homeBarIcon,
        )
        BasicText(
            text = balance,
            modifier = Modifier
                .widthIn(min = dimens.balanceMinWidth)
                .revealWhen(visible),
            style = YapNPlayTheme.typography.topBarValue.copy(
                textAlign = TextAlign.Start,
                color = colors.TextPrimary,
            ),
        )
    }
}

private const val LEVEL_TRACK = 0.3f
