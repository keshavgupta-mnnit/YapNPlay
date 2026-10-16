package com.kglabs28.yapnplay.appengine.appmanager

/**
 * Local key-value config storage backing [AppManager].
 * Platform implementations live in androidMain/jvmMain.
 */
interface AppConfigStore {
    fun getAll(): Map<String, String>
    fun saveAll(map: Map<String, String>)
}
