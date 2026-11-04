package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp

/**
 * The glass wall round the user's Doodle card, drawn as one piece with the
 * seat on it (like the profile's Player Stats panel and its title pill):
 * its top runs through the seat's middle line, from the avatar's start to
 * the side button's end, and the wall is cut away under the avatar, the
 * name pill and the button, so neither its fill nor its rim shows through
 * them — their own rims carry the outline on.
 */
fun Modifier.doodleCardWall(
    avatar: Dp,
    pillHeight: Dp,
    /** The round button beside the seat (the mic). */
    sideButton: Dp,
    /** The gap between the seat and that button. */
    gap: Dp,
    radius: Dp,
    fill: List<Color>,
    rim: Color,
    rimWidth: Dp,
): Modifier = drawBehind {
    val a = avatar.toPx()
    val middle = a / 2
    val pill = pillHeight.toPx()
    val button = sideButton.toPx()
    val wall = Path().apply {
        addRoundRect(RoundRect(Rect(0f, middle, size.width, size.height), CornerRadius(radius.toPx())))
    }
    // The seat's own shapes: the avatar, the name pill (from the avatar's centre) and the button.
    val seat = Path().apply {
        addOval(Rect(0f, 0f, a, a))
        addRoundRect(RoundRect(Rect(middle, middle - pill / 2, size.width - button - gap.toPx(), middle + pill / 2), CornerRadius(pill / 2)))
        addOval(Rect(size.width - button, middle - button / 2, size.width, middle + button / 2))
    }
    clipPath(seat, ClipOp.Difference) {
        drawPath(wall, Brush.verticalGradient(fill))
        drawPath(wall, rim, style = Stroke(rimWidth.toPx()))
    }
}
