package com.kglabs28.yapnplay.appengine.features.playrooms.domain

/**
 * PlayRoom badge picked when the room is created. The UI maps each kind to
 * its glyph and color; unknown values from the server fall back to [GROUP].
 */
enum class PlayRoomIcon { GROUP, DICE, FAMILY, GAMEPAD }
