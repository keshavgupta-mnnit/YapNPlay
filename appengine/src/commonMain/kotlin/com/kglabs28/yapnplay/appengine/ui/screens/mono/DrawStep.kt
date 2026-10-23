package com.kglabs28.yapnplay.appengine.ui.screens.mono

import kotlinx.datetime.Instant

/** One automatic draw on the user's behalf: whose turn, which turn (its deadline), and how many cards they held. */
data class DrawStep(val seat: Int, val turnEndsAt: Instant?, val handSize: Int)
