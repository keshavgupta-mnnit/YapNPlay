package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.runtime.collectAsState
import org.koin.compose.koinInject
import com.kglabs28.yapnplay.appengine.core.common.KeyboardPreference
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.FieldClearButton
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.AppKeyboardRequest
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.LocalAppKeyboard
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * The design's glass input: a rounded dark field with a soft rim (green
 * while typing, red on error) and a faint hint. A tap opens the app's own
 * keyboard; a caret blinks and × empties it meanwhile. With the phone's
 * keyboard chosen in Settings it is a real text box instead ([SystemFieldInput]).
 * With [lines] above 1 it is a taller box whose text wraps (a message).
 */
@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    maxLength: Int,
    isError: Boolean,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    lines: Int = 1,
    keyboardChoice: KeyboardPreference = koinInject(),
) {
    val system by keyboardChoice.useSystem.collectAsState()
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary)
    val keyboard = LocalAppKeyboard.current
    val owner = remember { Any() }
    /** Where this field's bottom edge is on screen (px), for the keyboard. */
    var bottom by remember { mutableFloatStateOf(0f) }
    val active = keyboard.isOpenFor(owner)
    val change by rememberUpdatedState(onValueChange)
    val done by rememberUpdatedState(onDone)
    LaunchedEffect(value, active) { if (active) keyboard.sync(owner, value) }
    DisposableEffect(owner) { onDispose { keyboard.close(owner) } }
    val caretAlpha by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(AppConstants.CARET_BLINK_MS), RepeatMode.Reverse),
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (lines == 1) dimens.ctaHeight else dimens.ctaHeight * lines * LINE_SHARE)
            .glassPill(
                glowColor = when {
                    isError -> colors.Error
                    active -> colors.GameZoneAccent
                    else -> colors.IconMuted
                },
                surface = colors.RoomsInput,
                shape = RoundedCornerShape(dimens.cbRowRadius),
            )
            // The app's keyboard: told where this field is, so the screen can lift it clear of the keys.
            .then(
                if (system) {
                    Modifier
                } else {
                    Modifier
                        .onGloballyPositioned {
                            bottom = it.boundsInRoot().bottom
                            keyboard.place(owner, bottom)
                        }
                        .clickable(role = Role.Button) {
                            keyboard.open(AppKeyboardRequest(owner, hint, value, maxLength, { change(it) }, { done() }))
                            keyboard.place(owner, bottom)
                        }
                },
            )
            .padding(horizontal = Spacing.Sm, vertical = if (lines == 1) dimens.zero else Spacing.Xs),
        verticalAlignment = if (lines == 1) Alignment.CenterVertically else Alignment.Top,
    ) {
        if (system) {
            SystemFieldInput(value, { change(it) }, hint, maxLength, lines, style, { done() }, Modifier.weight(1f))
        } else Row(modifier = Modifier.weight(1f), verticalAlignment = if (lines == 1) Alignment.CenterVertically else Alignment.Bottom) {
            if (value.isEmpty()) {
                if (active) Box(Modifier.width(dimens.hairline * 2).height(dimens.iconSmall).graphicsLayer { alpha = caretAlpha }.background(colors.Cyan))
                ShrinkText(text = hint, style = style.copy(color = colors.RoomsPlaceholderDigits))
            } else {
                // One line keeps the end in view; a taller box wraps the message.
                if (lines == 1) {
                    BasicText(text = value, style = style, maxLines = 1, overflow = TextOverflow.StartEllipsis, modifier = Modifier.weight(1f, fill = false))
                } else {
                    BasicText(text = value, style = style, maxLines = lines, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                }
                if (active) Box(Modifier.width(dimens.hairline * 2).height(dimens.iconSmall).graphicsLayer { alpha = caretAlpha }.background(colors.Cyan))
            }
        }
        // × empties the field.
        if (value.isNotEmpty()) FieldClearButton(onClear = { change("") })
    }
}

/** A taller box is this much of the one-line height per line (lines sit closer than fields). */
private const val LINE_SHARE = 0.6f
