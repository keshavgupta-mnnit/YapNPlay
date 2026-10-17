package com.kglabs28.yapnplay.appengine.features.moderation.usecase

/**
 * Every moderation use case a game screen needs (report, vote, outcome,
 * penalty, ad), grouped so a screen's view model takes one dependency.
 */
class ModerationUseCases(
    val report: ReportPlayerUseCase,
    val voteRequests: ObserveReportVoteRequestsUseCase,
    val castVote: CastReportVoteUseCase,
    val outcomes: ObserveReportOutcomesUseCase,
    val penalty: ObservePenaltyUseCase,
    val clearPenaltyWithAd: ClearPenaltyWithAdUseCase,
)
