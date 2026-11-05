package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.clickable
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.common.KeyboardPreference
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleGuessResult
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.AppKeyboardController
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.AppKeyboardRequest
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.LocalAppKeyboard
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileTextField
import org.koin.compose.koinInject

/**
 * The guess box: one plain box all turn, the prompt with a keyboard icon (what
 * is typed, in capitals, once they type; the last answer in its colour just after
 * a guess). The word's letter slots live under the user's card and on the
 * keyboard: there the hint's letters show in their places and what's typed fills the others. Its rim tells the last answer: green right, red wrong or
 * very close ([tone]); green and closed for typing once guessed. A tap opens
 * the app's keyboard (short, showing the same slots), which stays open
 * guess after guess. [owner] is the table's, so the keyboard stays open
 * while the table folds into its typing strip. With the phone's keyboard
 * chosen in Settings it is a plain text box.
 */
@Composable
fun DoodleGuessField(
    value: String,
    /** The text now, read when the keyboard opens again after Done (the box may be off screen by then). */
    current: () -> String,
    onValueChange: (String) -> Unit,
    pattern: String,
    tone: DoodleGuessResult?,
    guessed: Boolean,
    owner: Any,
    onSubmit: () -> Unit,
    /** The last letter is in (every hidden letter filled): send it at once, with the text as it now is. */
    onComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    /** The last answer, shown in the empty box in its colour (the table's own words). */
    prompt: String = DoodleStrings.dkGuessHint,
    keyboardChoice: KeyboardPreference = koinInject(),
) {
    val colors = YapNPlayTheme.colors
    val keyboard = LocalAppKeyboard.current
    val system by keyboardChoice.useSystem.collectAsState()
    // The phone's keyboard types the whole word; the app's types only the hidden letters (the hint's stay put).
    val letters = (if (system) pattern.count { it == '_' || it.isLetterOrDigit() } else DoodleGuessSlots.blanks(pattern)).coerceAtLeast(1)
    val change: (String) -> Unit = { raw ->
        val text = raw.filter(Char::isLetterOrDigit).take(letters)
        onValueChange(text)
        if (!guessed && text.length >= letters) onComplete(text)
    }
    if (system) {
        ProfileTextField(
            value = value,
            onValueChange = change,
            hint = DoodleStrings.dkGuessHint,
            maxLength = letters,
            isError = tone == DoodleGuessResult.WRONG || tone == DoodleGuessResult.CLOSE,
            onDone = onSubmit,
            keepOpenOnDone = true,
            modifier = modifier,
        )
        return
    }
    val submit by rememberUpdatedState(onSubmit)
    val read by rememberUpdatedState(current)
    val rim = when {
        guessed || tone == DoodleGuessResult.RIGHT -> colors.ReadyGreen
        tone != null -> colors.Error
        keyboard.isOpenFor(owner) -> colors.GameZoneAccent
        else -> colors.IconMuted
    }
    // The same plain box all turn (the word's slots are under the user's card and on the keyboard): what's typed,
    // or the prompt, or the last answer while it's empty, with the keyboard icon at the end.
    val answer = tone != null && value.isEmpty()
    // Hint letters given: the box shows the whole word (them in place, what's typed in the rest).
    val given = pattern.any { it != '_' && it.isLetterOrDigit() }
    val shown = DoodleGuessSlots.display(pattern, value)
    Row(
        modifier = modifier
            .glassPill(glowColor = rim, surface = colors.RoomsInput, shape = RoundedCornerShape(YapNPlayTheme.dimens.cbRowRadius))
            .then(if (guessed) Modifier else Modifier.clickable(role = Role.Button) { open(keyboard, owner, pattern, letters, { read() }, change) { submit() } })
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        BasicText(
            text = when {
                value.isNotEmpty() || given -> shown
                answer -> prompt
                else -> DoodleStrings.dkGuessHint
            },
            style = YapNPlayTheme.typography.label.copy(
                color = when {
                    value.isNotEmpty() || given -> colors.TextPrimary
                    answer -> rim
                    else -> colors.RoomsPlaceholderDigits
                },
            ),
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        AppIcon(imageVector = Icons.Rounded.Keyboard, tint = colors.IconMuted, size = YapNPlayTheme.dimens.iconMedium)
    }
}

/** Opens the short keyboard on the guess; after each Done it opens again on what's typed. */
private fun open(keyboard: AppKeyboardController, owner: Any, pattern: String, letters: Int, value: () -> String, change: (String) -> Unit, submit: () -> Unit) {
    keyboard.open(
        AppKeyboardRequest(
            owner = owner,
            hint = DoodleStrings.dkGuessHint,
            value = value(),
            maxLength = letters,
            onChange = change,
            onDone = {
                submit()
                open(keyboard, owner, pattern, letters, value, change, submit)
            },
            slots = pattern,
            compact = true,
        ),
    )
    // The table makes its own room above the keys: nothing to lift.
    keyboard.place(owner, 0f)
}
