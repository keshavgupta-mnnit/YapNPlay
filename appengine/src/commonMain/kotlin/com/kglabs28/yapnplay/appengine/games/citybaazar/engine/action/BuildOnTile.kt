package com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action

import com.kglabs28.yapnplay.appengine.games.base.play.Action

/** Build a house (or the hotel) on [tile]. */
data class BuildOnTile(val tile: Int) : Action
