package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleCardUi
import com.kglabs28.yapnplay.appengine.ui.components.common.LetterSlots
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardDensity
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard

/**
 * The user's own card, as in the design: one glass box, starting under
 * the avatar's centre, holding their seat card (its mark: brush, tick) and
 * under it the word as letter slots — their word while they draw, blanks
 * (and hints) while they guess, the word in green once they got it. The
 * mic is the round button beside it.
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
    val shape = RoundedCornerShape(dimens.ludoGlossRadius)
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(dimens.doodleMicGap)) {
        Box(modifier = Modifier.weight(1f)) {
            // The box: from the avatar's centre to the end, round the card and the word.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(start = avatar / 2)
                    .clip(shape)
                    .background(Brush.verticalGradient(listOf(colors.ArenaGlassTop, colors.ArenaGlassBottom)))
                    .border(dimens.hairline, colors.ArenaGlassRim, shape),
            )
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
                )
                if (word.isNotEmpty()) {
                    LetterSlots(
                        pattern = word,
                        letterColor = if (wordGuessed) colors.ReadyGreen else colors.TextPrimary,
                        lineColor = colors.IconMuted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimens.doodleWordPanelHeight)
                            .padding(start = avatar / 2 + Spacing.Sm, end = Spacing.Sm, bottom = Spacing.Xxs),
                    )
                }
            }
        }
        // The round mic, level with the avatar.
        DoodleMicButton(micOn = micOn, locked = micLocked, onToggle = onToggleMic, modifier = Modifier.padding(top = (avatar - dimens.doodleToolButton) / 2).align(Alignment.Top))
    }
}
