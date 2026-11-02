package com.kglabs28.yapnplay.appengine.core.ads

/** The ad pill's colours (ARGB) and sizes (px), taken from the app theme so the Android views match it. */
data class AdPillLook(
    val fillTop: Int,
    val fillBottom: Int,
    val rim: Int,
    val text: Int,
    val badgeFill: Int,
    val actionFill: Int,
    val actionText: Int,
    val height: Int,
    val icon: Int,
    val inset: Int,
    val rimWidth: Int,
    val textSize: Float,
    val smallTextSize: Float,
    /** Narrower than this (px), the advertiser's icon is left out so the headline stays readable. */
    val compactBelow: Int,
    /** The pill's design width (px): at it every part has its full size; narrower, they shrink together. */
    val fullWidth: Int,
)
