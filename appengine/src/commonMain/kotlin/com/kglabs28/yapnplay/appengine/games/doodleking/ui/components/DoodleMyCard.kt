package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleCardUi
import com.kglabs28.yapnplay.appengine.ui.components.common.LetterSlots
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardDensity
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard
import com.kglabs28.yapnplay.appengine.ui.components.table.seatPillHeight
import com.kglabs28.yapnplay.appengine.ui.components.table.tableCardAvatar

/**
 * The user's own card, as in the design: their seat card (its mark: brush,
 * tick) with the round mic beside it, and under it the word as letter
 * slots — their word while they draw, blanks (and hints) while they guess,
 * the word in green once they got it. One glass wall holds it all, like the
 * profile's Player Stats panel: its top runs through the middle of the
 * seat, from the avatar's start to the mic's end. The word's row is always
 * kept, so the card never changes height.
 */
@Composable
fun DoodleMyCard(
    card: DoodleCardUi,
    tint: Color,
    word: String,
    wordGuessed: Boolean,
    micOn: Boolean,
    micLocked: Boolean,
    onToggleMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens
    val colors = YapNPlayTheme.colors
    val avatar = dimens.doodleCardAvatar
    // The avatar as the card really draws it, so the wall's cut matches the seat.
    val drawn = tableCardAvatar(avatar)
    // One wall with the seat on it, cut away under the avatar, the pill and the mic.
    Box(
        modifier = modifier.doodleCardWall(
            avatar = drawn,
            pillHeight = seatPillHeight(drawn),
            sideButton = dimens.doodleToolButton,
            gap = Spacing.Xxxs,
            radius = dimens.ludoGlossRadius,
            fill = listOf(colors.ArenaGlassTop, colors.ArenaGlassBottom),
            rim = colors.ArenaGlassRim,
            rimWidth = dimens.hairline,
        ),
    ) {
        Column {
            TablePlayerCard(
                card = card,
                tint = tint,
                badge = card.tag,
                avatarSize = avatar,
                density = TableCardDensity.COMPACT,
                details = { BasicText(text = card.score, style = YapNPlayTheme.typography.caption.copy(color = colors.ArenaCrown)) },
                pillEnd = { DoodleCardMarkIcon(card.mark) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                DoodleMicButton(micOn = micOn, locked = micLocked, onToggle = onToggleMic)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.doodleWordPanelHeight)
                    .padding(horizontal = Spacing.Sm),
            ) {
                if (word.isNotEmpty()) {
                    LetterSlots(
                        pattern = word,
                        letterColor = if (wordGuessed) colors.ReadyGreen else colors.TextPrimary,
                        lineColor = colors.IconMuted,
                        modifier = Modifier.fillMaxSize().padding(bottom = Spacing.Xs),
                    )
                }
            }
        }
    }
}
