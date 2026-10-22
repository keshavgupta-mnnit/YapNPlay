package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.cbGlassBlack
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tileHeaderTint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbOwnedRowUi

/**
 * A property as a compact glassy black card: border and name in its
 * group's colour, its worth on the left and the rent it earns now on the
 * right. A tap opens its full card.
 */
@Composable
fun CbCityCard(city: CbOwnedRowUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val tint = tileHeaderTint(city.group, city.kind)
    val shape = RoundedCornerShape(dimens.ludoCardRadius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.cbCityCardHeight)
            .cbGlassBlack(shape)
            .border(dimens.ludoTimerStroke, tint, shape)
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        ShrinkText(text = city.name, style = typography.cardTitle.copy(color = tint, fontWeight = FontWeight.Bold))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            ShrinkText(text = CityBazaarStrings.cbWorthLine(city.worth), style = typography.micro.copy(color = colors.TextSecondary, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
            ShrinkText(text = CityBazaarStrings.cbRentLine(city.rent), style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
        }
    }
}
