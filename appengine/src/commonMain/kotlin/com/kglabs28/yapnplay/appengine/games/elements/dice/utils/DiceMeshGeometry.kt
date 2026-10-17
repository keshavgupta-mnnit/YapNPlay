package com.kglabs28.yapnplay.appengine.games.elements.dice.utils

import kotlin.math.floor

/**
 * The die's shape and orientation maths: a rounded glass cube built once
 * as a mesh, the pip layouts, and the angles that put any face on top.
 * Local axes: x right, y up, z toward the viewer; 1 up, 6 down, 2 front,
 * 5 back, 3 right, 4 left (opposite faces sum to 7).
 */
internal object DiceMeshGeometry {

    /** Corner roundness; above ~0.36 the pips need a tighter layout. */
    const val ROUNDNESS = 0.34f
    /** Cells across each rounded band: more is smoother but slower. */
    private const val BAND_STEPS = 6
    /** Resting turn about the vertical axis. */
    const val YAW = 30f
    /** Camera height: higher shows more of the top face. */
    private const val TILT = 55f
    /** Camera distance for perspective. */
    const val CAMERA = 6f
    /** How far pips sit from a face centre (must stay inside 1 - ROUNDNESS). */
    private const val PIP_SPREAD = 0.38f

    val light = Vector3D(-0.35f, 0.75f, 0.55f).normalized()
    /** Facing direction that drives the colour drift across the surface. */
    val tintDirection = Vector3D(0.6f, 0.5f, 0.6f).normalized()

    val faces = listOf(
        DiceFaceLayout(1, Vector3D(0f, 1f, 0f), Vector3D(1f, 0f, 0f), Vector3D(0f, 0f, -1f)),
        DiceFaceLayout(6, Vector3D(0f, -1f, 0f), Vector3D(1f, 0f, 0f), Vector3D(0f, 0f, 1f)),
        DiceFaceLayout(2, Vector3D(0f, 0f, 1f), Vector3D(1f, 0f, 0f), Vector3D(0f, 1f, 0f)),
        DiceFaceLayout(5, Vector3D(0f, 0f, -1f), Vector3D(-1f, 0f, 0f), Vector3D(0f, 1f, 0f)),
        DiceFaceLayout(3, Vector3D(1f, 0f, 0f), Vector3D(0f, 0f, -1f), Vector3D(0f, 1f, 0f)),
        DiceFaceLayout(4, Vector3D(-1f, 0f, 0f), Vector3D(0f, 0f, 1f), Vector3D(0f, 1f, 0f)),
    )

    /** Pip positions on a face, in face units around its centre. */
    fun pips(value: Int): List<Pair<Float, Float>> {
        val s = PIP_SPREAD
        val c = 0f to 0f
        return when (value) {
            1 -> listOf(c)
            2 -> listOf(-s to s, s to -s)
            3 -> listOf(-s to s, c, s to -s)
            4 -> listOf(-s to s, s to s, -s to -s, s to -s)
            5 -> listOf(-s to s, s to s, c, -s to -s, s to -s)
            else -> listOf(-s to s, s to s, -s to 0f, s to 0f, -s to -s, s to -s)
        }
    }

    /** One rounded-surface point: position and outward normal. */
    class Vertex(val pos: Vector3D, val normal: Vector3D)

    /** Per face, a grid of surface points: fine across the rounded bands, one cell over the flat middle. */
    val mesh: List<Array<Array<Vertex>>> by lazy {
        val flat = 1f - ROUNDNESS
        val grid = buildList {
            for (i in 0..BAND_STEPS) add(-1f + ROUNDNESS * i / BAND_STEPS)
            for (i in 0..BAND_STEPS) add(flat + ROUNDNESS * i / BAND_STEPS)
        }
        faces.map { f ->
            Array(grid.size) { i ->
                Array(grid.size) { j -> roundedPoint(f.normal + f.u * grid[i] + f.v * grid[j]) }
            }
        }
    }

    /** Nearest point on the inner cube, then one corner radius outward. */
    private fun roundedPoint(p: Vector3D): Vertex {
        val lim = 1f - ROUNDNESS
        val inner = Vector3D(p.x.coerceIn(-lim, lim), p.y.coerceIn(-lim, lim), p.z.coerceIn(-lim, lim))
        val n = (p - inner).normalized()
        return Vertex(inner + n * ROUNDNESS, n)
    }

    /** For each face value (index), the x and z angles that bring it to the top. */
    private val BASE_X = floatArrayOf(0f, 0f, -90f, 0f, 0f, 90f, 180f)
    private val BASE_Z = floatArrayOf(0f, 0f, 0f, 90f, -90f, 0f, 0f)

    fun baseX(face: Int) = BASE_X[face]
    fun baseZ(face: Int) = BASE_Z[face]

    fun restingAngles(face: Int) = DiceOrientation(BASE_X[face], YAW, BASE_Z[face])

    /** Smallest angle at or above [current] equal to [base] (mod 360), plus [turns] full turns. */
    fun landing(current: Float, base: Float, turns: Int): Float =
        base + FULL_TURN * (floor((current - base) / FULL_TURN).toInt() + turns)

    /** Applies the die's rotation, then the camera tilt. */
    fun Vector3D.oriented(a: DiceOrientation) = rotX(a.x).rotZ(a.z).rotY(a.y).rotX(TILT)

    const val FULL_TURN = 360f
    const val QUARTER_TURN = 90f
}
