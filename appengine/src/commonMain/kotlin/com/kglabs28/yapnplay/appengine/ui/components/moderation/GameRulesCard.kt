package com.kglabs28.yapnplay.appengine.ui.components.moderation

import com.kglabs28.yapnplay.appengine.ui.components.common.verticalScrollWithBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/** A game's rules as a scrolling list of glass rows (title, then what it means), in the app's dialog card. */
@Composable
fun GameRulesCard(
    rules: List<GameRuleUi>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    ModalCard(title = TableStrings.gameRules, onClose = onClose, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = YapNPlayTheme.dimens.rulesListMaxHeight)
                .verticalScrollWithBar(),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            rules.forEach { rule ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .arenaGlass(YapNPlayTheme.shapes.panel)
                        .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
                ) {
                    BasicText(text = rule.title, style = typography.sectionTitle.copy(color = colors.ArenaCrown, fontWeight = FontWeight.Bold))
                    BasicText(text = rule.body, style = typography.label.copy(color = colors.TextSecondary))
                }
            }
        }
    }
}
