package com.kglabs28.yapnplay.appengine.features.nearby.domain

/** A phone setting Nearby play needs switched on. */
enum class NearbyRadio {
    BLUETOOTH,
    WIFI,
    /** Many phones need Location on to find others, on every Android version. */
    LOCATION,
}
