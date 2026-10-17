package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.background
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
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.OnOffSwitch
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
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
            title = Strings.lobbyHouseRules,
            subtitle = Strings.lobbyHouseRulesSubtitle,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PillButton(
                text = Strings.lobbyRuleSheet,
                onClick = onShowRuleSheet,
                gradient = listOf(colors.RoomsSmallButton, colors.RoomsSmallButton),
                contentColor = colors.TextPrimary,
                height = dimens.smallButtonHeight,
                leadingIcon = Icons.Rounded.Gavel,
                trailingIcon = null,
                iconSize = dimens.iconSmall,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            items(rules, key = { it.key }) { rule ->
                HouseRuleRow(rule = rule, editable = editable, onToggle = { onToggle(rule.key) })
            }
        }
    }
}

@Composable
private fun HouseRuleRow(
    rule: HouseRuleUi,
    editable: Boolean,
    onToggle: () -> Unit,
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
            BasicText(text = rule.description, style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1)
        }
        OnOffSwitch(on = rule.enabled, onToggle = onToggle, enabled = editable)
    }
}
