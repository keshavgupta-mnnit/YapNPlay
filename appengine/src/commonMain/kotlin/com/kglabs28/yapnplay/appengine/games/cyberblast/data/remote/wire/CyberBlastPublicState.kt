package com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote.wire

import kotlinx.serialization.Serializable

/**
 * 0.13.0 Cyber Blast's `MatchSnapshot.publicState`: the board's size and
 * fixed rules, and the latest [frame] (with its blockers and walls). Frames
 * then come about [frameMs] apart as `MatchRelay`.
 */
@Serializable
data class CyberBlastPublicState(
    val cols: Int,
    val rows: Int,
    val lives: Int,
    val frameMs: Long,
    val fuseMs: Long,
    val boostMs: Long,
    val frame: CyberFrame,
)
