package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.AutoFixNormal
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/**
 * The drawer's tools, left to right: the brush (its dot shows the size; a
 * tap on it again steps the size), the 5 colours, the eraser, undo and
 * clear all. The tool in hand is lit.
 */
@Composable
fun DoodleToolBar(
    colour: Int,
    size: Int,
    eraser: Boolean,
    onBrush: () -> Unit,
    onInk: (Int) -> Unit,
    onEraser: () -> Unit,
    onUndo: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val button = dimens.doodleToolButton
    Row(
        modifier = modifier.arenaGlass(RoundedCornerShape(dimens.ludoGlossRadius)).padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            ToolButton(Icons.Rounded.Brush, DoodleStrings.dkBrush, lit = !eraser, size = button, onClick = onBrush)
            // The brush's size, as a dot in its corner.
            Box(
                modifier = Modifier
                    .size(dimens.doodleInkDot * (SIZE_DOT_BASE + size * SIZE_DOT_STEP))
                    .background(DoodleInk.colour(colour), CircleShape)
                    .border(dimens.hairline, colors.TextPrimary, CircleShape),
            )
        }
        DoodleInk.COLOURS.indices.forEach { index ->
            val chosen = !eraser && index == colour
            Box(
                modifier = Modifier
                    .size(dimens.doodleInkDot)
                    .background(DoodleInk.colour(index), CircleShape)
                    .border(if (chosen) dimens.doodleCanvasRim else dimens.hairline, if (chosen) colors.TextPrimary else colors.GlassShine, CircleShape)
                    .clickable(role = Role.Button, onClickLabel = DoodleStrings.dkInkNames[index]) { onInk(index) },
            )
        }
        ToolButton(Icons.Rounded.AutoFixNormal, DoodleStrings.dkEraser, lit = eraser, size = button, onClick = onEraser)
        ToolButton(Icons.AutoMirrored.Rounded.Undo, DoodleStrings.dkUndo, lit = false, size = button, onClick = onUndo)
        ToolButton(Icons.Rounded.DeleteSweep, DoodleStrings.dkClear, lit = false, size = button, onClick = onClear)
    }
}

@Composable
private fun ToolButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, lit: Boolean, size: Dp, onClick: () -> Unit) {
    val colors = YapNPlayTheme.colors
    GlassIconButton(
        imageVector = icon,
        contentDescription = label,
        onClick = onClick,
        size = size,
        iconSize = size * ICON_SHARE,
        tint = if (lit) colors.Cyan else colors.IconMuted,
        glowColor = if (lit) colors.Cyan else colors.IconMuted,
    )
}

/** The brush's size dot: this share of a colour dot, and this much more per size step. */
private const val SIZE_DOT_BASE = 0.3f
private const val SIZE_DOT_STEP = 0.15f
private const val ICON_SHARE = 0.6f
