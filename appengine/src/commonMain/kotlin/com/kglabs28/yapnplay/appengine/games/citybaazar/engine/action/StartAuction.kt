package com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action

import com.kglabs28.yapnplay.appengine.games.base.play.Action

/** Put [tile] (no buildings) up for auction among the other players, to raise money for a debt. */
data class StartAuction(val tile: Int) : Action
