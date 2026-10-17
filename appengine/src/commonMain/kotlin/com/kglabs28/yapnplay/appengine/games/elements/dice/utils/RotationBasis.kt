package com.kglabs28.yapnplay.appengine.games.elements.dice.utils

/** A rotation stored as where the three axes end up; applying it is three multiplications. */
internal class RotationBasis(private val ex: Vector3D, private val ey: Vector3D, private val ez: Vector3D) {
    fun apply(p: Vector3D) = ex * p.x + ey * p.y + ez * p.z
}
