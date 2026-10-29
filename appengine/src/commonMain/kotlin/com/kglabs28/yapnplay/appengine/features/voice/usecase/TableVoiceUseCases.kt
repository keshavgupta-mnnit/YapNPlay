package com.kglabs28.yapnplay.appengine.features.voice.usecase

/** What a game table needs to choose who the user hears (each person, or nobody: the speaker; only on this device) and whether it needs voice at all. */
class TableVoiceUseCases(
    val observeMuted: ObserveLocallyMutedUseCase,
    val setMuted: SetLocallyMutedUseCase,
    val observeSpeakerOn: ObserveSpeakerOnUseCase,
    val setSpeakerOn: SetSpeakerOnUseCase,
    val setVoiceWanted: SetVoiceWantedUseCase,
)
