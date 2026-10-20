package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbOwnedRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbSheetRowUi
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CbCityCard
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPanel

/**
 * A player (or the bank) at a glance, on frosted white: the name on a band
 * of their colour, balance and worth, then each property on a row of its group's
 * colour with the rent it earns now (a tap opens its card).
 */
@Composable
fun CbPlayerPanel(
    name: String,
    band: Color,
    sheet: List<CbSheetRowUi>,
    cities: List<CbOwnedRowUi>,
    onPropertyClick: (tile: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cbRowRadius)
    Column(
        modifier = modifier
            .background(colors.Background, shape)
            .tableFrostedGlass(tint = colors.AvatarRingSilver, shape = shape, radius = dimens.cbRowRadius, glow = dimens.zero)
            .clip(shape),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        BasicText(
            text = name,
            style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth().background(band).padding(vertical = Spacing.Xxs),
        )
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = Spacing.Xs).padding(bottom = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            CbSheetTable(rows = sheet)
            if (cities.isEmpty()) {
                CbDialogNote(text = Strings.cbNoProperties)
            } else {
                // Grouped by colour, most valuable first; a tap opens the full card.
                cities.forEach { CbCityCard(city = it, onClick = { onPropertyClick(it.tile) }) }
            }
        }
    }
}

