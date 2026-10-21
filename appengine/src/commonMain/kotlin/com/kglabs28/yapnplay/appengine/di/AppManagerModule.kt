package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AppManagerHelper
import com.kglabs28.yapnplay.appengine.appmanager.helpers.BackgroundsAppManagerHelper
import com.kglabs28.yapnplay.appengine.appmanager.helpers.CityBazaarAppManagerHelper
import com.kglabs28.yapnplay.appengine.appmanager.helpers.GamesAppManagerHelper
import org.koin.dsl.module

/**
 * AppManager and its module helpers. The [AppConfigStore] and the
 * [RemoteConfigSource] come from the platform module — construction loads
 * local values instantly, so the app never waits on the network.
 */
val appManagerModule = module {
    single { AppManager(get()) }
    single { AppManagerHelper(get()) }
    single { GamesAppManagerHelper(get()) }
    single { CityBazaarAppManagerHelper(get()) }
    single { BackgroundsAppManagerHelper(get()) }
}
