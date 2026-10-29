package com.kglabs28.yapnplay.appengine.ui.components.coins

import com.kglabs28.yapnplay.appengine.ui.components.common.TierLooks
import com.kglabs28.yapnplay.appengine.ui.components.common.TierLook
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
import com.kglabs28.yapnplay.appengine.core.ui.CoinsStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/**
 * Pick the Quick Play league: one row per league, coloured by price — the
 * cheap ones plain slate and bronze, then silver and gold, the dearest a
 * royal violet with a gold rim and crown. Locked rows open the coins offer.
 */
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
            // Tier by price: the cheapest plainest, the dearest royal.
            val byFee = picker.leagues.sortedBy { it.entryCoins }
            picker.leagues.forEach { league ->
                LeagueRow(
                    league = league,
                    tier = TierLooks.forLeague(byFee.indexOf(league), byFee.size),
                    onClick = { onPick(league.entryCoins) },
                )
            }
        }
    }
}
