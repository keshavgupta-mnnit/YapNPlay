package com.kglabs28.yapnplay.appengine.ui.components.coins

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CoinsStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** Pick the Quick Play league: one row per league, locked rows open the coins offer. */
@Composable
fun LeaguePickerCard(
    picker: LeaguePickerUi,
    onPick: (entryCoins: Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    ModalCard(title = CoinsStrings.leaguesTitle, subtitle = picker.balanceLabel, onClose = onClose, modifier = modifier) {
        picker.message?.let {
            BasicText(text = it, style = YapNPlayTheme.typography.label.copy(color = colors.Gold, fontWeight = FontWeight.SemiBold))
        }
        if (picker.leagues.isEmpty()) {
            BasicText(text = CoinsStrings.leaguesLoading, style = YapNPlayTheme.typography.label.copy(color = colors.TextSecondary))
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            picker.leagues.forEach { league -> LeagueRow(league = league, onClick = { onPick(league.entryCoins) }) }
        }
    }
}

@Composable
private fun LeagueRow(league: LeagueUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.cardRadius)
    val accent = if (league.canEnter) colors.Gold else colors.TextSecondary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.leagueRowHeight)
            .clip(shape)
            .background(colors.TopBarCurrencyPill)
            .border(dimens.promptRim, accent.copy(alpha = RIM_ALPHA), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        BasicText(
            text = league.name,
            modifier = Modifier.weight(1f),
            style = YapNPlayTheme.typography.title.copy(color = if (league.canEnter) colors.TextPrimary else colors.TextSecondary, fontWeight = FontWeight.Bold),
        )
        if (!league.canEnter) AppIcon(imageVector = Icons.Rounded.Lock, tint = colors.TextSecondary, size = dimens.iconSmall)
        if (league.entryCoins > 0) AppIcon(imageVector = Icons.Rounded.MonetizationOn, tint = accent, size = dimens.iconSmall)
        BasicText(text = league.feeLabel, style = YapNPlayTheme.typography.title.copy(color = accent, fontWeight = FontWeight.Black))
    }
}

private const val RIM_ALPHA = 0.6f
