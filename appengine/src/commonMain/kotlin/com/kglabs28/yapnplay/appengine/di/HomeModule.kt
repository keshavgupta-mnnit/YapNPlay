package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.ui.screens.home.HomeViewModel
import org.koin.dsl.module

val homeModule = module {
    factory { HomeViewModel(get(), get()) }

}
