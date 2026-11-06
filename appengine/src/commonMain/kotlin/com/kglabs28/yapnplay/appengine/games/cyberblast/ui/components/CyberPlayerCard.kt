package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match.CyberCardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.ArenaSeatSwitches
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardDensity
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard

/**
 * A Cyber Blast seat: the arena card in the player's [tint], their hearts
 * (same colour as their character and their fire) under the name, their
 * points under the pill (or Out / Away / Left). The user's own card has its mic switch beside it.
 */
@Composable
fun CyberPlayerCard(
    card: CyberCardUi,
    tint: Color,
    onToggleHear: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    micOn: Boolean = false,
    onToggleMic: (() -> Unit)? = null,
) {
    TablePlayerCard(
        card = card,
        tint = tint,
        faded = card.faded,
        // Under the pill: Out / Away / Left when it applies, else the points.
        badge = card.tag ?: card.score,
        avatarSize = YapNPlayTheme.dimens.blastCardAvatar,
        density = TableCardDensity.COMPACT,
        details = { CyberHearts(lives = card.lives, max = card.maxLives, colour = tint) },
        onToggleHear = onToggleHear,
        onLongPress = onLongPress,
        sideControls = onToggleMic?.let { toggle ->
            { ArenaSeatSwitches(micOn = micOn, autoplay = false, onToggleMic = toggle, onToggleAutoplay = {}, showAutoplay = false) }
        },
        modifier = modifier,
    )
}
