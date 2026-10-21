package com.kglabs28.yapnplay.appengine.features.playrooms.domain

/** A group just created: where to open it and what to share. */
data class CreatedPlayRoom(
    val roomId: String,
    val name: String,
    val code: String,
)
