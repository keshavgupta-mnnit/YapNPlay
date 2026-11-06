package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match.CyberCameraState
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CyberBlastStrings
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastGrid
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match.CyberBoardFeed
import com.kglabs28.yapnplay.appengine.games.cyberblast.utils.CyberCamera
import com.kglabs28.yapnplay.appengine.games.cyberblast.utils.CyberFlameTips

/**
 * The live arena, drawn from [feed] on every screen frame (no
 * recomposition: the canvas reads the feed while drawing). On a Big Arena
 * the screen shows a window that follows the user's character. Back to
 * front: the frame, the floor, closed walls, pillars, blockers, boots,
 * bombs, fire, the players (lower ones in front; the user's glowing), then
 * bursts and points.
 */
@Composable
fun CyberBoardCanvas(feed: CyberBoardFeed, camera: CyberCameraState, seatColours: List<Color>, modifier: Modifier = Modifier) {
    val palette = cyberBoardPalette(seatColours)
    val text = CyberBoardText(rememberTextMeasurer(), YapNPlayTheme.typography.caption)
    val pops = CyberBoardText(text.measurer, YapNPlayTheme.typography.label)
    val gain = YapNPlayTheme.colors.CtaGreenStart
    val loss = YapNPlayTheme.colors.Error
    // The screen's frame clock: read only while drawing.
    val tick = remember { mutableLongStateOf(0L) }
    LaunchedEffect(feed) { while (true) withFrameMillis { tick.longValue = it } }
    val barColour = YapNPlayTheme.colors.BlastPadRim
    val thumbColour = YapNPlayTheme.colors.BlastPadEdge
    // Dragging the board moves the arena with the finger; dragging along a bar moves its thumb.
    var barDrag: Boolean? = null
    val drag = Modifier.pointerInput(camera) {
        detectDragGestures(
            onDragStart = { at ->
                val strip = camera.tilePx * BAR_GRAB
                barDrag = when {
                    at.y > size.height - strip -> true
                    at.x > size.width - strip -> false
                    else -> null
                }
            },
            onDragEnd = { barDrag = null },
        ) { change, amount ->
            change.consume()
            when (barDrag) {
                true -> camera.dragBar(horizontal = true, deltaPx = amount.x)
                false -> camera.dragBar(horizontal = false, deltaPx = amount.y)
                null -> camera.dragBoard(amount.x, amount.y)
            }
        }
    }
    Canvas(modifier = modifier.then(drag)) {
        tick.longValue
        feed.version.intValue
        val cols = feed.cols
        val rows = feed.rows
        val now = feed.nowMs()
        val since = feed.sinceFrame(now)
        val frame = feed.current
        val me = feed.mySeat?.let { seat -> frame?.body(seat)?.takeIf { !it.out } }
        val view = CyberCamera.view(cols, rows, size.width, size.height)
        // Where the user scrolled to, else on their character (a spectator: the middle).
        val window = CyberCamera.window(cols, rows, view, camera.panned.value ?: me?.let { feed.position(it, now) })
        val tile = view.tile
        camera.window = window
        camera.tilePx = tile
        camera.boardCols = cols
        camera.boardRows = rows
        val viewLeft = (size.width - window.cols * tile) / 2
        val viewTop = (size.height - window.rows * tile) / 2
        val grid = CyberGrid(left = viewLeft - window.left * tile, top = viewTop - window.top * tile, tile = tile)
        // A whole board shows with its frame; a window onto a Big Arena shows only what is inside it (its slim border comes last).
        val windowed = window.cols < cols || window.rows < rows
        val pad = if (windowed) 0f else tile
        val padLeft = pad
        val padTop = pad
        val padRight = pad
        val padBottom = pad
        clipRect(viewLeft - padLeft, viewTop - padTop, viewLeft + window.cols * tile + padRight, viewTop + window.rows * tile + padBottom) {
            drawArenaFrame(grid, cols, rows, palette)
            drawFloor(grid, cols, rows, palette)
            feed.walls.forEach { drawClosedWall(grid, BlastGrid.col(it), BlastGrid.row(it), palette) }
            for (r in 0 until rows) for (c in 0 until cols) {
                val t = BlastGrid.of(c, r)
                if (BlastGrid.isPillar(t) && t !in feed.walls) drawPillar(grid, c, r, palette)
            }
            feed.crates.sortedBy { it }.forEach { drawCrate(grid, BlastGrid.col(it), BlastGrid.row(it), palette) }
            if (frame == null) return@clipRect
            frame.items.forEach { drawBoots(grid, BlastGrid.col(it.tile), BlastGrid.row(it.tile), it.msLeft - since, now, palette, text, CyberBlastStrings.blastBootsTag) }
            frame.bombs.forEach { b ->
                val left = ((b.fuseMs - since).toFloat() / FULL_FUSE_MS).coerceIn(0f, 1f)
                drawBomb(grid, BlastGrid.col(b.tile), BlastGrid.row(b.tile), left, now, palette.seat(b.owner), palette)
            }
            val burning = frame.flames.map { it.tile }.toSet()
            frame.flames.forEach { f ->
                val left = ((f.msLeft - since).toFloat() / FULL_FLAME_MS).coerceIn(0f, 1f)
                val tip = CyberFlameTips.side(f.tile, f.shape, burning)
                drawFire(grid, f.tile, BlastGrid.col(f.tile), BlastGrid.row(f.tile), f.shape, tip, left, now, palette.seat(f.owner), palette)
            }
            frame.bodies.filter { !it.out }.map { it to feed.position(it, now) }.sortedBy { it.second.second }.forEach { (body, at) ->
                val colour = palette.seat(body.seat)
                if (body.seat == me?.seat) drawYouGlow(grid, at.first, at.second, now, colour, palette)
                val look = CyberRunnerLook(suit = colour, facing = body.facing, moving = body.moving, shielded = body.shieldMs > since, boosted = body.boostMs > since)
                drawRunner(grid, at.first, at.second, look, now, palette)
                if (body.seat == me?.seat) drawYouMarker(grid, at.first, at.second, now, colour, palette)
            }
            feed.bursts.forEach { burst ->
                val age = ((now - burst.atMs).toFloat() / CyberBoardFeed.EFFECT_MS).coerceIn(0f, 1f)
                if (age >= 1f) return@forEach
                val c = BlastGrid.col(burst.tile)
                val r = BlastGrid.row(burst.tile)
                if (burst.boom) drawBoomRing(grid, c, r, age, palette.fireCore) else drawShatter(grid, c, r, age, palette)
            }
            feed.pops.forEach { pop ->
                val age = ((now - pop.atMs).toFloat() / CyberBoardFeed.EFFECT_MS).coerceIn(0f, 1f)
                if (age < 1f && pop.points != 0) drawPop(grid, pop.x, pop.y, CyberBlastStrings.blastPoints(pop.points), age, if (pop.points > 0) gain else loss, pops)
            }
        }
        // A window onto a Big Arena: a slim frame on all four sides with its neon edge, the scroll bars inside it.
        if (windowed) {
            val border = tile * CyberCamera.WINDOW_BORDER
            val outer = Offset(viewLeft - border / 2, viewTop - border / 2)
            val outerSize = Size(window.cols * tile + border, window.rows * tile + border)
            drawRoundRect(palette.frame, outer, outerSize, CornerRadius(border), style = Stroke(border))
            drawRoundRect(palette.frameGlow.copy(alpha = WINDOW_GLOW), outer - Offset(border / 2, border / 2), Size(outerSize.width + border, outerSize.height + border), CornerRadius(border * 1.5f), style = Stroke(tile * WINDOW_EDGE))
            drawRoundRect(palette.frameGlow, Offset(viewLeft, viewTop), Size(window.cols * tile, window.rows * tile), CornerRadius(tile * WINDOW_ROUND), style = Stroke(tile * WINDOW_EDGE))
            drawScrollBars(viewLeft, viewTop, window, cols, rows, tile, barColour, thumbColour)
        }
    }
}

/** A bomb's pulse and fire's thinning run over these lengths (the default fuse and flame). */
private const val FULL_FUSE_MS = 2_500L
private const val FULL_FLAME_MS = 500L
private const val WINDOW_ROUND = 0.15f
private const val WINDOW_EDGE = 0.06f
private const val WINDOW_GLOW = 0.5f
/** A drag starting this many tiles from the bottom / right edge moves that scroll bar. */
private const val BAR_GRAB = 0.7f
