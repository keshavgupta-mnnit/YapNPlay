package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.about.data.AboutRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.about.data.StaticAppInfoSource
import com.kglabs28.yapnplay.appengine.features.about.domain.AboutRepository
import com.kglabs28.yapnplay.appengine.features.about.domain.GetAppInfo
import com.kglabs28.yapnplay.appengine.features.about.ui.AboutViewModel
import org.koin.dsl.module

/** About sample feature: source → repository → use case → view model. */
val aboutModule = module {
    single { StaticAppInfoSource() }
    single<AboutRepository> { AboutRepositoryImpl(get()) }
    factory { GetAppInfo(get()) }
    factory { AboutViewModel(get(), get()) }
}
