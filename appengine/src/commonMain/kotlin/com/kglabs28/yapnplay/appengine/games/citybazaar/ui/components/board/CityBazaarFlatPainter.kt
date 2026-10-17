package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
 * The City Bazaar board, flat and square (START bottom-left): a dark slab
 * and a ring of 36 matte tiles. Each property has its colour header on the
 * inner side (houses and the hotel stand on it), its name and price laid
 * along the tile, and its outer edge lit in the owner's colour. Luck (?) and
 * Shagun Chest (₹) tiles read the same way; their piles sit in the middle.
 */
fun DrawScope.drawCityBazaarFlatBoard(
    p: CityBazaarFlatProjection,
    colours: CityBazaarBoardColours,
    tiles: List<CbTileUi>,
    measurer: TextMeasurer,
    highlight: Set<Int> = emptySet(),
) {
    drawSlab(p, colours)
    drawDeck(p, CardDeck.CHEST, colours.chest, Strings.cbChestGlyph, Strings.cbChestLabel, colours, measurer)
    drawDeck(p, CardDeck.LUCK, colours.luck, Strings.cbLuckGlyph, Strings.cbLuckLabel, colours, measurer)
    tiles.forEach { drawTile(p, it, colours, measurer, it.index in highlight) }
}

/** Tokens as pawn icons (a disc, a body and a glossy head), each lifted by its hop. */
fun DrawScope.drawCityBazaarFlatTokens(p: CityBazaarFlatProjection, spots: List<Pair<TokenSpot, Color>>) {
    val u = p.unit
    spots.sortedBy { it.first.grid.y }.forEach { (spot, tint) ->
        val ground = p.project(spot.grid)
        val base = u * PAWN_BASE
        drawOval(Color.Black.copy(alpha = SHADOW_ALPHA), Offset(ground.x - base, ground.y - base * SHADOW_FLAT / 2), Size(base * 2, base * SHADOW_FLAT))
        val foot = p.project(spot.grid, lift = spot.lift * PAWN_HOP)
        val neck = Offset(foot.x, foot.y - u * PAWN_HEIGHT)
        val deep = lerp(tint, Color.Black, TOKEN_DEEP)
        drawOval(deep, Offset(foot.x - base, foot.y - base * DISC_FLAT / 2), Size(base * 2, base * DISC_FLAT))
        val body = Path().apply {
            moveTo(foot.x - base * BODY_WIDTH, foot.y)
            lineTo(neck.x, neck.y)
            lineTo(foot.x + base * BODY_WIDTH, foot.y)
            close()
        }
        drawPath(body, Brush.horizontalGradient(listOf(deep, tint, lerp(tint, Color.White, TOKEN_SHINE / 2), deep), foot.x - base, foot.x + base))
        val head = u * PAWN_HEAD
        drawCircle(Brush.radialGradient(listOf(lerp(tint, Color.White, TOKEN_SHINE), tint, deep), Offset(neck.x - head / 3, neck.y - head / 3), head * 1.4f), head, neck)
        drawCircle(Color.White.copy(alpha = TOKEN_RIM_ALPHA), head, neck, style = Stroke(head * TOKEN_RIM))
    }
}

private fun DrawScope.drawSlab(p: CityBazaarFlatProjection, colours: CityBazaarBoardColours) {
    val u = p.unit
    val lo = p.project(Offset(-CityBazaarFlatProjection.FRAME, -CityBazaarFlatProjection.FRAME))
    val hi = p.project(Offset(CityBazaarBoardGeometry.SIDE + CityBazaarFlatProjection.FRAME, CityBazaarBoardGeometry.SIDE + CityBazaarFlatProjection.FRAME))
    val size = Size(hi.x - lo.x, hi.y - lo.y)
    val radius = CornerRadius(u * SLAB_RADIUS)
    drawRoundRect(colours.slabSide, lo + Offset(0f, u * SLAB_SHADOW), size, radius)
    drawRoundRect(Brush.verticalGradient(listOf(colours.slabTop, colours.slabBottom), lo.y, hi.y), lo, size, radius)
    drawRoundRect(colours.rim.copy(alpha = RIM_ALPHA), lo, size, radius, style = Stroke(u * RIM_WIDTH))
    val d = CityBazaarBoardGeometry.DEPTH
    rect(p, d, d, CityBazaarBoardGeometry.SIDE - d, CityBazaarBoardGeometry.SIDE - d).let { (o, s) -> drawRect(colours.field, o, s) }
}

