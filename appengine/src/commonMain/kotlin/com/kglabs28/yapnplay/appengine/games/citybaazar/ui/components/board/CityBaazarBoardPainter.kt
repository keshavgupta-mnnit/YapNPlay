package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoard
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoardGeometry
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.RupeeFormat
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbTileUi

/**
 * The City Baazar board (design): a raised dark slab, a ring of 36 matte
 * tiles — cities and transit/utility tiles with a coloured header on the
 * inner side, name and price — owned tiles outlined in the owner's neon
 * colour, green houses and a red hotel standing on built cities, and the
 * Shagun Chest and Luck decks in the middle. Drawn far to near.
 */
fun DrawScope.drawCityBaazarBoard(
    p: CityBaazarBoardProjection,
    colours: CityBaazarBoardColours,
    tiles: List<CbTileUi>,
    measurer: TextMeasurer,
    glow: Float,
    highlight: Set<Int> = emptySet(),
) {
    drawSlab(p, colours)
    drawDeck(p, Offset(DECK_CHEST, DECK_CHEST), colours.chest, Strings.cbChestLabel, colours, measurer, glow)
    drawDeck(p, Offset(DECK_LUCK, DECK_LUCK), colours.luck, Strings.cbLuckLabel, colours, measurer, glow)
    tiles.sortedBy { p.project(CityBaazarBoardProjection.tileCentre(it.index)).y }.forEach { drawTile(p, it, colours, measurer, glow, it.index in highlight) }
}

/** Tokens as glossy marbles, far to near, each lifted by its hop. */
fun DrawScope.drawCityBaazarTokens(p: CityBaazarBoardProjection, spots: List<Pair<TokenSpot, Color>>) {
    spots.sortedBy { p.project(it.first.grid).y }.forEach { (spot, tint) ->
        val cell = p.cellSize(spot.grid)
        val ground = p.project(spot.grid)
        val radius = cell * TOKEN_RADIUS
        drawOval(Color.Black.copy(alpha = SHADOW_ALPHA), Offset(ground.x - radius, ground.y - radius * SHADOW_FLAT), androidx.compose.ui.geometry.Size(radius * 2, radius * 2 * SHADOW_FLAT))
        val c = p.project(spot.grid, lift = TOKEN_REST + spot.lift * TOKEN_HOP)
        drawCircle(
            Brush.radialGradient(listOf(lerp(tint, Color.White, TOKEN_SHINE), tint, lerp(tint, Color.Black, TOKEN_DEEP)), Offset(c.x - radius / 3, c.y - radius / 3), radius * 1.4f),
            radius,
            c,
        )
        drawCircle(Color.White.copy(alpha = TOKEN_RIM_ALPHA), radius, c, style = Stroke(radius * TOKEN_RIM))
    }
}

private fun DrawScope.drawSlab(p: CityBaazarBoardProjection, colours: CityBaazarBoardColours) {
    val lo = -CityBaazarBoardProjection.FRAME
    val hi = CityBaazarBoardGeometry.GRID + CityBaazarBoardProjection.FRAME
    val top = quad(p, lo, lo, hi, hi)
    drawPath(Path().apply { addPath(top, Offset(0f, p.slabDepth)) }, colours.slabSide)
    val b = top.getBounds()
    drawPath(top, Brush.verticalGradient(listOf(colours.slabTop, colours.slabBottom), b.top, b.bottom))
    drawPath(top, colours.rim.copy(alpha = RIM_ALPHA), style = Stroke(p.cellSize(Offset(CENTRE, CENTRE)) * RIM_WIDTH))
    drawPath(quad(p, 1f, 1f, CityBaazarBoardGeometry.GRID - 1f, CityBaazarBoardGeometry.GRID - 1f), colours.field)
}

