package com.kglabs28.yapnplay.appengine.ui.components.keyboard

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardCapslock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.FieldClearButton
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton

/**
 * The app's own keyboard (the phone's is never used): on top, what's being
 * typed (or the field's hint) with a caret and ×; then a phone-style
 * QWERTY with Shift (a capital to start each sentence by itself) and backspace, a 123
 * page for digits and symbols, and a wide space bar with Done.
 */
@Composable
fun AppKeyboard(
    request: AppKeyboardRequest,
    onType: (String) -> Unit,
    onDone: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val value = request.value
    var shift by remember(request.owner) { mutableStateOf(false) }
    var numbers by remember(request.owner) { mutableStateOf(false) }
    // A capital to start the text and each sentence (like a phone keyboard), then small letters (unless Shift).
    val upper = shift || startsSentence(value)
    val full = value.length >= request.maxLength
    val caretAlpha by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(AppConstants.CARET_BLINK_MS), RepeatMode.Reverse),
    )
    fun key(c: Char) {
        if (full) return
        onType(value + c)
        shift = false
    }
    Column(
        modifier = modifier
            // Touches on the keys stay here (the shade above closes the keyboard).
            .pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false).consume() } }
            .glassPanel(glowColor = colors.GameZoneAccent, surface = colors.RoomsPanel.copy(alpha = PANEL_ALPHA))
            .padding(Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        // What's typed so far.
        Row(
            modifier = Modifier.fillMaxWidth().height(dimens.appKeyboardPreviewHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .glassPill(glowColor = colors.GameZoneAccent, surface = colors.RoomsInput, shape = RoundedCornerShape(dimens.cbRowRadius))
                    .padding(horizontal = Spacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    if (value.isEmpty()) {
                        Box(Modifier.width(dimens.hairline * 2).height(dimens.iconSmall).graphicsLayer { alpha = caretAlpha }.background(colors.Cyan))
                        BasicText(text = request.hint, style = typography.label.copy(color = colors.RoomsPlaceholderDigits), maxLines = 1)
                    } else {
                        BasicText(text = value, style = typography.label.copy(color = colors.TextPrimary), maxLines = 1, overflow = TextOverflow.StartEllipsis)
                        Box(Modifier.width(dimens.hairline * 2).height(dimens.iconSmall).graphicsLayer { alpha = caretAlpha }.background(colors.Cyan))
                    }
                }
                BasicText(text = Strings.keyboardCount(value.length, request.maxLength), style = typography.micro.copy(color = colors.TextSecondary))
                if (value.isNotEmpty()) FieldClearButton(onClear = { onType("") })
            }
            GlassIconButton(
                imageVector = Icons.Rounded.Close,
                contentDescription = Strings.keyboardClose,
                onClick = onClose,
                size = dimens.smallButtonHeight,
                iconSize = dimens.iconSmall,
                tint = colors.TextPrimary,
            )
        }
        if (numbers) {
            KeyRow { DIGITS.forEach { c -> CharKey(c.toString(), enabled = !full) { key(c) } } }
            KeyRow { SYMBOLS_1.forEach { c -> CharKey(c.toString(), enabled = !full) { key(c) } } }
            KeyRow {
                SYMBOLS_2.forEach { c -> CharKey(c.toString(), enabled = !full) { key(c) } }
                IconKey(Icons.AutoMirrored.Rounded.Backspace, Strings.keyboardDelete, colors.IconMuted, weight = WIDE_KEY) { onType(value.dropLast(1)) }
            }
        } else {
            KeyRow { ROW_1.forEach { c -> CharKey(if (upper) c.uppercase() else c.toString(), enabled = !full) { key(if (upper) c.uppercaseChar() else c) } } }
            KeyRow {
                Spacer(Modifier.weight(HALF_KEY))
                ROW_2.forEach { c -> CharKey(if (upper) c.uppercase() else c.toString(), enabled = !full) { key(if (upper) c.uppercaseChar() else c) } }
                Spacer(Modifier.weight(HALF_KEY))
            }
            KeyRow {
                IconKey(Icons.Rounded.KeyboardCapslock, Strings.keyboardShift, if (shift) colors.Cyan else colors.IconMuted, weight = WIDE_KEY) { shift = !shift }
                ROW_3.forEach { c -> CharKey(if (upper) c.uppercase() else c.toString(), enabled = !full) { key(if (upper) c.uppercaseChar() else c) } }
                IconKey(Icons.AutoMirrored.Rounded.Backspace, Strings.keyboardDelete, colors.IconMuted, weight = WIDE_KEY) { onType(value.dropLast(1)) }
            }
        }
        KeyRow {
            CharKey(if (numbers) Strings.keyboardLetters else Strings.keyboardNumbers, weight = WIDE_KEY) { numbers = !numbers }
            CharKey(",", enabled = !full) { key(',') }
            CharKey(Strings.keyboardSpace, enabled = !full && value.isNotEmpty() && !value.endsWith(' '), weight = SPACE_KEY) { key(' ') }
            CharKey(".", enabled = !full) { key('.') }
            IconKey(Icons.Rounded.Check, Strings.keyboardDone, colors.ReadyGreen, weight = DONE_KEY, label = Strings.keyboardDone, onClick = onDone)
        }
    }
}

@Composable
private fun ColumnScope.KeyRow(content: @Composable RowScope.() -> Unit) {
    Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs), content = content)
}

@Composable
private fun RowScope.CharKey(text: String, enabled: Boolean = true, weight: Float = 1f, onClick: () -> Unit) {
    KeyTile(onClick = onClick, glow = YapNPlayTheme.colors.IconMuted, enabled = enabled, weight = weight) {
        BasicText(text = text, style = YapNPlayTheme.typography.sectionTitle.copy(color = YapNPlayTheme.colors.TextPrimary), maxLines = 1)
    }
}

@Composable
private fun RowScope.IconKey(icon: ImageVector, description: String, tint: Color, weight: Float = 1f, label: String? = null, onClick: () -> Unit) {
    KeyTile(onClick = onClick, glow = tint, weight = weight, modifier = Modifier.semantics { contentDescription = description }) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            AppIcon(imageVector = icon, tint = tint, size = YapNPlayTheme.dimens.iconSmall)
            label?.let { BasicText(text = it, style = YapNPlayTheme.typography.micro.copy(color = tint), maxLines = 1) }
        }
    }
}

@Composable
private fun RowScope.KeyTile(
    onClick: () -> Unit,
    glow: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    weight: Float = 1f,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .weight(weight)
            .fillMaxHeight()
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .glassPill(glowColor = glow, surface = YapNPlayTheme.colors.RoomsSmallButton, shape = RoundedCornerShape(YapNPlayTheme.dimens.rowRadius))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private const val DIGITS = "1234567890"
private const val ROW_1 = "qwertyuiop"
private const val ROW_2 = "asdfghjkl"
private const val ROW_3 = "zxcvbnm"
private const val SYMBOLS_1 = "@#&*-_+()/"
private const val SYMBOLS_2 = "!?:;'\"="
private const val PANEL_ALPHA = 0.96f
private const val DISABLED_ALPHA = 0.35f
private const val WIDE_KEY = 1.5f
private const val SPACE_KEY = 5f
private const val HALF_KEY = 0.5f
private const val DONE_KEY = 2f

/** At the very start, or after a sentence ends ("Hi. ", "Done! ", "Why? "). */
private fun startsSentence(value: String): Boolean {
    val text = value.trimEnd()
    return text.isEmpty() || (value.length > text.length && text.last() in SENTENCE_ENDS)
}

private val SENTENCE_ENDS = setOf('.', '!', '?')
