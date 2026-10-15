package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.ui.screens.gallery.GalleryViewModel
import org.koin.dsl.module

/** Screen-level view models that own no repository (gallery, placeholders). */
val uiModule = module {
    factory { GalleryViewModel(get()) }
}
