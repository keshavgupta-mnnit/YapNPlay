package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.play.GameView

/** No hidden information in City Baazar: everyone sees the whole table. */
data class CityBaazarView(val state: CityBaazarState) : GameView
