package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.kglabs28.yapnplay.appengine.ui.components.common.LetterSlots
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.AppKeyboardController
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.AppKeyboardRequest
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.LocalAppKeyboard
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileTextField
import org.koin.compose.koinInject

/**
 * The guess box: the word's letter slots ([pattern]); the hint's letters
 * show in their places and what the user types fills the others, in capitals. Its rim tells the last answer: green right, red wrong or
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
    modifier: Modifier = Modifier,
    keyboardChoice: KeyboardPreference = koinInject(),
) {
    val colors = YapNPlayTheme.colors
    val keyboard = LocalAppKeyboard.current
    val system by keyboardChoice.useSystem.collectAsState()
    // The phone's keyboard types the whole word; the app's types only the hidden letters (the hint's stay put).
    val letters = (if (system) pattern.count { it == '_' || it.isLetterOrDigit() } else DoodleGuessSlots.blanks(pattern)).coerceAtLeast(1)
    val change: (String) -> Unit = { onValueChange(it.filter(Char::isLetterOrDigit).take(letters)) }
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
    Box(
        modifier = modifier
            .glassPill(glowColor = rim, surface = colors.RoomsInput, shape = RoundedCornerShape(YapNPlayTheme.dimens.cbRowRadius))
            .then(if (guessed) Modifier else Modifier.clickable(role = Role.Button) { open(keyboard, owner, pattern, letters, { read() }, change) { submit() } })
            .padding(horizontal = Spacing.Sm),
        contentAlignment = Alignment.CenterStart,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
        // Empty and nothing given yet: the plain prompt; otherwise the word's slots (hint letters in place).
        val prompt = value.isEmpty() && pattern.none { it != '_' && it.isLetterOrDigit() }
        if (prompt) {
            BasicText(
                text = DoodleStrings.dkGuessHint,
                style = YapNPlayTheme.typography.label.copy(color = colors.RoomsPlaceholderDigits),
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
        } else {
            LetterSlots(
                pattern = pattern,
                typed = value,
                letterColor = when {
                    guessed || tone == DoodleGuessResult.RIGHT -> colors.ReadyGreen
                    tone != null -> colors.Error
                    else -> colors.TextPrimary
                },
                modifier = Modifier.weight(1f),
            )
        }
        AppIcon(imageVector = Icons.Rounded.Keyboard, tint = colors.IconMuted, size = YapNPlayTheme.dimens.iconMedium)
      }
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
