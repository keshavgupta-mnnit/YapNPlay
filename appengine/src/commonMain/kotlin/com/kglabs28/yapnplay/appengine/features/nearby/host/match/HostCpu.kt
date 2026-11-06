package com.kglabs28.yapnplay.appengine.features.nearby.host.match

/** A CPU seat's identity at a Nearby table: "CPU 2", its id and its avatar. */
object HostCpu {
    fun userId(number: Int): String = USER_PREFIX + number
    fun name(number: Int): String = NAME + number
    fun avatar(number: Int): String = if (number % 2 == 1) AVATAR_ODD else AVATAR_EVEN

    private const val USER_PREFIX = "cpu-"
    private const val NAME = "CPU "
    private const val AVATAR_ODD = "a09"
    private const val AVATAR_EVEN = "a18"
}
