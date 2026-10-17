package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarBoard
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarBoardGeometry
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.RupeeFormat
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTileUi

/**
 * The City Bazaar board in 3D (design): a raised dark slab and a ring of 36
 * matte tiles. Cities, transit and utilities carry their colour header on
 * the inner side, the name laid along the tile and the price; an owned tile
 * lights its outer edge in the owner's colour. Houses are green blocks with
 * roofs, a hotel a tall red tower; Shagun Chest (₹) and Luck (?) piles sit in
 * the middle. Drawn far to near.
 */
fun DrawScope.drawCityBazaarIsoBoard(
    p: CityBazaarIsoProjection,
    colours: CityBazaarBoardColours,
    tiles: List<CbTileUi>,
    measurer: TextMeasurer,
    glow: Float,
    highlight: Set<Int> = emptySet(),
) {
    drawSlab(p, colours)
    drawDeck(p, CardDeck.CHEST, colours.chest, Strings.cbChestGlyph, Strings.cbChestLabel, colours, measurer, glow)
    drawDeck(p, CardDeck.LUCK, colours.luck, Strings.cbLuckGlyph, Strings.cbLuckLabel, colours, measurer, glow)
    tiles.sortedBy { p.project(CityBazaarIsoProjection.tileCentre(it.index)).y }.forEach { drawTile(p, it, colours, measurer, glow, it.index in highlight) }
}

/** Tokens as pawns (a disc, a cone and a glossy head), far to near, each lifted by its hop. */
fun DrawScope.drawCityBazaarIsoTokens(p: CityBazaarIsoProjection, spots: List<Pair<TokenSpot, Color>>) {
    spots.sortedBy { p.project(it.first.grid).y }.forEach { (spot, tint) ->
        val cell = p.cellSize(spot.grid)
        val ground = p.project(spot.grid)
        val base = cell * PAWN_BASE
        drawOval(Color.Black.copy(alpha = SHADOW_ALPHA), Offset(ground.x - base, ground.y - base * SHADOW_FLAT), Size(base * 2, base * 2 * SHADOW_FLAT))
        val lift = spot.lift * PAWN_HOP
        val foot = p.project(spot.grid, lift = lift)
        val neck = p.project(spot.grid, lift = lift + PAWN_HEIGHT)
        val deep = lerp(tint, Color.Black, TOKEN_DEEP)
        // Base disc
        drawOval(deep, Offset(foot.x - base, foot.y - base * DISC_FLAT), Size(base * 2, base * 2 * DISC_FLAT))
        // Cone body
        val body = Path().apply {
            moveTo(foot.x - base * BODY_WIDTH, foot.y)
            lineTo(neck.x, neck.y)
            lineTo(foot.x + base * BODY_WIDTH, foot.y)
            close()
        }
        drawPath(body, Brush.horizontalGradient(listOf(deep, tint, lerp(tint, Color.White, TOKEN_SHINE / 2), deep), foot.x - base, foot.x + base))
        // Head
        val head = cell * PAWN_HEAD
        drawCircle(
            Brush.radialGradient(listOf(lerp(tint, Color.White, TOKEN_SHINE), tint, deep), Offset(neck.x - head / 3, neck.y - head / 3), head * 1.4f),
            head,
            neck,
        )
        drawCircle(Color.White.copy(alpha = TOKEN_RIM_ALPHA), head, neck, style = Stroke(head * TOKEN_RIM))
    }
}

private fun DrawScope.drawSlab(p: CityBazaarIsoProjection, colours: CityBazaarBoardColours) {
    val lo = -CityBazaarIsoProjection.FRAME
    val hi = CityBazaarBoardGeometry.SIDE + CityBazaarIsoProjection.FRAME
    val top = quad(p, lo, lo, hi, hi)
    drawPath(Path().apply { addPath(top, Offset(0f, p.slabDepth)) }, colours.slabSide)
    val b = top.getBounds()
    drawPath(top, Brush.verticalGradient(listOf(colours.slabTop, colours.slabBottom), b.top, b.bottom))
    drawPath(top, colours.rim.copy(alpha = RIM_ALPHA), style = Stroke(p.cellSize(Offset(CENTRE, CENTRE)) * RIM_WIDTH))
    val d = CityBazaarBoardGeometry.DEPTH
    drawPath(quad(p, d, d, CityBazaarBoardGeometry.SIDE - d, CityBazaarBoardGeometry.SIDE - d), colours.field)
}

