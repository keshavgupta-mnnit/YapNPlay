package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleBarMode
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleGuessResult
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleKingMatchIntent
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleKingMatchState
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText

/**
 * Under the canvas, the user's controls. Drawing: the tools with Pass
 * (solid purple) beside them. Guessing: the guess box with Enter beside it,
 * and the answer to the last guess under them. Nothing between turns.
 */
@Composable
fun DoodleControls(
    state: DoodleKingMatchState,
    guess: String,
    onGuessChange: (String) -> Unit,
    onSubmitGuess: () -> Unit,
    keyboardOwner: Any,
    guessTone: DoodleGuessResult?,
    send: (DoodleKingMatchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens
    val colors = YapNPlayTheme.colors
    val purple = listOf(colors.CtaPurpleStart, colors.CtaPurpleEnd)
    when (state.barMode) {
        DoodleBarMode.DRAW -> Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            DoodleToolBar(
                colour = state.inkColour,
                size = state.inkSize,
                eraser = state.eraser,
                onBrush = { send(DoodleKingMatchIntent.SelectBrush) },
                onInk = { send(DoodleKingMatchIntent.SelectInk(it)) },
                onEraser = { send(DoodleKingMatchIntent.SelectEraser) },
                onUndo = { send(DoodleKingMatchIntent.Undo) },
                onRedo = { send(DoodleKingMatchIntent.Redo) },
                onClear = { send(DoodleKingMatchIntent.Clear) },
            )
            PillButton(
                text = DoodleStrings.dkPass,
                onClick = { send(DoodleKingMatchIntent.Pass) },
                gradient = purple,
                contentColor = colors.TextPrimary,
                height = dimens.doodleToolButton + Spacing.Xs * 2,
                trailingIcon = null,
                modifier = Modifier.width(dimens.doodleEnterWidth),
            )
        }
        DoodleBarMode.GUESS, DoodleBarMode.GUESSED -> Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                DoodleGuessField(
                    value = guess,
                    current = { guess },
                    onValueChange = onGuessChange,
                    pattern = state.hint,
                    tone = guessTone,
                    guessed = state.barMode == DoodleBarMode.GUESSED,
                    owner = keyboardOwner,
                    onSubmit = onSubmitGuess,
                    modifier = Modifier.weight(1f).height(dimens.doodleInputHeight),
                )
                PillButton(
                    text = DoodleStrings.dkEnter,
                    onClick = onSubmitGuess,
                    gradient = purple,
                    contentColor = colors.TextPrimary,
                    enabled = state.barMode == DoodleBarMode.GUESS && guess.isNotBlank(),
                    height = dimens.doodleInputHeight,
                    trailingIcon = null,
                    modifier = Modifier.width(dimens.doodleEnterWidth),
                )
            }
            DoodleGuessFeedback(state)
        }
        DoodleBarMode.WAIT -> Unit
    }
}

/** The answer to the user's last guess, under the box: green right, red wrong or very close. */
@Composable
fun DoodleGuessFeedback(state: DoodleKingMatchState, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val feedback = state.feedback ?: return
    ShrinkText(
        text = feedback.message,
        style = YapNPlayTheme.typography.caption.copy(color = if (feedback.kind == DoodleGuessResult.RIGHT) colors.ReadyGreen else colors.Error),
        modifier = modifier,
    )
}
