package com.kglabs28.yapnplay.appengine.ui.components.profile

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

/**
 * The design's glass input: a rounded dark field with a soft rim (green
 * while typing, red on error) and a faint hint. A tap opens the app's own
 * keyboard (never the phone's); a caret blinks and × empties it meanwhile.
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
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary)
    val keyboard = LocalAppKeyboard.current
    val owner = remember { Any() }
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
            .height(dimens.ctaHeight)
            .glassPill(
                glowColor = when {
                    isError -> colors.Error
                    active -> colors.GameZoneAccent
                    else -> colors.IconMuted
                },
                surface = colors.RoomsInput,
                shape = RoundedCornerShape(dimens.cbRowRadius),
            )
            .clickable(role = Role.Button) {
                keyboard.open(AppKeyboardRequest(owner, hint, value, maxLength, { change(it) }, { done() }))
            }
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            if (value.isEmpty()) {
                if (active) Box(Modifier.width(dimens.hairline * 2).height(dimens.iconSmall).graphicsLayer { alpha = caretAlpha }.background(colors.Cyan))
                BasicText(text = hint, style = style.copy(color = colors.RoomsPlaceholderDigits), maxLines = 1, overflow = TextOverflow.Ellipsis)
            } else {
                BasicText(text = value, style = style, maxLines = 1, overflow = TextOverflow.StartEllipsis, modifier = Modifier.weight(1f, fill = false))
                if (active) Box(Modifier.width(dimens.hairline * 2).height(dimens.iconSmall).graphicsLayer { alpha = caretAlpha }.background(colors.Cyan))
            }
        }
        // × empties the field.
        if (value.isNotEmpty()) FieldClearButton(onClear = { change("") })
    }
}
