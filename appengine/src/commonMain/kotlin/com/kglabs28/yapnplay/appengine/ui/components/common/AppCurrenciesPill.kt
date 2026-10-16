package com.kglabs28.yapnplay.appengine.ui.components.common

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
    diamondBalance: String,
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
        CurrencyBalance(
            icon = Icons.Rounded.Diamond,
            iconTint = colors.Purple,
            balance = diamondBalance,
            visible = balancesVisible,
        )
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
