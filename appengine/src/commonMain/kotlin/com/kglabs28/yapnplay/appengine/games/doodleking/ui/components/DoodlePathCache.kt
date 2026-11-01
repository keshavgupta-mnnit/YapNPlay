package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import com.kglabs28.yapnplay.appengine.games.doodleking.identity.DoodleKingConstants
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleLine

/**
 * Each line's path, built once: a redraw rebuilds only a line that grew
 * (the one being drawn) or every line when the canvas changed size.
 * Lines no longer on the board are dropped.
 */
class DoodlePathCache {
    private class Built(val points: Int, val path: Path)

    private val built = HashMap<DoodleLine, Built>()
    private var size = Size.Zero

    fun paths(lines: List<DoodleLine>, canvas: Size): List<Pair<DoodleLine, Path>> {
        if (canvas != size) {
            built.clear()
            size = canvas
        }
        if (built.size > lines.size) built.keys.retainAll(lines.toSet())
        return lines.map { line ->
            val cached = built[line]?.takeIf { it.points == line.points.size }
            line to (cached ?: Built(line.points.size, build(line.points, canvas)).also { built[line] = it }).path
        }
    }

    private fun build(points: List<Int>, canvas: Size): Path = Path().apply {
        moveTo(x(points[0], canvas), y(points[1], canvas))
        for (i in 2 until points.size step 2) lineTo(x(points[i], canvas), y(points[i + 1], canvas))
    }

    private fun x(v: Int, canvas: Size) = v / CANVAS_MAX * canvas.width
    private fun y(v: Int, canvas: Size) = v / CANVAS_MAX * canvas.height

    private companion object {
        val CANVAS_MAX = DoodleKingConstants.CANVAS_MAX.toFloat()
    }
}
