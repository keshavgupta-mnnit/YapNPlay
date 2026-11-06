package com.kglabs28.yapnplay.appengine.games.cyberblast.domain

/** One snapshot: the match and the board as it stood (with its blockers and walls). */
data class CyberUpdate(val match: CyberMatch, val frame: CyberFrameData)
