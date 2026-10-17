package com.kglabs28.yapnplay.appengine.games.elements.dice.utils

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Tiny 3D vector for the dice renderer (x right, y up, z toward the viewer). */
internal data class Vector3D(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: Vector3D) = Vector3D(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Vector3D) = Vector3D(x - o.x, y - o.y, z - o.z)
    operator fun times(k: Float) = Vector3D(x * k, y * k, z * k)
    fun dot(o: Vector3D) = x * o.x + y * o.y + z * o.z
    fun length() = sqrt(dot(this))
    fun normalized(): Vector3D {
        val l = length()
        return if (l < EPSILON) this else this * (1f / l)
    }

    fun rotX(deg: Float): Vector3D {
        val r = deg.toRadians(); val c = cos(r); val s = sin(r)
        return Vector3D(x, y * c - z * s, y * s + z * c)
    }

    fun rotY(deg: Float): Vector3D {
        val r = deg.toRadians(); val c = cos(r); val s = sin(r)
        return Vector3D(x * c + z * s, y, -x * s + z * c)
    }

    fun rotZ(deg: Float): Vector3D {
        val r = deg.toRadians(); val c = cos(r); val s = sin(r)
        return Vector3D(x * c - y * s, x * s + y * c, z)
    }

    private fun Float.toRadians() = this * PI.toFloat() / HALF_TURN_DEG

    private companion object {
        const val EPSILON = 1e-6f
        const val HALF_TURN_DEG = 180f
    }
}
