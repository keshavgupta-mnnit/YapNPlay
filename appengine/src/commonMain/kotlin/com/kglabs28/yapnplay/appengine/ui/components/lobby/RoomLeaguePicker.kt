package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CoinsStrings
import com.kglabs28.yapnplay.appengine.ui.components.coins.LeagueRow
import com.kglabs28.yapnplay.appengine.ui.components.coins.LeagueUi
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.TierLooks

/**
 * The room's league, first in House Rules: the chosen league in its tier
 * colours. The leader taps it to open every league as a drop-down list
 * (ones someone in the lobby can't pay are locked) and picks one; everyone
 * else just sees it.
 */
@Composable
fun RoomLeaguePicker(
    leagues: List<LeagueUi>,
    selectedEntry: Int,
    editable: Boolean,
    onPick: (entryCoins: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = leagues.firstOrNull { it.entryCoins == selectedEntry } ?: return
    var open by rememberSaveable { mutableStateOf(false) }
    val dimens = YapNPlayTheme.dimens
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
        BasicText(text = CoinsStrings.roomLeagueTitle, style = YapNPlayTheme.typography.label.copy(color = YapNPlayTheme.colors.TextSecondary))
        if (!open) {
            LeagueRow(
                // The chosen league always shows lit, even if someone can't pay it now.
                league = selected.copy(canEnter = true),
                tier = TierLooks.forLeague(leagues.indexOf(selected), leagues.size),
                onClick = if (editable) ({ open = true }) else null,
                selected = true,
                trailing = if (editable) ({ AppIcon(imageVector = Icons.Rounded.ExpandMore, tint = YapNPlayTheme.colors.TextPrimary, size = dimens.iconSmall) }) else null,
            )
        } else {
            leagues.forEachIndexed { rank, league ->
                LeagueRow(
                    league = league,
                    tier = TierLooks.forLeague(rank, leagues.size),
                    // A league someone here can't pay is locked: not tappable.
                    onClick = if (league.canEnter) ({
                        onPick(league.entryCoins)
                        open = false
                    }) else null,
                    selected = league.entryCoins == selectedEntry,
                    trailing = if (rank == 0) ({ AppIcon(imageVector = Icons.Rounded.ExpandLess, tint = YapNPlayTheme.colors.TextPrimary, size = dimens.iconSmall) }) else null,
                )
            }
            BasicText(text = CoinsStrings.roomLeagueHint, style = YapNPlayTheme.typography.micro.copy(color = YapNPlayTheme.colors.TextSecondary))
        }
    }
}
