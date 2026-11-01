package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleLine
import com.kglabs28.yapnplay.appengine.games.doodleking.identity.DoodleKingConstants
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleBoard

/**
 * The paper everyone sees (4:3, the front-facing board). The drawing is
 * read while drawing only ([DoodleBoard.revision]), so new strokes redraw
 * the paper and nothing else; each line's path is built once and only the
 * line being drawn is rebuilt ([DoodlePathCache]). With [canDraw] the
 * user's finger draws on it.
 */
@Composable
fun DoodleCanvasView(board: DoodleBoard, canDraw: Boolean, modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.doodleCanvasRadius)
    val paths = remember { DoodlePathCache() }
    val input = if (!canDraw) {
        Modifier
    } else {
        Modifier.pointerInput(board) {
            detectDragGestures(
                onDragStart = { at -> board.start(at.x / size.width, at.y / size.height) },
                onDrag = { change, _ -> board.extend(change.position.x / size.width, change.position.y / size.height) },
                onDragEnd = board::finish,
                onDragCancel = board::finish,
            )
        }
    }
    Box(
        modifier = modifier
            .aspectRatio(DoodleKingConstants.CANVAS_ASPECT)
            .clip(shape)
            .background(DoodleInk.PAPER)
            // The same bordered board for the drawer and the guessers.
            .border(dimens.doodleCanvasRim, YapNPlayTheme.colors.Cyan, shape)
            .then(input)
            .drawBehind {
                // Read here, while drawing: a new stroke redraws the paper only.
                board.revision
                paths.paths(board.strokes, size).forEach { (line, path) -> drawLine(line, path) }
            },
    )
}

/** One line: a dot for a single point, else its rounded path. */
private fun DrawScope.drawLine(line: DoodleLine, path: Path) {
    val width = size.width * DoodleInk.WIDTHS.getOrElse(line.size) { DoodleInk.WIDTHS[1] } * if (line.eraser) DoodleInk.ERASER_GROW else 1f
    val colour = if (line.eraser) DoodleInk.PAPER else DoodleInk.colour(line.colour)
    if (line.points.size < 2) return
    if (line.points.size == 2) {
        drawCircle(colour, radius = width / 2, center = Offset(line.points[0] / CANVAS_MAX * size.width, line.points[1] / CANVAS_MAX * size.height))
        return
    }
    drawPath(path, colour, style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private val CANVAS_MAX = DoodleKingConstants.CANVAS_MAX.toFloat()
