package com.kglabs28.yapnplay.appengine.games.royalludo.domain

/** One server message: the new match plus the events that led to it. */
data class LudoUpdate(val match: LudoMatch, val events: List<LudoEvent>)
