package com.kglabs28.yapnplay.appengine.ui.components.coins

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.TierLook

/**
 * One league in its tier's colours (cheapest plain, dearest royal): name,
 * then the entry fee. A league that can't be entered is dimmed with a
 * lock; the [selected] one carries a tick and a bolder rim. [onClick] null
 * makes it a label only. Shared by Quick Play and the Game Room lobby.
 */
@Composable
fun LeagueRow(
    league: LeagueUi,
    tier: TierLook,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.cardRadius)
    val accent = if (league.canEnter) tier.rim else colors.TextSecondary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.leagueRowHeight)
            .alpha(if (league.canEnter) 1f else LOCKED_ALPHA)
            .clip(shape)
            .background(Brush.linearGradient(tier.fill))
            .border(if (tier.royal || selected) dimens.borderThin else dimens.promptRim, tier.rim.copy(alpha = if (selected) 1f else RIM_ALPHA), shape)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = Spacing.Md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        if (selected) AppIcon(imageVector = Icons.Rounded.CheckCircle, tint = tier.rim, size = dimens.iconSmall)
        else if (tier.royal) AppIcon(imageVector = Icons.Rounded.WorkspacePremium, tint = tier.rim, size = dimens.iconSmall)
        BasicText(
            text = league.name,
            modifier = Modifier.weight(1f),
            style = YapNPlayTheme.typography.title.copy(color = if (league.canEnter) colors.TextPrimary else colors.TextSecondary, fontWeight = FontWeight.Bold),
        )
        if (!league.canEnter) AppIcon(imageVector = Icons.Rounded.Lock, tint = colors.TextSecondary, size = dimens.iconSmall)
        if (league.entryCoins > 0) AppIcon(imageVector = Icons.Rounded.MonetizationOn, tint = accent, size = dimens.iconSmall)
        BasicText(text = league.feeLabel, style = YapNPlayTheme.typography.title.copy(color = accent, fontWeight = FontWeight.Black))
        trailing?.invoke()
    }
}

private const val RIM_ALPHA = 0.85f
private const val LOCKED_ALPHA = 0.7f
