package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.playinvites.data.PlayInvitesRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.playinvites.repository.PlayInvitesRepository
import com.kglabs28.yapnplay.appengine.features.playinvites.usecase.AnswerPlayInviteUseCase
import com.kglabs28.yapnplay.appengine.features.playinvites.usecase.ObservePlayInviteUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.playinvite.PlayInviteViewModel
import org.koin.dsl.module

/** Play-now invites to Quick Play parties, online and nearby, asked wherever the user is. */
val playInvitesModule = module {
    single<PlayInvitesRepository> { PlayInvitesRepositoryImpl(get(), get(), get(), get(APP_SCOPE)) }
    factory { ObservePlayInviteUseCase(get()) }
    factory { AnswerPlayInviteUseCase(get()) }
    factory { PlayInviteViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
