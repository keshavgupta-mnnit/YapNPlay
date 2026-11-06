package com.kglabs28.yapnplay.appengine.features.voice

import kotlin.js.Promise

/*
 * The thin JavaScript side of web voice, over LiveKit's browser SDK (the
 * page loads it as the global `LivekitClient`). It keeps the one Room and
 * reports to Kotlin as (event, value) strings; the logic stays in
 * [LiveKitWebVoiceEngine]. Identities are user ids, lists are comma-joined.
 *
 * Events: "speakers" (others speaking), "me" ("1"/"0"), "receiving" (others
 * whose audio this browser has), "reconnected", "disconnected".
 */

/** Leaves any Room and joins [url] with [token]; resolves once joined. */
@JsFun(
    """(url, token, onEvent) => {
    const LK = globalThis.LivekitClient;
    if (!LK) return Promise.reject(new Error('LiveKit library not loaded'));
    const s = globalThis.__yapVoice || (globalThis.__yapVoice = { room: null, muted: new Set(), speakerOn: true });
    if (s.room) { try { s.room.disconnect(); } catch (e) {} }
    const room = new LK.Room({ audioCaptureDefaults: { echoCancellation: true, noiseSuppression: true, autoGainControl: true } });
    s.room = room;
    const E = LK.RoomEvent;
    const audioPubs = (p) => [...p.trackPublications.values()].filter((pub) => pub.kind === 'audio');
    s.apply = () => {
        for (const p of room.remoteParticipants.values()) {
            const hear = s.speakerOn && !s.muted.has(p.identity);
            for (const pub of audioPubs(p)) if (pub.isDesired !== hear) pub.setSubscribed(hear);
        }
    };
    const receiving = () => {
        if (s.room !== room) return;
        onEvent('receiving', [...room.remoteParticipants.values()].filter((p) => audioPubs(p).some((pub) => pub.track)).map((p) => p.identity).join(','));
    };
    let box = document.getElementById('yap-voice-audio');
    if (!box) { box = document.createElement('div'); box.id = 'yap-voice-audio'; box.style.display = 'none'; document.documentElement.appendChild(box); }
    room.on(E.ActiveSpeakersChanged, (speakers) => {
        if (s.room !== room) return;
        onEvent('speakers', speakers.filter((p) => p !== room.localParticipant).map((p) => p.identity).join(','));
        onEvent('me', speakers.includes(room.localParticipant) ? '1' : '0');
    });
    room.on(E.TrackSubscribed, (track) => { if (track.kind === 'audio') box.appendChild(track.attach()); s.apply(); receiving(); });
    room.on(E.TrackUnsubscribed, (track) => { track.detach().forEach((el) => el.remove()); receiving(); });
    room.on(E.TrackPublished, () => { s.apply(); receiving(); });
    room.on(E.TrackUnpublished, receiving);
    room.on(E.ParticipantConnected, () => { s.apply(); receiving(); });
    room.on(E.ParticipantDisconnected, receiving);
    room.on(E.Reconnected, () => { if (s.room === room) onEvent('reconnected', ''); });
    room.on(E.Disconnected, () => { if (s.room === room) { s.room = null; onEvent('disconnected', ''); } });
    if (!s.unlock) {
        // iPhone Safari plays sound only after a tap: the first tap after joining starts it.
        s.unlock = () => { const r = s.room; if (r && !r.canPlaybackAudio) r.startAudio().catch(() => {}); };
        document.addEventListener('pointerdown', s.unlock, true);
        document.addEventListener('touchend', s.unlock, true);
    }
    return room.connect(url, token).then(() => { s.apply(); receiving(); return null; });
}"""
)
internal external fun liveKitConnect(url: String, token: String, onEvent: (String, String) -> Unit): Promise<JsAny?>

/** Leaves the Room (its audio elements go with it). */
@JsFun(
    """() => {
    const s = globalThis.__yapVoice;
    const room = s && s.room;
    if (!room) return Promise.resolve(null);
    s.room = null;
    const box = document.getElementById('yap-voice-audio');
    if (box) box.replaceChildren();
    return room.disconnect().then(() => null);
}"""
)
internal external fun liveKitDisconnect(): Promise<JsAny?>

/** Publishes or mutes the mic (the browser asks for the microphone the first time). */
@JsFun(
    """(enabled) => {
    const s = globalThis.__yapVoice;
    if (!s || !s.room) return Promise.resolve(null);
    return s.room.localParticipant.setMicrophoneEnabled(enabled).then(() => null);
}"""
)
internal external fun liveKitSetMic(enabled: Boolean): Promise<JsAny?>

/** True when a live, unmuted mic track is going out. */
@JsFun(
    """() => {
    const s = globalThis.__yapVoice;
    if (!s || !s.room) return false;
    const pub = s.room.localParticipant.getTrackPublication(globalThis.LivekitClient.Track.Source.Microphone);
    return !!(pub && !pub.isMuted && pub.track);
}"""
)
internal external fun liveKitMicLive(): Boolean

/** Who this browser doesn't hear (comma-joined ids), and whether it hears anyone; applied now and on later joins. */
@JsFun(
    """(mutedIds, speakerOn) => {
    const s = globalThis.__yapVoice || (globalThis.__yapVoice = { room: null, muted: new Set(), speakerOn: true });
    s.muted = new Set(mutedIds ? mutedIds.split(',') : []);
    s.speakerOn = speakerOn;
    if (s.room && s.apply) s.apply();
}"""
)
internal external fun liveKitSetListening(mutedIds: String, speakerOn: Boolean)
