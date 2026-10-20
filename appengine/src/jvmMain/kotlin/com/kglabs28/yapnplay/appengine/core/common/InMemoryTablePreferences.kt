package com.kglabs28.yapnplay.appengine.core.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Desktop / tests: [TablePreferences] for this run only. */
class InMemoryTablePreferences : TablePreferences {
    private val popups = MutableStateFlow(true)
    override val moneyPopups: StateFlow<Boolean> = popups.asStateFlow()
    override fun setMoneyPopups(on: Boolean) {
        popups.value = on
    }
}
