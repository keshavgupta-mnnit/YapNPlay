package com.kglabs28.yapnplay.appengine.core.common

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** [TablePreferences] in SharedPreferences. */
class AndroidTablePreferences(context: Context) : TablePreferences {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val popups = MutableStateFlow(prefs.getBoolean(KEY_MONEY_POPUPS, true))
    override val moneyPopups: StateFlow<Boolean> = popups.asStateFlow()

    override fun setMoneyPopups(on: Boolean) {
        prefs.edit().putBoolean(KEY_MONEY_POPUPS, on).apply()
        popups.value = on
    }

    private companion object {
        const val PREFS = "table_prefs"
        const val KEY_MONEY_POPUPS = "money_popups"
    }
}
