package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match.CyberCardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.ArenaSeatSwitches
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardDensity
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard

/**
 * A Cyber Blast seat: the arena card in the player's [tint]; in the pill,
 * their points (or Out / Away / Left) under the name and their blast's
 * reach ("3x") at its end; under the pill, their hearts and how many
 * players they have hit, in the same colour as their character and fire.
 * The user's own card has its mic switch beside it.
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
    val colors = YapNPlayTheme.colors
    TablePlayerCard(
        card = card,
        tint = tint,
        faded = card.faded,
        avatarSize = YapNPlayTheme.dimens.blastCardAvatar,
        density = TableCardDensity.COMPACT,
        details = { BasicText(text = card.tag ?: card.score, style = YapNPlayTheme.typography.caption.copy(color = if (card.tag != null) colors.TextSecondary else colors.ArenaCrown), maxLines = 1) },
        pillEnd = { BasicText(text = card.reach, style = YapNPlayTheme.typography.label.copy(color = tint), maxLines = 1) },
        underPill = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                CyberHearts(lives = card.lives, max = card.maxLives, colour = tint)
                CyberKills(kills = card.kills, colour = tint)
            }
        },
        onToggleHear = onToggleHear,
        onLongPress = onLongPress,
        sideControls = onToggleMic?.let { toggle ->
            { ArenaSeatSwitches(micOn = micOn, autoplay = false, onToggleMic = toggle, onToggleAutoplay = {}, showAutoplay = false) }
        },
        modifier = modifier,
    )
}
