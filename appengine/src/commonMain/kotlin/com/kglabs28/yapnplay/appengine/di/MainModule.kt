package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.ui.components.moderation.AppSettingsController
import com.kglabs28.yapnplay.appengine.ui.screens.gamezone.GameZoneViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.main.MainViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.playhub.PlayHubViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.playrooms.PlayRoomsViewModel
import org.koin.dsl.module

/**
 * Dashboard bindings: scaffold plus the Games tab (Quick Play hub, Game Zone) and Rooms tab.
 */
val mainModule = module {
    factory { AppSettingsController(get(), get()) }
    factory { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { GameZoneViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { PlayHubViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { PlayRoomsViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
