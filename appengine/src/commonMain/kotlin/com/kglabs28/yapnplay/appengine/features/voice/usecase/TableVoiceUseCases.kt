package com.kglabs28.yapnplay.appengine.features.voice.usecase

/** What a game table needs to choose who the user hears: each person, or nobody (the speaker). Only on this device. */
class TableVoiceUseCases(
    val observeMuted: ObserveLocallyMutedUseCase,
    val setMuted: SetLocallyMutedUseCase,
    val observeSpeakerOn: ObserveSpeakerOnUseCase,
    val setSpeakerOn: SetSpeakerOnUseCase,
)
