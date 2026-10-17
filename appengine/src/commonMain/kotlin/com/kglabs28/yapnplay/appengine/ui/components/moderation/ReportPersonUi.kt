package com.kglabs28.yapnplay.appengine.ui.components.moderation

/** Someone the user can report: a player or a spectator. */
data class ReportPersonUi(val userId: String, val name: String, val role: String)
