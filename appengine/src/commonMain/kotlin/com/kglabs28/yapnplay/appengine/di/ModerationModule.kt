package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.moderation.data.ModerationRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.moderation.data.remote.ModerationClient
import com.kglabs28.yapnplay.appengine.features.moderation.data.remote.ModerationClientImpl
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRepository
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.CastReportVoteUseCase
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ClearPenaltyWithAdUseCase
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ObservePenaltyUseCase
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ObserveReportOutcomesUseCase
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ObserveReportVoteRequestsUseCase
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ReportPlayerUseCase
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ModerationUseCases
import org.koin.dsl.module

/** Voice-chat reports, votes and penalties — shared by every game and room. */
val moderationModule = module {
    single<ModerationClient> { ModerationClientImpl(get(), get()) }
    single<ModerationRepository> { ModerationRepositoryImpl(get(), get()) }
    factory { ReportPlayerUseCase(get()) }
    factory { ObserveReportVoteRequestsUseCase(get()) }
    factory { CastReportVoteUseCase(get()) }
    factory { ObserveReportOutcomesUseCase(get()) }
    factory { ObservePenaltyUseCase(get()) }
    factory { ClearPenaltyWithAdUseCase(get(), get()) }
    factory { ModerationUseCases(get(), get(), get(), get(), get(), get()) }
}