private fun DrawScope.drawTile(p: CityBazaarIsoProjection, tile: CbTileUi, colours: CityBazaarBoardColours, measurer: TextMeasurer, glow: Float, lit: Boolean) {
    val spec = CityBazaarBoard.tile(tile.index)
    val r = CityBazaarBoardGeometry.rect(tile.index)
    val x0 = r.x0 + INSET
    val y0 = r.y0 + INSET
    val x1 = r.x1 - INSET
    val y1 = r.y1 - INSET
    val centre = Offset(r.centreX, r.centreY)
    val cell = p.cellSize(centre)
    val top = quad(p, x0, y0, x1, y1)
    drawPath(Path().apply { addPath(top, Offset(0f, cell * TILE_HEIGHT)) }, colours.tileSide)
    val b = top.getBounds()
    drawPath(top, Brush.verticalGradient(listOf(colours.tileEdge, colours.tile), b.top, b.bottom))

    val (ix, iy) = CityBazaarBoardGeometry.inward(tile.index)
    val depth = CityBazaarBoardGeometry.DEPTH
    // Colour header on the inner side.
    spec.group?.let { colours.groups[it] }?.let { header ->
        drawPath(edgeBand(p, x0, y0, x1, y1, ix, iy, depth * HEADER), header)
    }
    // Owner: the outer edge only, in their colour.
    tile.ownerColour?.let { owner ->
        val tint = colours.players[owner] ?: colours.text
        val band = edgeBand(p, x0, y0, x1, y1, -ix, -iy, depth * OWNER_BAND)
        drawPath(band, tint.copy(alpha = OWNER_GLOW_ALPHA * glow), style = Stroke(cell * OWNER_GLOW_WIDTH))
        drawPath(band, tint)
    }
    if (lit) {
        drawPath(top, colours.highlight.copy(alpha = HIGHLIGHT_GLOW_ALPHA * glow), style = Stroke(cell * HIGHLIGHT_GLOW))
        drawPath(top, colours.highlight, style = Stroke(cell * HIGHLIGHT_WIDTH))
    }

    val inward = Offset(ix.toFloat(), iy.toFloat())
    when (spec.kind) {
        TileKind.START, TileKind.JAIL, TileKind.CLUB, TileKind.REST_HOUSE -> {
            val name = when (spec.kind) {
                TileKind.START -> Strings.cbCornerStart
                TileKind.JAIL -> Strings.cbCornerJail
                TileKind.CLUB -> Strings.cbCornerClub
                else -> Strings.cbCornerRest
            }
            val colour = when (spec.kind) {
                TileKind.START -> colours.start
                TileKind.JAIL -> colours.jail
                else -> colours.text
            }
            val room = cell * (depth - TEXT_MARGIN)
            val layout = fitText(measurer, name, TextStyle(color = colour, fontSize = (cell * CORNER_TEXT).toSp(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center), room, room)
            drawUpright(p.project(centre), listOf(layout))
        }
        TileKind.LUCK, TileKind.CHEST -> {
            val luck = spec.kind == TileKind.LUCK
            val colour = if (luck) colours.luck else colours.chest
            val glyph = measurer.measure(if (luck) Strings.cbLuckGlyph else Strings.cbChestGlyph, TextStyle(color = colour, fontSize = (cell * GLYPH_TEXT).toSp(), fontWeight = FontWeight.Bold))
            val label = measurer.measure(if (luck) Strings.cbLuckLabel else Strings.cbChestShort, TextStyle(color = colour, fontSize = (cell * PRICE_TEXT).toSp(), fontWeight = FontWeight.Bold))
            drawAlongTile(p, centre, ix, cell, listOf(glyph, label))
        }
        else -> {
            val along = cell * (depth * (1 - HEADER) - TEXT_MARGIN)
            val across = cell * (1 - TEXT_MARGIN)
            val name = fitText(measurer, tileLines(spec.name), TextStyle(color = colours.text, fontSize = (cell * NAME_TEXT).toSp(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center), along, across * NAME_SHARE)
            val priceText = when {
                spec.buyable && tile.ownerColour == null -> Strings.cbRupees(RupeeFormat.digits(spec.price))
                spec.kind == TileKind.TAX && spec.tax > 0 -> Strings.cbRupees(RupeeFormat.digits(spec.tax))
                spec.kind == TileKind.TAX -> Strings.cbPerBuilding(RupeeFormat.digits(spec.taxPerBuilding))
                else -> null
            }
            val price = priceText?.let { fitText(measurer, it, TextStyle(color = colours.subText, fontSize = (cell * PRICE_TEXT).toSp(), fontWeight = FontWeight.Medium), along, across * PRICE_SHARE, maxLines = 1) }
            val textAt = centre - inward * (depth * TEXT_OUTWARD)
            drawAlongTile(p, textAt, ix, cell, listOfNotNull(name, price))
            drawBuildings(p, tile, centre + inward * (depth * BUILDING_INWARD), ix, iy, colours)
        }
    }
}

/** A band along one edge of a tile: the side [dx], [dy] points to, [thickness] deep. */
private fun edgeBand(p: CityBazaarIsoProjection, x0: Float, y0: Float, x1: Float, y1: Float, dx: Int, dy: Int, thickness: Float): Path = when {
    dx > 0 -> quad(p, x1 - thickness, y0, x1, y1)
    dx < 0 -> quad(p, x0, y0, x0 + thickness, y1)
    dy > 0 -> quad(p, x0, y1 - thickness, x1, y1)
    else -> quad(p, x0, y0, x1, y0 + thickness)
}

/** Lines of text stacked and centred on [at], upright. */
private fun DrawScope.drawUpright(at: Offset, lines: List<TextLayoutResult>) {
    val total = lines.sumOf { it.size.height }
    withTransform({ translate(at.x, at.y) }) {
        var y = -total / 2f
        lines.forEach {
            drawText(it, topLeft = Offset(-it.size.width / 2f, y))
            y += it.size.height
        }
    }
}

/** Lines of text laid on the tile's surface: along the ring, reading left to right, tops toward the far side. */
private fun DrawScope.drawAlongTile(p: CityBazaarIsoProjection, at: Offset, ix: Int, cell: Float, lines: List<TextLayoutResult>) {
    val origin = p.project(at)
    // Along the tile's length (toward the middle), so names have room.
    val along = if (ix != 0) Offset(1f, 0f) else Offset(0f, 1f)
    val across = Offset(along.y, along.x)
    val alongScreen = (p.project(at + along) - origin).let { if (it.x >= 0) it else -it }
    val upScreen = (p.project(at + across) - origin).let { if (it.y <= 0) it else -it }
    val m = Matrix()
    m.values[Matrix.ScaleX] = alongScreen.x / cell
    m.values[Matrix.SkewY] = alongScreen.y / cell
    m.values[Matrix.SkewX] = -upScreen.x / cell
    m.values[Matrix.ScaleY] = -upScreen.y / cell
    m.values[Matrix.TranslateX] = origin.x
    m.values[Matrix.TranslateY] = origin.y
    val total = lines.sumOf { it.size.height }
    withTransform({ transform(m) }) {
        var y = -total / 2f
        lines.forEach {
            drawText(it, topLeft = Offset(-it.size.width / 2f, y))
            y += it.size.height
        }
    }
}

/** Up to 3 houses (block + pyramid roof) in a row across the tile, or one tall hotel tower, in the owner's colour. */
private fun DrawScope.drawBuildings(p: CityBazaarIsoProjection, tile: CbTileUi, at: Offset, ix: Int, iy: Int, colours: CityBazaarBoardColours) {
    if (tile.level == 0) return
    val tint = tile.ownerColour?.let { colours.players[it] } ?: colours.house
    if (tile.level >= HOTEL_LEVEL) return drawHotel(p, at, tint)
    val across = Offset(iy.toFloat(), ix.toFloat())
    for (i in 0 until tile.level) {
        val shift = (i - (tile.level - 1) / 2f) * HOUSE_SPACING
        drawHouse(p, at + across * shift, tint)
    }
}

private fun DrawScope.drawHouse(p: CityBazaarIsoProjection, at: Offset, tint: Color) {
    val h = HOUSE_SIZE / 2
    drawBox(p, at, h, HOUSE_HEIGHT, tint)
    val apex = p.project(at, HOUSE_HEIGHT + ROOF_HEIGHT)
    val corners = listOf(Offset(-h, -h), Offset(h, -h), Offset(h, h), Offset(-h, h)).map { p.project(at + it, HOUSE_HEIGHT) }
    val roof = lerp(tint, Color.Black, ROOF_DARKEN)
    // Far faces first, then the near ones a little brighter.
    corners.indices.sortedBy { (corners[it].y + corners[(it + 1) % 4].y) }.forEachIndexed { order, i ->
        val face = Path().apply {
            moveTo(apex.x, apex.y)
            lineTo(corners[i].x, corners[i].y)
            lineTo(corners[(i + 1) % 4].x, corners[(i + 1) % 4].y)
            close()
        }
        drawPath(face, lerp(roof, tint, order / ROOF_FACES))
    }
}

private fun DrawScope.drawHotel(p: CityBazaarIsoProjection, at: Offset, tint: Color) {
    val h = HOTEL_SIZE / 2
    drawBox(p, at, h, HOTEL_HEIGHT, tint)
    // Floor lines on the tower.
    val cell = p.cellSize(at)
    for (floor in 1 until HOTEL_FLOORS) {
        val lift = HOTEL_HEIGHT * floor / HOTEL_FLOORS
        val a = p.project(at + Offset(-h, h), lift)
        val b = p.project(at + Offset(h, h), lift)
        val c = p.project(at + Offset(h, -h), lift)
        drawLine(Color.White.copy(alpha = FLOOR_ALPHA), a, b, cell * FLOOR_WIDTH)
        drawLine(Color.White.copy(alpha = FLOOR_ALPHA), b, c, cell * FLOOR_WIDTH)
    }
}

/** A box [h] half-wide, [height] tall: shadowed sides and a lit top. */
private fun DrawScope.drawBox(p: CityBazaarIsoProjection, at: Offset, h: Float, height: Float, tint: Color) {
    val cell = p.cellSize(at)
    val base = listOf(Offset(-h, -h), Offset(h, -h), Offset(h, h), Offset(-h, h)).map { at + it }
    val side = lerp(tint, Color.Black, BLOCK_SIDE)
    // Each wall: bottom edge to top edge.
    base.indices.sortedBy { p.project(base[it]).y + p.project(base[(it + 1) % 4]).y }.forEach { i ->
        val a = base[i]
        val b = base[(i + 1) % 4]
        val wall = Path().apply {
            val a0 = p.project(a)
            moveTo(a0.x, a0.y)
            p.project(b).let { lineTo(it.x, it.y) }
            p.project(b, height).let { lineTo(it.x, it.y) }
            p.project(a, height).let { lineTo(it.x, it.y) }
            close()
        }
        drawPath(wall, side)
    }
    val top = Path().apply {
        val first = p.project(base[0], height)
        moveTo(first.x, first.y)
        base.drop(1).forEach { p.project(it, height).let { q -> lineTo(q.x, q.y) } }
        close()
    }
    drawPath(top, Brush.verticalGradient(listOf(lerp(tint, Color.White, BLOCK_SHINE), tint), top.getBounds().top, top.getBounds().bottom))
    drawPath(top, tint.copy(alpha = BLOCK_GLOW_ALPHA), style = Stroke(cell * BLOCK_GLOW))
}

private fun DrawScope.drawDeck(
    p: CityBazaarIsoProjection,
    deck: CardDeck,
    tint: Color,
    glyph: String,
    label: String,
    colours: CityBazaarBoardColours,
    measurer: TextMeasurer,
    glow: Float,
) {
    val centre = CityBazaarIsoProjection.deckCentre(deck)
    val cell = p.cellSize(centre)
    for (layer in DECK_LAYERS downTo 0) {
        val path = quad(p, centre.x - DECK_HALF, centre.y - DECK_HALF, centre.x + DECK_HALF, centre.y + DECK_HALF, lift = layer * DECK_STEP)
        drawPath(path, lerp(tint, colours.field, if (layer == 0) DECK_TOP_MIX else DECK_LAYER_MIX))
        drawPath(path, tint.copy(alpha = if (layer == 0) 1f else DECK_EDGE_ALPHA), style = Stroke(cell * DECK_EDGE))
    }
    val top = quad(p, centre.x - DECK_HALF, centre.y - DECK_HALF, centre.x + DECK_HALF, centre.y + DECK_HALF)
    drawPath(top, tint.copy(alpha = DECK_GLOW_ALPHA * glow), style = Stroke(cell * DECK_GLOW))
    val glyphLayout = measurer.measure(glyph, TextStyle(color = tint, fontSize = (cell * DECK_GLYPH).toSp(), fontWeight = FontWeight.Bold))
    val labelLayout = measurer.measure(label, TextStyle(color = tint, fontSize = (cell * DECK_TEXT).toSp(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
    drawUpright(p.project(centre), listOf(glyphLayout, labelLayout))
}

private fun quad(p: CityBazaarIsoProjection, x0: Float, y0: Float, x1: Float, y1: Float, lift: Float = 0f): Path = Path().apply {
    val a = p.project(Offset(x0, y0), lift)
    moveTo(a.x, a.y)
    listOf(Offset(x1, y0), Offset(x1, y1), Offset(x0, y1)).forEach {
        val q = p.project(it, lift)
        lineTo(q.x, q.y)
    }
    close()
}

private const val CENTRE = CityBazaarIsoProjection.CENTRE
private const val INSET = 0.05f
private const val HEADER = 0.24f
private const val OWNER_BAND = 0.1f
private const val TILE_HEIGHT = 0.12f
private const val RIM_ALPHA = 0.35f
private const val RIM_WIDTH = 0.05f
private const val OWNER_GLOW_WIDTH = 0.18f
private const val OWNER_GLOW_ALPHA = 0.55f
private const val HIGHLIGHT_WIDTH = 0.07f
private const val HIGHLIGHT_GLOW = 0.28f
private const val HIGHLIGHT_GLOW_ALPHA = 0.7f
private const val NAME_TEXT = 0.24f
private const val CORNER_TEXT = 0.3f
private const val GLYPH_TEXT = 0.6f
private const val PRICE_TEXT = 0.19f
private const val TEXT_MARGIN = 0.18f
private const val NAME_SHARE = 0.62f
private const val PRICE_SHARE = 0.34f
private const val TEXT_OUTWARD = 0.14f
private const val BUILDING_INWARD = 0.2f
private const val HOUSE_SIZE = 0.22f
private const val HOUSE_HEIGHT = 0.16f
private const val ROOF_HEIGHT = 0.14f
private const val ROOF_DARKEN = 0.35f
private const val ROOF_FACES = 3f
private const val HOUSE_SPACING = 0.3f
private const val HOTEL_SIZE = 0.34f
private const val HOTEL_HEIGHT = 0.6f
private const val HOTEL_FLOORS = 4
private const val FLOOR_ALPHA = 0.45f
private const val FLOOR_WIDTH = 0.015f
private const val BLOCK_SIDE = 0.45f
private const val BLOCK_SHINE = 0.35f
private const val BLOCK_GLOW = 0.03f
private const val BLOCK_GLOW_ALPHA = 0.6f
private const val DECK_HALF = 1.2f
private const val DECK_LAYERS = 4
private const val DECK_STEP = 0.05f
private const val DECK_TOP_MIX = 0.72f
private const val DECK_LAYER_MIX = 0.4f
private const val DECK_EDGE = 0.04f
private const val DECK_EDGE_ALPHA = 0.7f
private const val DECK_GLOW = 0.25f
private const val DECK_GLOW_ALPHA = 0.35f
private const val DECK_GLYPH = 0.8f
private const val DECK_TEXT = 0.3f
private const val PAWN_BASE = 0.2f
private const val PAWN_HEIGHT = 0.5f
private const val PAWN_HEAD = 0.13f
private const val PAWN_HOP = 0.8f
private const val BODY_WIDTH = 0.7f
private const val DISC_FLAT = 0.55f
private const val TOKEN_SHINE = 0.55f
private const val TOKEN_DEEP = 0.4f
private const val TOKEN_RIM = 0.12f
private const val TOKEN_RIM_ALPHA = 0.5f
private const val SHADOW_ALPHA = 0.4f
private const val SHADOW_FLAT = 0.5f
