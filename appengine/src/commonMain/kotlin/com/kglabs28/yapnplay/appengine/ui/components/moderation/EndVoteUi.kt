package com.kglabs28.yapnplay.appengine.ui.components.moderation

/** The end-game vote as the user sees it: buttons only if [canVote] (a player still in who hasn't voted). */
data class EndVoteUi(val body: String, val tally: String, val status: String?, val canVote: Boolean, val countdown: String)
