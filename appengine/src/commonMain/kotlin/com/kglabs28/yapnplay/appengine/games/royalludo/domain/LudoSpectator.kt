package com.kglabs28.yapnplay.appengine.games.royalludo.domain

/** Someone watching (room spectators); only shown in the top bar. */
data class LudoSpectator(val userId: String, val name: String, val micOn: Boolean, val avatarId: String = "")
