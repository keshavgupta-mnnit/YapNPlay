package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.identity.DoodleKingConstants
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleBarMode
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleKingMatchIntent
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleKingMatchState
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileTextField
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardDensity
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard

/**
 * The user's bar, one fixed height. Drawing: their card and mic, the tools,
 * then the word with Pass under it. Guessing: their card and mic, the hint
 * over the guess box, then Enter. While [typing], only the guess box and
 * Enter show (the card and mic come back when the keyboard closes); the box
 * stays the same box, so the keyboard stays open.
 */
@Composable
fun DoodleBottomBar(
    state: DoodleKingMatchState,
    guess: String,
    onGuessChange: (String) -> Unit,
    typing: Boolean,
    seatTint: (Int) -> Color,
    send: (DoodleKingMatchIntent) -> Unit,
    micGranted: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens
    val colors = YapNPlayTheme.colors
    val sendGuess = {
        send(DoodleKingMatchIntent.SendGuess(guess))
        onGuessChange("")
    }
    Row(
        modifier = modifier.fillMaxWidth().height(if (typing) dimens.doodleTypingRowHeight else dimens.doodleBarHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        if (!typing) {
            state.myCard?.let { card ->
                TablePlayerCard(
                    card = card,
                    tint = seatTint(card.seat),
                    badge = card.tag,
                    avatarSize = dimens.doodleCardAvatar,
                    density = TableCardDensity.COMPACT,
                    details = { BasicText(text = card.score, style = YapNPlayTheme.typography.caption.copy(color = colors.ArenaCrown)) },
                    modifier = Modifier.width(dimens.doodleBarCardWidth),
                )
            }
            DoodleMicButton(
                micOn = state.micOn,
                locked = state.micLocked,
                onToggle = { send(DoodleKingMatchIntent.ToggleMic(micGranted())) },
            )
        }
        // The middle: the tools, or the hint over the guess box (always this same box while guessing).
        Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
            when (state.barMode) {
                DoodleBarMode.DRAW -> DoodleToolBar(
                    colour = state.inkColour,
                    size = state.inkSize,
                    eraser = state.eraser,
                    onBrush = { send(DoodleKingMatchIntent.SelectBrush) },
                    onInk = { send(DoodleKingMatchIntent.SelectInk(it)) },
                    onEraser = { send(DoodleKingMatchIntent.SelectEraser) },
                    onUndo = { send(DoodleKingMatchIntent.Undo) },
                    onClear = { send(DoodleKingMatchIntent.Clear) },
                )
                DoodleBarMode.GUESS, DoodleBarMode.CHAT -> Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxs), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (!typing && state.barMode == DoodleBarMode.GUESS) DoodleHintText(hint = state.hint)
                    ProfileTextField(
                        value = guess,
                        onValueChange = onGuessChange,
                        hint = if (state.barMode == DoodleBarMode.CHAT) DoodleStrings.dkChatHint else DoodleStrings.dkGuessHint,
                        maxLength = DoodleKingConstants.MAX_GUESS_LENGTH,
                        isError = false,
                        onDone = sendGuess,
                        keepOpenOnDone = true,
                        modifier = Modifier.height(dimens.doodleInputHeight),
                    )
                }
                DoodleBarMode.WAIT -> if (state.hint.isNotEmpty() && !state.spectating) DoodleHintText(hint = state.hint)
            }
        }
        // The end: the word with Pass under it, or Enter; empty but as wide otherwise, so nothing moves.
        Column(
            modifier = Modifier.width(dimens.doodleBarEndWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            when (state.barMode) {
                DoodleBarMode.DRAW -> {
                    ShrinkText(text = state.word, style = YapNPlayTheme.typography.sectionTitle.copy(color = colors.ArenaCrown))
                    PillButton(
                        text = DoodleStrings.dkPass,
                        onClick = { send(DoodleKingMatchIntent.Pass) },
                        gradient = listOf(colors.CtaPurpleStart, colors.CtaPurpleEnd),
                        contentColor = colors.TextPrimary,
                        trailingIcon = null,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                DoodleBarMode.GUESS, DoodleBarMode.CHAT -> PillButton(
                    text = DoodleStrings.dkEnter,
                    onClick = sendGuess,
                    gradient = listOf(colors.CtaPurpleStart, colors.CtaPurpleEnd),
                    contentColor = colors.TextPrimary,
                    enabled = guess.isNotBlank(),
                    trailingIcon = null,
                    modifier = Modifier.fillMaxWidth(),
                )
                DoodleBarMode.WAIT -> Unit
            }
        }
    }
}