private fun DrawScope.drawTile(p: CityBazaarFlatProjection, tile: CbTileUi, colours: CityBazaarBoardColours, measurer: TextMeasurer, lit: Boolean) {
    val u = p.unit
    val spec = CityBazaarBoard.tile(tile.index)
    val r = CityBazaarBoardGeometry.rect(tile.index)
    val x0 = r.x0 + INSET
    val y0 = r.y0 + INSET
    val x1 = r.x1 - INSET
    val y1 = r.y1 - INSET
    val (origin, size) = rect(p, x0, y0, x1, y1)
    val radius = CornerRadius(u * TILE_RADIUS)
    drawRoundRect(colours.tileSide, origin + Offset(0f, u * TILE_SHADOW), size, radius)
    drawRoundRect(Brush.verticalGradient(listOf(colours.tileEdge, colours.tile), origin.y, origin.y + size.height), origin, size, radius)

    val (ix, iy) = CityBazaarBoardGeometry.inward(tile.index)
    val depth = CityBazaarBoardGeometry.DEPTH
    val centre = Offset(r.centreX, r.centreY)
    val inward = Offset(ix.toFloat(), iy.toFloat())
    spec.group?.let { colours.groups[it] }?.let { header -> band(p, x0, y0, x1, y1, ix, iy, depth * HEADER).let { (o, s) -> drawRect(header, o, s) } }
    tile.ownerColour?.let { owner ->
        val tint = colours.players[owner] ?: colours.text
        val (o, s) = band(p, x0, y0, x1, y1, -ix, -iy, depth * OWNER_BAND)
        drawRect(tint, o, s)
    }
    if (lit) {
        drawRoundRect(colours.highlight, origin, size, radius, style = Stroke(u * HIGHLIGHT_WIDTH))
    }

    // Text runs along the tile's long side: across for the left and right columns, upward for the top and bottom rows.
    val angle = if (iy != 0) VERTICAL else 0f
    val bodyCentre = centre - inward * (depth * HEADER / 2)
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
            val room = u * (depth - TEXT_MARGIN)
            val layout = fitText(measurer, name, TextStyle(color = colour, fontSize = (u * CORNER_TEXT).toSp(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center), room, room)
            drawLines(p.project(centre), 0f, listOf(layout))
        }
        TileKind.LUCK, TileKind.CHEST -> {
            val luck = spec.kind == TileKind.LUCK
            val colour = if (luck) colours.luck else colours.chest
            val along = u * (depth - TEXT_MARGIN)
            val across = u * (1 - TEXT_MARGIN)
            val glyph = fitText(measurer, if (luck) Strings.cbLuckGlyph else Strings.cbChestGlyph, TextStyle(color = colour, fontSize = (u * GLYPH_TEXT).toSp(), fontWeight = FontWeight.Bold), along, across * NAME_SHARE)
            val label = fitText(measurer, if (luck) Strings.cbLuckLabel else Strings.cbChestShort, TextStyle(color = colour, fontSize = (u * PRICE_TEXT).toSp(), fontWeight = FontWeight.Bold), along, across * PRICE_SHARE)
            drawLines(p.project(centre), angle, listOf(glyph, label))
        }
        else -> {
            // The text area: the tile's length minus the colour header, its width minus a margin.
            val along = u * (depth * (1 - HEADER) - TEXT_MARGIN)
            val across = u * (1 - TEXT_MARGIN)
            val name = fitText(measurer, tileLines(spec.name), TextStyle(color = colours.text, fontSize = (u * NAME_TEXT).toSp(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center), along, across * NAME_SHARE)
            val priceText = when {
                spec.buyable && tile.ownerColour == null -> Strings.cbRupees(RupeeFormat.digits(spec.price))
                spec.kind == TileKind.TAX && spec.taxPerProperty > 0 -> Strings.cbPerProperty(RupeeFormat.digits(spec.taxPerProperty))
                spec.kind == TileKind.TAX -> Strings.cbPerBuilding(RupeeFormat.digits(spec.taxPerBuilding))
                else -> null
            }
            val price = priceText?.let { fitText(measurer, it, TextStyle(color = colours.subText, fontSize = (u * PRICE_TEXT).toSp(), fontWeight = FontWeight.Medium), along, across * PRICE_SHARE, maxLines = 1) }
            drawLines(p.project(bodyCentre), angle, listOfNotNull(name, price))
            drawBuildings(p, tile, centre + inward * (depth / 2 - depth * HEADER / 2), ix, iy, colours)
        }
    }
}

