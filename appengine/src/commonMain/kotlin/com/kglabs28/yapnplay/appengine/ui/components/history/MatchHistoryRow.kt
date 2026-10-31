package com.kglabs28.yapnplay.appengine.ui.components.history

import androidx.compose.ui.draw.alpha
import com.kglabs28.yapnplay.appengine.ui.components.common.TierLooks
import com.kglabs28.yapnplay.appengine.ui.components.common.TierLook
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.podium.podiumMedal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass
import com.kglabs28.yapnplay.appengine.ui.screens.history.MatchHistoryRowUi

/**
 * One past match, in one line: the game, its place (1st gold, 2nd silver,
 * 3rd bronze), its league (in the league picker's look) and "group · date"; then the XP; then the coins,
 * each behind a thin divider, in columns of the same width on every row. A tap opens its leaderboard, except for an abandoned one.
 */
@Composable
fun MatchHistoryRow(row: MatchHistoryRowUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            // An abandoned match opens nothing: dimmed, so it doesn't look tappable.
            .alpha(if (row.opensBoard) 1f else ABANDONED_ALPHA)
            .arenaGlass(YapNPlayTheme.shapes.panel)
            .then(if (row.opensBoard) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Md),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            ShrinkText(text = row.game, style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                row.place?.let { InfoPill(text = it, tint = placeTint(row)) }
                // The league, in the same look as the league picker (cheapest plainest, dearest royal).
                if (row.league.isNotEmpty()) LeaguePill(text = row.league, tier = TierLooks.forLeague(row.leagueRank.coerceAtLeast(0), row.leagueCount))
                // Every detail a pill: where (Quick Play green, a group blue) and when.
                InfoPill(text = row.mode, tint = if (row.quickPlay) colors.LiveGreen else colors.PlayerBlue, modifier = Modifier.weight(1f, fill = false))
                InfoPill(text = row.time, tint = colors.Purple, modifier = Modifier.weight(1f, fill = false))
            }
        }
        // Fixed widths, so every row's columns line up whatever the amounts.
        RowDivider()
        ShrinkText(
            text = row.xp,
            style = typography.label.copy(color = colors.Gold, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier.width(YapNPlayTheme.dimens.historyXpWidth),
        )
        RowDivider()
        ShrinkText(
            text = row.coins,
            style = typography.label.copy(color = if (row.lost) colors.PlayerRed else colors.LiveGreen, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier.width(YapNPlayTheme.dimens.historyCoinsWidth),
        )
    }
}

/** 1st gold, 2nd silver, 3rd bronze, later places cyan; an abandoned match red. */
@Composable
private fun placeTint(row: MatchHistoryRowUi): Color = when (val place = row.placeNumber) {
    null -> YapNPlayTheme.colors.PlayerRed
    in 1..PODIUM_PLACES -> podiumMedal(place)
    else -> YapNPlayTheme.colors.Cyan
}

/** A detail of the row (the place, where, when), bold in a pill of [tint]. */
@Composable
private fun InfoPill(text: String, tint: Color, modifier: Modifier = Modifier) {
    val shape = YapNPlayTheme.shapes.pill
    // A long group name or date shrinks to fit rather than being cut.
    ShrinkText(
        text = text,
        style = YapNPlayTheme.typography.label.copy(color = tint, fontWeight = FontWeight.Black),
        modifier = modifier
            .clip(shape)
            .background(tint.copy(alpha = PILL_FILL_ALPHA))
            .border(YapNPlayTheme.dimens.hairline, tint.copy(alpha = PILL_RIM_ALPHA), shape)
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
    )
}

/** The match's league in its tier's glass and rim. */
@Composable
private fun LeaguePill(text: String, tier: TierLook) {
    val shape = YapNPlayTheme.shapes.pill
    BasicText(
        text = text,
        style = YapNPlayTheme.typography.label.copy(color = tier.rim, fontWeight = FontWeight.Black),
        maxLines = 1,
        modifier = Modifier
            .clip(shape)
            .background(Brush.verticalGradient(tier.fill))
            .border(YapNPlayTheme.dimens.hairline, tier.rim, shape)
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
    )
}

/** A thin upright line between the row's groups. */
@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .width(YapNPlayTheme.dimens.hairline)
            .fillMaxHeight()
            .padding(vertical = Spacing.Xxs)
            .background(YapNPlayTheme.colors.TextSecondary.copy(alpha = DIVIDER_ALPHA)),
    )
}

private const val PODIUM_PLACES = 3
private const val ABANDONED_ALPHA = 0.45f
private const val PILL_FILL_ALPHA = 0.16f
private const val PILL_RIM_ALPHA = 0.6f
private const val DIVIDER_ALPHA = 0.3f
