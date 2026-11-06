package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** The board's colours (read once while composing, used while drawing), and each seat's colour. */
@Immutable
data class CyberBoardPalette(
    val floor: Color,
    val floorAlt: Color,
    val floorLine: Color,
    val pillarTop: Color,
    val pillarSide: Color,
    val pillarShine: Color,
    val frame: Color,
    val frameGlow: Color,
    val crateFill: Color,
    val crateEdge: Color,
    val crateMark: Color,
    val wall: Color,
    val wallEdge: Color,
    val bombBody: Color,
    val bombShine: Color,
    val spark: Color,
    val fireCore: Color,
    val visor: Color,
    val eye: Color,
    val trim: Color,
    val boot: Color,
    val bootWing: Color,
    val seats: List<Color>,
) {
    fun seat(seat: Int): Color = seats[(seat - 1).coerceAtLeast(0) % seats.size]
}

/** The text drawn on the board (floating points, the boots' tag). */
class CyberBoardText(val measurer: TextMeasurer, val style: TextStyle)

@Composable
fun cyberBoardPalette(seats: List<Color>): CyberBoardPalette {
    val c = YapNPlayTheme.colors
    return CyberBoardPalette(
        floor = c.BlastFloor,
        floorAlt = c.BlastFloorAlt,
        floorLine = c.BlastFloorLine,
        pillarTop = c.BlastPillarTop,
        pillarSide = c.BlastPillarSide,
        pillarShine = c.BlastPillarShine,
        frame = c.BlastFrame,
        frameGlow = c.BlastFrameGlow,
        crateFill = c.BlastCrateFill,
        crateEdge = c.BlastCrateEdge,
        crateMark = c.BlastCrateMark,
        wall = c.BlastWall,
        wallEdge = c.BlastWallEdge,
        bombBody = c.BlastBombBody,
        bombShine = c.BlastBombShine,
        spark = c.BlastSpark,
        fireCore = c.BlastFireCore,
        visor = c.BlastVisor,
        eye = c.BlastEye,
        trim = c.BlastSkinTrim,
        boot = c.BlastBoot,
        bootWing = c.BlastBootWing,
        seats = seats,
    )
}
