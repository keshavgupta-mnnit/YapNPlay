package com.kglabs28.yapnplay.appengine.core.common

import kotlinx.coroutines.flow.StateFlow

/** The player's own table choices, kept on the device. [moneyPopups] off: a short coin animation instead of the popup. */
interface TablePreferences {
    val moneyPopups: StateFlow<Boolean>
    fun setMoneyPopups(on: Boolean)
}
