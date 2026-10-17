package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

/**
 * What the current seat must do: roll (or build first), choose how to leave
 * jail, decide on buying or building on the tile landed on, raise money
 * for a debt, or nothing (finished).
 */
enum class CityBaazarPhase { ROLL, JAIL_CHOICE, BUY, BUILD, DEBT, FINISHED }
