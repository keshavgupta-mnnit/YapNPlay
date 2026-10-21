package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.ui.components.common.FieldClearButton
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill

/**
 * The glass pill showing the friend's ID being typed. It never opens the
 * system keyboard: a tap calls [onActivate] and the screen shows its own
 * keyboard; while [active] the rim lights up and a caret blinks.
 */
@Composable
fun FriendCodeField(
    code: String,
    isError: Boolean,
    active: Boolean,
    onActivate: () -> Unit,
    modifier: Modifier = Modifier,
    onClear: () -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val caretAlpha by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(AppConstants.CARET_BLINK_MS), RepeatMode.Reverse),
    )
    Row(
        modifier = modifier
            .height(dimens.smallButtonHeight)
            .glassPill(
                glowColor = when {
                    isError -> colors.Error
                    active -> colors.GameZoneAccent
                    else -> colors.IconMuted
                },
                surface = colors.RoomsInput,
            )
            .clickable(role = Role.Button, onClick = onActivate)
            .padding(horizontal = Spacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (code.isEmpty() && !active) {
            BasicText(text = ProfileStrings.profileCodeHint, style = typography.label.copy(color = colors.RoomsPlaceholderDigits), maxLines = 1)
        } else {
            BasicText(
                text = code,
                style = typography.title.copy(color = colors.TextPrimary, letterSpacing = typography.title.fontSize / LETTER_SPACING_DIVISOR),
                maxLines = 1,
            )
        }
        if (active) {
            Box(
                Modifier
                    .padding(start = Spacing.Xxxs)
                    .graphicsLayer { alpha = caretAlpha }
                    .size(width = dimens.borderThin, height = dimens.iconMedium)
                    .background(colors.GameZoneAccent),
            )
        }
        // While the keyboard is open: × empties the ID.
        if (active && code.isNotEmpty()) {
            Spacer(Modifier.weight(1f))
            FieldClearButton(onClear = onClear)
        }
    }
}

private const val LETTER_SPACING_DIVISOR = 4
