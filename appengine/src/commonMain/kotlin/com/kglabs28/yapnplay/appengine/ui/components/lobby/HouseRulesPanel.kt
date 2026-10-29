package com.kglabs28.yapnplay.appengine.ui.components.lobby

import com.kglabs28.yapnplay.appengine.ui.components.coins.LeagueUi
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.ui.components.common.OnOffSwitch
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.SectionHeader
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.HouseRuleUi

/**
 * "In-House Rules" card: header with a Rules button (fixed rule sheet),
 * then one row per toggle ("Fill with Bots" first). Only the leader can
 * flip toggles ([editable]); everyone else sees them read-only.
 */
@Composable
fun HouseRulesPanel(
    rules: List<HouseRuleUi>,
    editable: Boolean,
    onToggle: (String) -> Unit,
    onShowRuleSheet: () -> Unit,
    modifier: Modifier = Modifier,
    /** Rule whose description is sliding (tapped); null = none. */
    slidingRuleKey: String? = null,
    onSlideRule: (String) -> Unit = {},
    /** The room's league, shown first (above Fill with bots); empty hides it. */
    leagues: List<LeagueUi> = emptyList(),
    entryCoins: Int = 0,
    onPickLeague: (entryCoins: Int) -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Column(
        modifier = modifier
            .glassPanel(glowColor = colors.IconMuted, surface = colors.RoomsPanel.copy(alpha = 0.9f))
            .padding(Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        SectionHeader(
            icon = Icons.Rounded.Settings,
            title = RoomStrings.lobbyHouseRules,
            modifier = Modifier.fillMaxWidth(),
        ) {
            GlassPillButton(
                text = RoomStrings.lobbyRuleSheet,
                onClick = onShowRuleSheet,
                leadingIcon = Icons.Rounded.Gavel,
            )
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            if (leagues.isNotEmpty()) item(key = LEAGUE_ITEM_KEY) {
                RoomLeaguePicker(leagues = leagues, selectedEntry = entryCoins, editable = editable, onPick = onPickLeague)
            }
            items(rules, key = { it.key }) { rule ->
                HouseRuleRow(
                    rule = rule,
                    editable = editable,
                    onToggle = { onToggle(rule.key) },
                    sliding = rule.key == slidingRuleKey,
                    onSlide = { onSlideRule(rule.key) },
                )
            }
        }
    }
}

@Composable
private fun HouseRuleRow(
    rule: HouseRuleUi,
    editable: Boolean,
    onToggle: () -> Unit,
    sliding: Boolean,
    onSlide: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(YapNPlayTheme.dimens.rowRadius))
            .background(colors.RoomsRow.copy(alpha = 0.85f))
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BasicText(text = rule.title, style = typography.itemTitle.copy(color = colors.TextPrimary), maxLines = 1)
            // Tap a long description to slide it; the lobby stops it on any
            // other touch.
            BasicText(
                text = rule.description,
                style = typography.micro.copy(color = colors.TextSecondary),
                maxLines = 1,
                modifier = Modifier
                    .then(if (sliding) Modifier.basicMarquee(iterations = Int.MAX_VALUE, initialDelayMillis = 0) else Modifier)
                    .pointerInput(onSlide) { detectTapGestures { onSlide() } },
            )
        }
        OnOffSwitch(on = rule.enabled, onToggle = onToggle, enabled = editable)
    }
}

private const val LEAGUE_ITEM_KEY = "league"
