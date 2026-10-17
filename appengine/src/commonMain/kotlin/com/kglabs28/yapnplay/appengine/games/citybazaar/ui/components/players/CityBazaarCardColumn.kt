package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceIn
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPlayerCardUi

/**
 * A side column of player pods, top down. The column's height is shared
 * between them: fewer players get bigger avatars, many players get compact
 * pods (and, if even those don't fit, the column scrolls).
 */
@Composable
fun CityBazaarCardColumn(
    cards: List<CbPlayerCardUi>,
    onToggleHear: (seat: Int) -> Unit,
    onCardClick: (seat: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens
    BoxWithConstraints(modifier = modifier.fillMaxHeight()) {
        val count = cards.size.coerceAtLeast(1)
        val roomy = (maxHeight - Spacing.Xs * (count - 1)) / count
        val compact = roomy < dimens.cbCardCompactBelow
        val gap = if (compact) Spacing.Xxs else Spacing.Xs
        val slot = (maxHeight - gap * (count - 1)) / count
        val chrome = if (compact) dimens.cbCardChromeCompact else dimens.cbCardChrome
        val avatar = (slot - chrome).coerceIn(dimens.cbCardAvatarMin, dimens.cbCardAvatarMax)
        Column(
            modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            cards.forEach { card ->
                CityBazaarPlayerCard(
                    card = card,
                    avatarSize = avatar,
                    compact = compact,
                    onToggleHear = { onToggleHear(card.seat) },
                    onClick = { onCardClick(card.seat) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
