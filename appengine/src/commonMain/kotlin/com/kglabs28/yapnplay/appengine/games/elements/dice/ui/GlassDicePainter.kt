package com.kglabs28.yapnplay.appengine.games.elements.dice.ui

import com.kglabs28.yapnplay.appengine.games.elements.dice.utils.DiceFaceLayout
import com.kglabs28.yapnplay.appengine.games.elements.dice.utils.DiceMeshGeometry
import com.kglabs28.yapnplay.appengine.games.elements.dice.utils.DiceOrientation
import com.kglabs28.yapnplay.appengine.games.elements.dice.utils.RotationBasis
import com.kglabs28.yapnplay.appengine.games.elements.dice.utils.Vector3D
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.kglabs28.yapnplay.appengine.games.elements.dice.utils.DiceMeshGeometry.oriented
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

/**
 * Draws the glass die: a soft contact shadow and halo, the bloom around
 * its outline, a lit translucent body, the far faces and pips seen
 * through the glass, then the near surface, glare and glowing pips.
 * [pulse] (0..1) breathes the glow; [phase] drifts the tint over time.
 */
internal fun DrawScope.drawGlassDice(
    angles: DiceOrientation,
    progress: Float,
    palette: DicePalette,
    pulse: Float,
    phase: Float,
) {
    val hop = sin(PI.toFloat() * progress)
    val glow = GLOW_BASE + GLOW_PULSE * pulse + GLOW_HOP * hop
    val unit = size.minDimension / UNIT_DIVISOR * (1f + HOP_GROW * hop)
    val c = Offset(center.x, center.y - unit * HOP_LIFT * hop)
    val cameraPos = Vector3D(0f, 0f, DiceMeshGeometry.CAMERA)
    val basis = RotationBasis(
        Vector3D(1f, 0f, 0f).oriented(angles),
        Vector3D(0f, 1f, 0f).oriented(angles),
        Vector3D(0f, 0f, 1f).oriented(angles),
    )
    val glowColor = sample(palette.tints, phase)
    val core = sample(palette.tints, phase + CORE_PHASE_SHIFT)

    fun project(p: Vector3D): Offset {
        val f = DiceMeshGeometry.CAMERA / (DiceMeshGeometry.CAMERA - p.z)
        return Offset(c.x + p.x * f * unit, c.y - p.y * f * unit)
    }
    fun facePoint(face: DiceFaceLayout, pu: Float, pv: Float) = project(basis.apply(face.normal + face.u * pu + face.v * pv))
    fun polygon(pts: List<Offset>) = Path().apply {
        moveTo(pts[0].x, pts[0].y)
        for (k in 1 until pts.size) lineTo(pts[k].x, pts[k].y)
        close()
    }
    fun ring(face: DiceFaceLayout, pu: Float, pv: Float, radius: Float) = Path().apply {
        for (i in 0 until RING_SEGMENTS) {
            val ang = 2f * PI.toFloat() * i / RING_SEGMENTS
            val p = facePoint(face, pu + radius * cos(ang), pv + radius * sin(ang))
            if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    // Contact shadow on the board (shrinks as the die hops) and the halo.
    val shadowWidth = unit * SHADOW_WIDTH * (1f - SHADOW_SHRINK * hop)
    drawOval(
        brush = Brush.radialGradient(
            listOf(Color.Black.copy(alpha = SHADOW_ALPHA * (1f - hop * SHADOW_SHRINK)), Color.Transparent),
            center = Offset(center.x, center.y + unit * SHADOW_DROP),
            radius = shadowWidth,
        ),
        topLeft = Offset(center.x - shadowWidth, center.y + unit * SHADOW_DROP - shadowWidth * SHADOW_FLATTEN),
        size = Size(shadowWidth * 2, shadowWidth * 2 * SHADOW_FLATTEN),
    )
    drawCircle(
        brush = Brush.radialGradient(
            listOf(glowColor.copy(alpha = (HALO_ALPHA * glow).coerceAtMost(1f)), Color.Transparent),
            center = c,
            radius = size.minDimension / 2,
        ),
        radius = size.minDimension / 2,
        center = c,
    )

    // Visible surface cells, each with its own glass colour and opacity.
    val silhouette = Path()
    val cells = ArrayList<Pair<Path, Color>>()
    for (grid in DiceMeshGeometry.mesh) {
        val n = grid.size
        val pos = Array(n) { i -> Array(n) { j -> basis.apply(grid[i][j].pos) } }
        val nor = Array(n) { i -> Array(n) { j -> basis.apply(grid[i][j].normal) } }
        val scr = Array(n) { i -> Array(n) { j -> project(pos[i][j]) } }
        for (i in 0 until n - 1) for (j in 0 until n - 1) {
            val nm = (nor[i][j] + nor[i + 1][j] + nor[i + 1][j + 1] + nor[i][j + 1]).normalized()
            val centre = (pos[i][j] + pos[i + 1][j] + pos[i + 1][j + 1] + pos[i][j + 1]) * QUARTER
            val toEye = (cameraPos - centre).normalized()
            val facing = nm.dot(toEye)
            if (facing <= 0f) continue
            val diffuse = max(0f, nm.dot(DiceMeshGeometry.light))
            val spec = max(0f, nm.dot((DiceMeshGeometry.light + toEye).normalized())).pow(SPECULAR_POWER)
            val rim = (1f - facing).pow(RIM_POWER)
            val tint = sample(palette.tints, phase + TINT_DRIFT * (0.5f + 0.5f * nm.dot(DiceMeshGeometry.tintDirection)))
            var color = lerp(palette.deep, tint, BODY_BASE + BODY_DIFFUSE * diffuse)
            color = lerp(color, Color.White, (RIM_WHITE * rim).coerceAtMost(RIM_WHITE_MAX))
            color = lerp(color, Color.White, SPEC_WHITE * spec)
            val alpha = max((ALPHA_BASE + ALPHA_RIM * rim + ALPHA_DIFFUSE * diffuse).coerceAtMost(ALPHA_MAX), spec)
            val quad = polygon(listOf(scr[i][j], scr[i + 1][j], scr[i + 1][j + 1], scr[i][j + 1]))
            silhouette.addPath(quad)
            cells += quad to color.copy(alpha = alpha)
        }
    }

    // Bloom: the outline stroked wide-and-faint to narrow-and-bright.
    GLOW_LAYERS.forEach { (width, alpha) ->
        drawPath(silhouette, glowColor.copy(alpha = (alpha * glow).coerceAtMost(1f)), style = Stroke(unit * width, join = StrokeJoin.Round))
    }
    // Inner light: bright centre, deeper toward the edges.
    drawPath(
        silhouette,
        Brush.radialGradient(
            listOf(lerp(core, Color.White, CORE_WHITE).copy(alpha = CORE_ALPHA), lerp(core, palette.deep, CORE_DEEP).copy(alpha = CORE_ALPHA)),
            center = c,
            radius = unit * CORE_RADIUS,
        ),
    )

    // Far side seen through the glass.
    val lim = 1f - DiceMeshGeometry.ROUNDNESS
    for (face in DiceMeshGeometry.faces) {
        val n = basis.apply(face.normal)
        if (n.dot(cameraPos - n) > 0f) continue
        drawPath(
            polygon(listOf(facePoint(face, -lim, -lim), facePoint(face, lim, -lim), facePoint(face, lim, lim), facePoint(face, -lim, lim))),
            Color.White.copy(alpha = FAR_EDGE_ALPHA),
            style = Stroke(unit * FAR_EDGE_WIDTH, join = StrokeJoin.Round),
        )
        for ((pu, pv) in DiceMeshGeometry.pips(face.value)) {
            drawPath(ring(face, pu, pv, PIP_HALO), glowColor.copy(alpha = FAR_PIP_HALO_ALPHA))
            drawPath(ring(face, pu, pv, PIP_CORE), palette.pip.copy(alpha = FAR_PIP_ALPHA))
        }
    }

    // Near surface, translucent over the far side.
    for ((path, color) in cells) drawPath(path, color)

    // Glare and pips on the faces toward us.
    for (face in DiceMeshGeometry.faces) {
        val n = basis.apply(face.normal)
        if (n.dot(cameraPos - n) <= 0f) continue
        val lit = max(0f, n.dot(DiceMeshGeometry.light))
        drawPath(
            polygon(listOf(facePoint(face, -GLARE, GLARE), facePoint(face, -GLARE_TIP, GLARE), facePoint(face, -GLARE, GLARE_TIP))),
            Color.White.copy(alpha = GLARE_BASE + GLARE_LIT * lit),
        )
        for ((pu, pv) in DiceMeshGeometry.pips(face.value)) {
            drawPath(ring(face, pu, pv, PIP_GLOW), glowColor.copy(alpha = PIP_GLOW_ALPHA))
            drawPath(ring(face, pu, pv, PIP_RING), Color.White.copy(alpha = PIP_RING_ALPHA))
            drawPath(ring(face, pu, pv, PIP_CORE), palette.pip.copy(alpha = PIP_CORE_ALPHA))
        }
    }
}

/** Smoothly picks a colour from [palette]; [t] wraps, so time can keep growing. */
private fun sample(palette: List<Color>, t: Float): Color {
    val x = (t - floor(t)) * palette.size
    val i = x.toInt().coerceAtMost(palette.size - 1)
    return lerp(palette[i], palette[(i + 1) % palette.size], x - i)
}

private val GLOW_LAYERS = listOf(0.70f to 0.06f, 0.45f to 0.10f, 0.24f to 0.16f, 0.10f to 0.28f)
private const val RING_SEGMENTS = 20
private const val QUARTER = 0.25f
private const val UNIT_DIVISOR = 5.4f
private const val HOP_GROW = 0.12f
private const val HOP_LIFT = 0.35f
private const val GLOW_BASE = 0.7f
private const val GLOW_PULSE = 0.3f
private const val GLOW_HOP = 0.6f
private const val CORE_PHASE_SHIFT = 0.15f
private const val HALO_ALPHA = 0.22f
private const val SHADOW_WIDTH = 1.5f
private const val SHADOW_SHRINK = 0.4f
private const val SHADOW_ALPHA = 0.55f
private const val SHADOW_DROP = 1.35f
private const val SHADOW_FLATTEN = 0.35f
private const val SPECULAR_POWER = 60f
private const val RIM_POWER = 2.5f
private const val TINT_DRIFT = 0.45f
private const val BODY_BASE = 0.55f
private const val BODY_DIFFUSE = 0.45f
private const val RIM_WHITE = 0.45f
private const val RIM_WHITE_MAX = 0.8f
private const val SPEC_WHITE = 0.9f
private const val ALPHA_BASE = 0.55f
private const val ALPHA_RIM = 0.55f
private const val ALPHA_DIFFUSE = 0.12f
private const val ALPHA_MAX = 0.95f
private const val CORE_WHITE = 0.15f
private const val CORE_DEEP = 0.75f
private const val CORE_ALPHA = 0.9f
private const val CORE_RADIUS = 1.7f
private const val FAR_EDGE_ALPHA = 0.22f
private const val FAR_EDGE_WIDTH = 0.03f
private const val FAR_PIP_HALO_ALPHA = 0.12f
private const val FAR_PIP_ALPHA = 0.35f
private const val GLARE = 0.58f
private const val GLARE_TIP = 0.10f
private const val GLARE_BASE = 0.06f
private const val GLARE_LIT = 0.22f
private const val PIP_GLOW = 0.25f
private const val PIP_HALO = 0.22f
private const val PIP_RING = 0.19f
private const val PIP_CORE = 0.15f
private const val PIP_GLOW_ALPHA = 0.22f
private const val PIP_RING_ALPHA = 0.85f
private const val PIP_CORE_ALPHA = 0.92f
