package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.ui.screens.games.GamesViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.main.MainViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.rooms.RoomsViewModel
import org.koin.dsl.module

/**
 * Dashboard bindings: scaffold plus the Games and Rooms tabs.
 */
val mainModule = module {
    factory { MainViewModel(get(), get()) }
    factory { GamesViewModel(get()) }
    factory { RoomsViewModel(get()) }
}
