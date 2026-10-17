package com.kglabs28.yapnplay.appengine.games.elements.dice.utils

/**
 * One face of the die: its pip count, outward normal and the two in-face
 * axes. Opposite faces always add up to 7, like a real die.
 */
internal class DiceFaceLayout(val value: Int, val normal: Vector3D, val u: Vector3D, val v: Vector3D)
