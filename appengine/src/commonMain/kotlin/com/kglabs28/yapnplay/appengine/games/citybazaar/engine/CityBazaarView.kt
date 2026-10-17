package com.kglabs28.yapnplay.appengine.games.citybazaar.engine

import com.kglabs28.yapnplay.appengine.games.base.play.GameView

/** No hidden information in City Bazaar: everyone sees the whole table. */
data class CityBazaarView(val state: CityBazaarState) : GameView
