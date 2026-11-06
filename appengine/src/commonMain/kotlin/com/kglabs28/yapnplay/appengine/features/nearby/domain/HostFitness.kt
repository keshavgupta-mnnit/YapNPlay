package com.kglabs28.yapnplay.appengine.features.nearby.domain

/**
 * How well this phone could host a Nearby party, 0..100: battery first
 * (a host that dies mid-game forces another takeover), charging, memory
 * and processor cores. The party hands over to the highest score.
 */
fun interface HostFitness {
    fun score(): Int
}
