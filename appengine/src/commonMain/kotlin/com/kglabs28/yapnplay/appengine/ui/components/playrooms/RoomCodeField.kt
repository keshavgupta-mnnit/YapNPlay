package com.kglabs28.yapnplay.appengine.ui.components.playrooms

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.ui.components.common.FieldClearButton
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Glass pill showing the room code: spaced-out digits, a faint "123456"
 * hint, QR glyph on the right, red rim on error. It never opens the
 * system keyboard: a tap calls [onActivate] and the screen shows its own
 * keypad; while [active] the rim lights up and a caret blinks.
 */
@Composable
fun RoomCodeField(
    code: String,
    isError: Boolean,
    active: Boolean,
    onActivate: () -> Unit,
    modifier: Modifier = Modifier,
    onClear: () -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val textStyle = YapNPlayTheme.typography.title.copy(
        letterSpacing = YapNPlayTheme.typography.title.fontSize / 2,
    )
    val caretAlpha by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(AppConstants.CARET_BLINK_MS), RepeatMode.Reverse),
    )

    Row(
        modifier = modifier
            .height(dimens.codeFieldHeight)
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
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            if (code.isEmpty() && !active) {
                BasicText(
                    text = Strings.roomsCodeHint,
                    style = textStyle.copy(color = colors.RoomsPlaceholderDigits),
                )
            } else {
                BasicText(text = code, style = textStyle.copy(color = colors.TextPrimary))
            }
            if (active) {
                Box(
                    Modifier
                        .graphicsLayer { alpha = caretAlpha }
                        .size(width = dimens.borderThin, height = dimens.iconMedium)
                        .background(colors.GameZoneAccent),
                )
            }
        }
        // While the keypad is open: × empties the code.
        if (active && code.isNotEmpty()) {
            FieldClearButton(onClear = onClear, modifier = Modifier.padding(end = Spacing.Xs))
        }
        AppIcon(
            imageVector = Icons.Rounded.QrCodeScanner,
            tint = colors.IconMuted,
        )
    }
}