/** A strip along one edge of a tile (the side [dx], [dy] points to), [thickness] units deep. */
private fun band(p: CityBazaarFlatProjection, x0: Float, y0: Float, x1: Float, y1: Float, dx: Int, dy: Int, thickness: Float): Pair<Offset, Size> = when {
    dx > 0 -> rect(p, x1 - thickness, y0, x1, y1)
    dx < 0 -> rect(p, x0, y0, x0 + thickness, y1)
    dy > 0 -> rect(p, x0, y1 - thickness, x1, y1)
    else -> rect(p, x0, y0, x1, y0 + thickness)
}

private fun rect(p: CityBazaarFlatProjection, x0: Float, y0: Float, x1: Float, y1: Float): Pair<Offset, Size> {
    val a = p.project(Offset(x0, y0))
    val b = p.project(Offset(x1, y1))
    return a to Size(b.x - a.x, b.y - a.y)
}

/** Lines of text stacked and centred on [at], turned by [degrees]. */
private fun DrawScope.drawLines(at: Offset, degrees: Float, lines: List<TextLayoutResult>) {
    val total = lines.sumOf { it.size.height }
    withTransform({ translate(at.x, at.y) }) {
        rotate(degrees, pivot = Offset.Zero) {
            var y = -total / 2f
            lines.forEach {
                drawText(it, topLeft = Offset(-it.size.width / 2f, y))
                y += it.size.height
            }
        }
    }
}

/** Up to 3 houses (square + roof) in a row on the colour header, or one hotel, in the owner's colour. */
private fun DrawScope.drawBuildings(p: CityBazaarFlatProjection, tile: CbTileUi, at: Offset, ix: Int, iy: Int, colours: CityBazaarBoardColours) {
    if (tile.level == 0) return
    val tint = tile.ownerColour?.let { colours.players[it] } ?: colours.house
    val u = p.unit
    val centre = p.project(at)
    if (tile.level >= HOTEL_LEVEL) {
        val w = u * HOTEL_WIDTH
        val h = u * HOTEL_HEIGHT
        val o = Offset(centre.x - w / 2, centre.y - h / 2)
        drawRect(lerp(tint, Color.Black, BUILDING_SHADE), o + Offset(u * BUILDING_SHADOW, u * BUILDING_SHADOW), Size(w, h))
        drawRect(tint, o, Size(w, h))
        for (row in 0 until HOTEL_WINDOW_ROWS) {
            for (col in 0 until HOTEL_WINDOW_COLS) {
                val wx = o.x + w * (col + 1) / (HOTEL_WINDOW_COLS + 1)
                val wy = o.y + h * (row + 1) / (HOTEL_WINDOW_ROWS + 1)
                drawCircle(Color.White.copy(alpha = WINDOW_ALPHA), u * WINDOW_RADIUS, Offset(wx, wy))
            }
        }
        return
    }
    val across = Offset(iy.toFloat(), ix.toFloat())
    for (i in 0 until tile.level) {
        val c = p.project(at + across * ((i - (tile.level - 1) / 2f) * HOUSE_SPACING))
        val half = u * HOUSE_SIZE / 2
        val house = Path().apply {
            moveTo(c.x - half, c.y + half)
            lineTo(c.x - half, c.y - half * HOUSE_WALL)
            lineTo(c.x, c.y - half * HOUSE_ROOF)
            lineTo(c.x + half, c.y - half * HOUSE_WALL)
            lineTo(c.x + half, c.y + half)
            close()
        }
        drawPath(Path().apply { addPath(house, Offset(u * BUILDING_SHADOW, u * BUILDING_SHADOW)) }, lerp(tint, Color.Black, BUILDING_SHADE))
        drawPath(house, tint)
        drawPath(house, Color.White.copy(alpha = WINDOW_ALPHA), style = Stroke(u * HOUSE_OUTLINE))
    }
}