private fun DrawScope.drawTile(p: CityBaazarBoardProjection, tile: CbTileUi, colours: CityBaazarBoardColours, measurer: TextMeasurer, glow: Float, lit: Boolean) {
    val spec = CityBaazarBoard.tile(tile.index)
    val (col, row) = CityBaazarBoardGeometry.cell(tile.index)
    val x0 = col + INSET
    val y0 = row + INSET
    val x1 = col + 1 - INSET
    val y1 = row + 1 - INSET
    val centre = Offset(col + HALF, row + HALF)
    val cell = p.cellSize(centre)
    val top = quad(p, x0, y0, x1, y1)
    val height = cell * TILE_HEIGHT
    drawPath(Path().apply { addPath(top, Offset(0f, height)) }, colours.tileSide)
    val b = top.getBounds()
    drawPath(top, Brush.verticalGradient(listOf(colours.tileEdge, colours.tile), b.top, b.bottom))

    // Coloured header on the inner side (cities, transit, utilities).
    val header = spec.group?.let { colours.groups[it] }
    val (ix, iy) = CityBaazarBoardGeometry.inward(tile.index)
    var body = centre
    if (header != null) {
        val hx0 = if (ix > 0) x1 - HEADER else x0
        val hx1 = if (ix < 0) x0 + HEADER else x1
        val hy0 = if (iy > 0) y1 - HEADER else y0
        val hy1 = if (iy < 0) y0 + HEADER else y1
        drawPath(if (ix != 0) quad(p, hx0, y0, hx1, y1) else quad(p, x0, hy0, x1, hy1), header)
        body = centre - Offset(ix * HEADER / 2, iy * HEADER / 2)
    }

    // Owner: the whole border lights up in their colour.
    tile.ownerColour?.let { owner ->
        val tint = colours.players[owner] ?: colours.text
        drawPath(top, tint.copy(alpha = OWNER_GLOW_ALPHA * glow), style = Stroke(cell * OWNER_GLOW_WIDTH))
        drawPath(top, tint, style = Stroke(cell * OWNER_WIDTH))
        // A dot in the owner's colour on the outer corner: owner and group colours can match.
        val dot = p.project(centre - Offset(ix * DOT_SHIFT, iy * DOT_SHIFT) + Offset(iy * DOT_SIDE, ix * DOT_SIDE))
        drawCircle(tint, cell * DOT_RADIUS, dot)
        drawCircle(Color.White, cell * DOT_RADIUS, dot, style = Stroke(cell * DOT_RIM))
    }
    // Being looked at in a card: the tile glows.
    if (lit) {
        drawPath(top, colours.highlight.copy(alpha = HIGHLIGHT_GLOW_ALPHA * glow), style = Stroke(cell * HIGHLIGHT_GLOW))
        drawPath(top, colours.highlight, style = Stroke(cell * OWNER_WIDTH))
    }

    val name = when (spec.kind) {
        TileKind.START -> Strings.cbCornerStart
        TileKind.JAIL -> Strings.cbCornerJail
        TileKind.CLUB -> Strings.cbCornerClub
        TileKind.REST_HOUSE -> Strings.cbCornerRest
        TileKind.LUCK -> Strings.cbLuckGlyph
        TileKind.CHEST -> Strings.cbChestGlyph
        else -> spec.name
    }
    val corner = spec.kind in CORNERS
    val glyph = spec.kind == TileKind.LUCK || spec.kind == TileKind.CHEST
    val nameColour = when (spec.kind) {
        TileKind.START -> colours.start
        TileKind.JAIL -> colours.jail
        TileKind.LUCK -> colours.luck
        TileKind.CHEST -> colours.chest
        else -> colours.text
    }
    val nameSize = cell * when {
        corner -> CORNER_TEXT
        glyph -> GLYPH_TEXT
        else -> NAME_TEXT
    }
    val price = when {
        spec.buyable && tile.ownerColour == null -> Strings.cbRupees(RupeeFormat.digits(spec.price))
        spec.kind == TileKind.TAX -> Strings.cbRupees(RupeeFormat.digits(spec.tax))
        else -> null
    }
    val at = p.project(body)
    val nameLayout = measurer.measure(
        name,
        TextStyle(color = nameColour, fontSize = nameSize.toSp(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
        maxLines = if (corner) 2 else 1,
    )
    val priceLayout = price?.let {
        measurer.measure(it, TextStyle(color = colours.subText, fontSize = (cell * PRICE_TEXT).toSp(), fontWeight = FontWeight.Medium))
    }
    val totalHeight = nameLayout.size.height + (priceLayout?.size?.height ?: 0)
    val draw: DrawScope.() -> Unit = {
        drawText(nameLayout, topLeft = Offset(-nameLayout.size.width / 2f, -totalHeight / 2f))
        priceLayout?.let { drawText(it, topLeft = Offset(-it.size.width / 2f, -totalHeight / 2f + nameLayout.size.height)) }
    }
    if (corner || glyph) {
        withTransform({ translate(at.x, at.y) }, draw)
    } else {
        // Lay the text on the tile: along the ring, reading left to right, tops toward the far side.
        val along = if (ix != 0) Offset(0f, 1f) else Offset(1f, 0f)
        val inward = Offset(ix.toFloat(), iy.toFloat())
        val alongScreen = p.project(body + along) - at
        val reading = if (alongScreen.x >= 0) alongScreen else -alongScreen
        val upScreen = (p.project(body + inward) - at).let { if (it.y <= 0) it else -it }
        val unit = cell
        val m = Matrix()
        m.values[Matrix.ScaleX] = reading.x / unit
        m.values[Matrix.SkewY] = reading.y / unit
        m.values[Matrix.SkewX] = -upScreen.x / unit
        m.values[Matrix.ScaleY] = -upScreen.y / unit
        m.values[Matrix.TranslateX] = at.x
        m.values[Matrix.TranslateY] = at.y
        withTransform({ transform(m) }, draw)
    }

    drawBuildings(p, tile, centre, ix, iy, colours)
}

/** Up to 3 green houses along the outer edge, or one red hotel. */
private fun DrawScope.drawBuildings(p: CityBaazarBoardProjection, tile: CbTileUi, centre: Offset, ix: Int, iy: Int, colours: CityBaazarBoardColours) {
    if (tile.level == 0) return
    val outer = centre - Offset(ix * OUTER_SHIFT, iy * OUTER_SHIFT)
    if (tile.level >= HOTEL_LEVEL) return drawBlock(p, outer, HOTEL_SIZE, HOTEL_HEIGHT, colours.hotel)
    val along = Offset(iy.toFloat(), ix.toFloat())
    val count = tile.level
    for (i in 0 until count) {
        val shift = (i - (count - 1) / 2f) * HOUSE_SPACING
        drawBlock(p, outer + along * shift, HOUSE_SIZE, HOUSE_HEIGHT, colours.house)
    }
}

private fun DrawScope.drawBlock(p: CityBaazarBoardProjection, at: Offset, size: Float, height: Float, tint: Color) {
    val h = size / 2
    val cell = p.cellSize(at)
    val lift = height
    val top = Path().apply {
        val a = p.project(Offset(at.x - h, at.y - h), lift)
        moveTo(a.x, a.y)
        listOf(Offset(at.x + h, at.y - h), Offset(at.x + h, at.y + h), Offset(at.x - h, at.y + h)).forEach {
            val q = p.project(it, lift)
            lineTo(q.x, q.y)
        }
        close()
    }
    drawPath(Path().apply { addPath(top, Offset(0f, cell * height)) }, lerp(tint, Color.Black, BLOCK_SIDE))
    drawPath(top, Brush.verticalGradient(listOf(lerp(tint, Color.White, BLOCK_SHINE), tint), top.getBounds().top, top.getBounds().bottom))
    drawPath(top, tint.copy(alpha = BLOCK_GLOW_ALPHA), style = Stroke(cell * BLOCK_GLOW))
}

private fun DrawScope.drawDeck(
    p: CityBaazarBoardProjection,
    centre: Offset,
    tint: Color,
    label: String,
    colours: CityBaazarBoardColours,
    measurer: TextMeasurer,
    glow: Float,
) {
    val cell = p.cellSize(centre)
    for (layer in DECK_LAYERS downTo 0) {
        val path = quad(p, centre.x - DECK_HALF, centre.y - DECK_HALF, centre.x + DECK_HALF, centre.y + DECK_HALF, lift = layer * DECK_STEP)
        drawPath(path, lerp(tint, colours.field, if (layer == 0) DECK_TOP_MIX else DECK_LAYER_MIX))
        drawPath(path, tint.copy(alpha = if (layer == 0) 1f else DECK_EDGE_ALPHA), style = Stroke(cell * DECK_EDGE))
    }
    val top = quad(p, centre.x - DECK_HALF, centre.y - DECK_HALF, centre.x + DECK_HALF, centre.y + DECK_HALF)
    drawPath(top, tint.copy(alpha = DECK_GLOW_ALPHA * glow), style = Stroke(cell * DECK_GLOW))
    val at = p.project(centre)
    val layout = measurer.measure(label, TextStyle(color = tint, fontSize = (cell * DECK_TEXT).toSp(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
    drawText(layout, topLeft = Offset(at.x - layout.size.width / 2f, at.y - layout.size.height / 2f))
}

private fun quad(p: CityBaazarBoardProjection, x0: Float, y0: Float, x1: Float, y1: Float, lift: Float = 0f): Path = Path().apply {
    val a = p.project(Offset(x0, y0), lift)
    moveTo(a.x, a.y)
    listOf(Offset(x1, y0), Offset(x1, y1), Offset(x0, y1)).forEach {
        val q = p.project(it, lift)
        lineTo(q.x, q.y)
    }
    close()
}

private val CORNERS = setOf(TileKind.START, TileKind.JAIL, TileKind.CLUB, TileKind.REST_HOUSE)
private const val CENTRE = CityBaazarBoardProjection.CENTRE
private const val HALF = 0.5f
private const val INSET = 0.05f
private const val HEADER = 0.3f
private const val TILE_HEIGHT = 0.12f
private const val RIM_ALPHA = 0.35f
private const val RIM_WIDTH = 0.05f
private const val OWNER_WIDTH = 0.07f
private const val OWNER_GLOW_WIDTH = 0.2f
private const val OWNER_GLOW_ALPHA = 0.45f
private const val NAME_TEXT = 0.17f
private const val CORNER_TEXT = 0.24f
private const val GLYPH_TEXT = 0.45f
private const val PRICE_TEXT = 0.16f
private const val OUTER_SHIFT = 0.28f
private const val DOT_SHIFT = 0.36f
private const val DOT_SIDE = 0.34f
private const val DOT_RADIUS = 0.07f
private const val DOT_RIM = 0.02f
private const val HIGHLIGHT_GLOW = 0.28f
private const val HIGHLIGHT_GLOW_ALPHA = 0.7f
private const val HOUSE_SIZE = 0.16f
private const val HOUSE_HEIGHT = 0.12f
private const val HOUSE_SPACING = 0.22f
private const val HOTEL_SIZE = 0.3f
private const val HOTEL_HEIGHT = 0.28f
private const val BLOCK_SIDE = 0.45f
private const val BLOCK_SHINE = 0.35f
private const val BLOCK_GLOW = 0.04f
private const val BLOCK_GLOW_ALPHA = 0.6f
private const val DECK_CHEST = 3.3f
private const val DECK_LUCK = 6.7f
private const val DECK_HALF = 1.05f
private const val DECK_LAYERS = 4
private const val DECK_STEP = 0.05f
private const val DECK_TOP_MIX = 0.72f
private const val DECK_LAYER_MIX = 0.4f
private const val DECK_EDGE = 0.04f
private const val DECK_EDGE_ALPHA = 0.7f
private const val DECK_GLOW = 0.25f
private const val DECK_GLOW_ALPHA = 0.35f
private const val DECK_TEXT = 0.36f
private const val TOKEN_RADIUS = 0.2f
private const val TOKEN_REST = 0.3f
private const val TOKEN_HOP = 0.9f
private const val TOKEN_SHINE = 0.55f
private const val TOKEN_DEEP = 0.4f
private const val TOKEN_RIM = 0.12f
private const val TOKEN_RIM_ALPHA = 0.5f
private const val SHADOW_ALPHA = 0.4f
private const val SHADOW_FLAT = 0.5f
