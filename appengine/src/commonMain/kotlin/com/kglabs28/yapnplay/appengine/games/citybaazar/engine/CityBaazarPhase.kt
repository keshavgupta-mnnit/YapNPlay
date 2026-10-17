package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

/**
 * What happens now: the current seat rolls (or rolls for doubles in jail,
 * up to 3 times, then chooses fine or half rent), decides on buying or
 * building the tile landed on, raises money for a debt, or waits while
 * their property is auctioned; or nothing (finished).
 */
enum class CityBaazarPhase { ROLL, JAIL_ROLL, JAIL_CHOICE, BUY, BUILD, DEBT, AUCTION, FINISHED }