private fun DrawScope.drawDeck(
    p: CityBazaarFlatProjection,
    deck: CardDeck,
    tint: Color,
    glyph: String,
    label: String,
    colours: CityBazaarBoardColours,
    measurer: TextMeasurer,
) {
    val u = p.unit
    val centre = p.project(CityBazaarFlatProjection.deckCentre(deck))
    val half = u * CityBazaarFlatProjection.DECK_HALF
    val radius = CornerRadius(u * DECK_RADIUS)
    for (layer in DECK_LAYERS downTo 0) {
        val o = Offset(centre.x - half + layer * u * DECK_STEP, centre.y - half + layer * u * DECK_STEP)
        drawRoundRect(lerp(tint, colours.field, if (layer == 0) DECK_TOP_MIX else DECK_LAYER_MIX), o, Size(half * 2, half * 2), radius)
        drawRoundRect(tint.copy(alpha = if (layer == 0) 1f else DECK_EDGE_ALPHA), o, Size(half * 2, half * 2), radius, style = Stroke(u * DECK_EDGE))
    }
    // Symbol and name (two words on two lines) kept inside the pile.
    val room = half * 2 * DECK_TEXT_ROOM
    val glyphLayout = fitText(measurer, glyph, TextStyle(color = tint, fontSize = (u * DECK_GLYPH).toSp(), fontWeight = FontWeight.Bold), room, room * DECK_GLYPH_SHARE, maxLines = 1)
    val labelLayout = fitText(measurer, tileLines(label), TextStyle(color = tint, fontSize = (u * DECK_TEXT).toSp(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center), room, room * DECK_LABEL_SHARE)
    drawLines(centre, 0f, listOf(glyphLayout, labelLayout))
}

private const val VERTICAL = -90f
private const val INSET = 0.04f
private const val TILE_RADIUS = 0.08f
private const val TILE_SHADOW = 0.05f
private const val SLAB_RADIUS = 0.3f
private const val SLAB_SHADOW = 0.15f
private const val HEADER = 0.26f
private const val OWNER_BAND = 0.1f
private const val RIM_ALPHA = 0.35f
private const val RIM_WIDTH = 0.05f
private const val HIGHLIGHT_WIDTH = 0.06f
private const val NAME_TEXT = 0.24f
private const val CORNER_TEXT = 0.27f
private const val GLYPH_TEXT = 0.42f
private const val PRICE_TEXT = 0.18f
private const val TEXT_MARGIN = 0.18f
private const val NAME_SHARE = 0.62f
private const val PRICE_SHARE = 0.34f
private const val HOUSE_SIZE = 0.24f
private const val HOUSE_WALL = 0.1f
private const val HOUSE_ROOF = 1.1f
private const val HOUSE_SPACING = 0.28f
private const val HOUSE_OUTLINE = 0.015f
private const val HOTEL_WIDTH = 0.7f
private const val HOTEL_HEIGHT = 0.34f
private const val HOTEL_WINDOW_ROWS = 2
private const val HOTEL_WINDOW_COLS = 4
private const val WINDOW_RADIUS = 0.025f
private const val WINDOW_ALPHA = 0.6f
private const val BUILDING_SHADOW = 0.03f
private const val BUILDING_SHADE = 0.45f
private const val DECK_RADIUS = 0.15f
private const val DECK_LAYERS = 3
private const val DECK_STEP = 0.05f
private const val DECK_TOP_MIX = 0.72f
private const val DECK_LAYER_MIX = 0.4f
private const val DECK_EDGE = 0.04f
private const val DECK_EDGE_ALPHA = 0.7f
private const val DECK_GLYPH = 0.55f
private const val DECK_TEXT = 0.22f
private const val DECK_TEXT_ROOM = 0.8f
private const val DECK_GLYPH_SHARE = 0.45f
private const val DECK_LABEL_SHARE = 0.45f
private const val PAWN_BASE = 0.2f
private const val PAWN_HEIGHT = 0.42f
private const val PAWN_HEAD = 0.12f
private const val PAWN_HOP = 0.7f
private const val BODY_WIDTH = 0.7f
private const val DISC_FLAT = 0.5f
private const val TOKEN_SHINE = 0.55f
private const val TOKEN_DEEP = 0.4f
private const val TOKEN_RIM = 0.12f
private const val TOKEN_RIM_ALPHA = 0.5f
private const val SHADOW_ALPHA = 0.4f
private const val SHADOW_FLAT = 0.5f
