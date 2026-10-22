package com.kglabs28.yapnplay.appengine.features.seathandover.presentation

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Instant

/**
 * The exit dialog every game's table shares (Royal Ludo's), so leaving is
 * always confirmed: opened by Exit, it closes by itself after
 * [AppConstants.EXIT_DIALOG_SECONDS]. In a room a player picks who plays
 * on for them (a CPU first, then each spectator watching); in Quick Play
 * nobody does — the player is out of the match. A view model keeps one
 * and shows [ui] on every render.
 */
class LeaveDialogState {
    private var deadline: Instant? = null
    private var pick = 0

    fun open(now: Instant) {
        deadline = now + AppConstants.EXIT_DIALOG_SECONDS.seconds
        pick = 0
    }

    fun close() {
        deadline = null
    }

    fun pick(index: Int) {
        pick = index
    }

    /** The dialog to show now, or null when closed (or its time ran out, which closes it). */
    fun ui(now: Instant, spectatorNames: List<String>, hasSeat: Boolean, roomMatch: Boolean): LeaveDialogUi? {
        val end = deadline ?: return null
        if (now >= end) {
            close()
            return null
        }
        val options = if (hasSeat && roomMatch && spectatorNames.isNotEmpty()) SeatHandoverUiMapper.options(spectatorNames, withBot = true) else emptyList()
        return LeaveDialogUi(
            message = when {
                !hasSeat -> TableStrings.seatSpectatorLeaveBody
                !roomMatch -> TableStrings.leaveQuickPlayBody
                options.isNotEmpty() -> TableStrings.seatLeaveBody
                else -> TableStrings.leaveMatchBody
            },
            options = options,
            selectedIndex = if (options.isEmpty()) null else pick,
            countdown = TableStrings.seatCountdown((end - now).inWholeSeconds.coerceAtLeast(0)),
        )
    }

    /** Who takes the seat: the picked spectator's id, or null for a CPU. */
    fun giveTo(spectatorIds: List<String>): String? = SeatHandoverUiMapper.giveTo(pick, spectatorIds, withBot = true)
}
