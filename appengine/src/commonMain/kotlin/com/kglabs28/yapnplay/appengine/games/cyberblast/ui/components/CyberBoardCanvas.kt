package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.rememberTextMeasurer
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CyberBlastStrings
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastGrid
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match.CyberBoardFeed

/**
 * The live arena, drawn from [feed] on every screen frame (no
 * recomposition: the canvas reads the feed while drawing). Back to front:
 * the frame, the floor, closed walls, pillars, blockers, boots, bombs,
 * fire, the players (lower ones in front), then bursts and points.
 */
@Composable
fun CyberBoardCanvas(feed: CyberBoardFeed, seatColours: List<Color>, modifier: Modifier = Modifier) {
    val palette = cyberBoardPalette(seatColours)
    val text = CyberBoardText(rememberTextMeasurer(), YapNPlayTheme.typography.caption)
    val pops = CyberBoardText(text.measurer, YapNPlayTheme.typography.label)
    val gain = YapNPlayTheme.colors.CtaGreenStart
    val loss = YapNPlayTheme.colors.Error
    // The screen's frame clock: read only while drawing.
    val tick = remember { mutableLongStateOf(0L) }
    LaunchedEffect(feed) { while (true) withFrameMillis { tick.longValue = it } }
    Canvas(modifier = modifier) {
        tick.longValue
        feed.version.intValue
        val cols = feed.cols
        val rows = feed.rows
        val tile = minOf(size.width / (cols + 2), size.height / (rows + 2))
        val grid = CyberGrid(left = (size.width - cols * tile) / 2, top = (size.height - rows * tile) / 2, tile = tile)
        val now = feed.nowMs()
        val since = feed.sinceFrame(now)
        drawArenaFrame(grid, cols, rows, palette)
        drawFloor(grid, cols, rows, palette)
        feed.walls.forEach { drawClosedWall(grid, BlastGrid.col(it), BlastGrid.row(it), palette) }
        for (r in 0 until rows) for (c in 0 until cols) {
            val t = BlastGrid.of(c, r)
            if (BlastGrid.isPillar(t) && t !in feed.walls) drawPillar(grid, c, r, palette)
        }
        feed.crates.forEach { drawCrate(grid, BlastGrid.col(it), BlastGrid.row(it), palette) }
        val frame = feed.current ?: return@Canvas
        frame.items.forEach { drawBoots(grid, BlastGrid.col(it.tile), BlastGrid.row(it.tile), it.msLeft - since, now, palette, text, CyberBlastStrings.blastBootsTag) }
        frame.bombs.forEach { b ->
            val left = ((b.fuseMs - since).toFloat() / FULL_FUSE_MS).coerceIn(0f, 1f)
            drawBomb(grid, BlastGrid.col(b.tile), BlastGrid.row(b.tile), left, now, palette.seat(b.owner), palette)
        }
        frame.flames.forEach { f ->
            val left = ((f.msLeft - since).toFloat() / FULL_FLAME_MS).coerceIn(0f, 1f)
            drawFlame(grid, BlastGrid.col(f.tile), BlastGrid.row(f.tile), f.shape, left, now, palette.seat(f.owner), palette)
        }
        frame.bodies.filter { !it.out }.map { it to feed.position(it, now) }.sortedBy { it.second.second }.forEach { (body, at) ->
            val look = CyberRunnerLook(
                suit = palette.seat(body.seat),
                facing = body.facing,
                moving = body.moving,
                shielded = body.shieldMs > since,
                boosted = body.boostMs > since,
            )
            drawRunner(grid, at.first, at.second, look, now, palette)
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
}

/** A bomb's pulse and fire's thinning run over these lengths (the default fuse and flame). */
private const val FULL_FUSE_MS = 2_500L
private const val FULL_FLAME_MS = 500L
