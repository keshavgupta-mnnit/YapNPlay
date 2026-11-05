package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tileHeaderTint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbAssetUi

/** A section's small heading in the sell dialog ("Cities", "Buildings"). */
@Composable
fun CbAssetSectionTitle(text: String, modifier: Modifier = Modifier) {
    BasicText(
        text = text,
        style = YapNPlayTheme.typography.label.copy(color = YapNPlayTheme.colors.TextSecondary, fontWeight = FontWeight.Bold),
        modifier = modifier,
    )
}

/** One sideways row of the sell dialog's cards; a card that isn't [CbAssetUi.enabled] is dimmed and can't be picked. */
@Composable
fun CbAssetRow(assets: List<CbAssetUi>, selected: Set<String>, onToggle: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        assets.forEach { asset ->
            CbAssetCard(
                name = asset.name,
                header = tileHeaderTint(asset.group, asset.kind),
                line = asset.action,
                value = asset.value,
                selected = asset.id in selected,
                enabled = asset.enabled,
                onClick = { onToggle(asset.id) },
            )
        }
    }
}
