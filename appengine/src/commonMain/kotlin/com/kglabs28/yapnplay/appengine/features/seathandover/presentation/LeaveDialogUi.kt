package com.kglabs28.yapnplay.appengine.features.seathandover.presentation

/** The exit dialog as shown: its line, who may take the seat (the picked one), and the time left before it closes. */
data class LeaveDialogUi(
    val message: String,
    val options: List<SeatPickOptionUi>,
    val selectedIndex: Int?,
    val countdown: String,
)
