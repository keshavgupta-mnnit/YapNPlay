package com.kglabs28.yapnplay.appengine.ui.components.table

/** A player the Rule of 7 can swap hands with: their seat, name, avatar and how many cards they hold. */
data class MonoSwapTargetUi(val seat: Int, val name: String, val avatarId: String, val cards: Int)
