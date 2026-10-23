package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.feedback.data.FeedbackRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackRepository
import com.kglabs28.yapnplay.appengine.features.feedback.usecase.GetFeedbackConversationUseCase
import com.kglabs28.yapnplay.appengine.features.feedback.usecase.GetMyFeedbackUseCase
import com.kglabs28.yapnplay.appengine.features.feedback.usecase.SubmitFeedbackUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.feedback.FeedbackViewModel
import org.koin.dsl.module

/** Feedback & support: the player's messages to the team and the team's replies. */
val feedbackModule = module {
    single<FeedbackRepository> { FeedbackRepositoryImpl(get(), get()) }
    factory { SubmitFeedbackUseCase(get()) }
    factory { GetMyFeedbackUseCase(get()) }
    factory { GetFeedbackConversationUseCase(get()) }
    factory { FeedbackViewModel(get(), get(), get(), get()) }
}
