package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceIn
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPlayerCardUi

/**
 * A side column of player pods, top down, with [footer] pinned to the
 * bottom. The pods grow with the room they get: fewer players, bigger
 * avatars.
 */
@Composable
fun CityBazaarCardColumn(
    cards: List<CbPlayerCardUi>,
    onToggleHear: (seat: Int) -> Unit,
    onCardClick: (seat: Int) -> Unit,
    modifier: Modifier = Modifier,
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    val dimens = YapNPlayTheme.dimens
    BoxWithConstraints(modifier = modifier.fillMaxHeight()) {
        val count = cards.size.coerceAtLeast(1)
        val room = maxHeight - dimens.cbControlHeight - Spacing.Xs * count
        val avatar = (room / count - dimens.cbCardChrome).coerceIn(dimens.cbCardAvatar, dimens.cbCardAvatarMax)
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            cards.forEach { card ->
                CityBazaarPlayerCard(
                    card = card,
                    avatarSize = avatar,
                    onToggleHear = { onToggleHear(card.seat) },
                    onClick = { onCardClick(card.seat) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.weight(1f))
            footer()
        }
    }
}
