package com.kglabs28.yapnplay.appengine.core.audio

import kotlin.js.Promise

/*
 * The browser's Web Audio side of [WebGameAudio]: one AudioContext, each
 * sound decoded once (instant, overlapping plays), the music as one looping
 * source. iPhone Safari keeps audio suspended until a tap: the first tap
 * resumes it (sounds asked for before then are simply skipped).
 */

/** Downloads and decodes the sound at [url] once, kept as [name]. */
@JsFun(
    """(name, url) => {
    const s = globalThis.__yapAudio || (globalThis.__yapAudio = { ctx: null, buffers: {}, music: null });
    if (!s.ctx) {
        const Ctx = globalThis.AudioContext || globalThis.webkitAudioContext;
        if (!Ctx) return Promise.resolve(null);
        s.ctx = new Ctx();
        const unlock = () => { if (s.ctx.state !== 'running') s.ctx.resume().catch(() => {}); };
        document.addEventListener('pointerdown', unlock, true);
        document.addEventListener('touchend', unlock, true);
        document.addEventListener('keydown', unlock, true);
    }
    return fetch(url)
        .then((r) => r.arrayBuffer())
        .then((data) => new Promise((ok, fail) => s.ctx.decodeAudioData(data, ok, fail)))
        .then((buffer) => { s.buffers[name] = buffer; return null; });
}"""
)
internal external fun webAudioLoad(name: String, url: String): Promise<JsAny?>

/** Plays [name] once at [volume] (0..1); nothing if it isn't loaded or audio is still locked. */
@JsFun(
    """(name, volume) => {
    const s = globalThis.__yapAudio;
    if (!s || !s.ctx || s.ctx.state !== 'running' || !s.buffers[name]) return;
    const source = s.ctx.createBufferSource();
    source.buffer = s.buffers[name];
    const gain = s.ctx.createGain();
    gain.gain.value = volume;
    source.connect(gain).connect(s.ctx.destination);
    source.start();
}"""
)
internal external fun webAudioPlay(name: String, volume: Double)

/** Loops [name] at [volume] until [webAudioStopLoop]; starts by itself once audio unlocks. False: not loaded yet. */
@JsFun(
    """(name, volume) => {
    const s = globalThis.__yapAudio;
    if (!s || !s.ctx || !s.buffers[name]) return false;
    if (s.music) return true;
    const source = s.ctx.createBufferSource();
    source.buffer = s.buffers[name];
    source.loop = true;
    const gain = s.ctx.createGain();
    gain.gain.value = volume;
    source.connect(gain).connect(s.ctx.destination);
    source.start();
    s.music = source;
    return true;
}"""
)
internal external fun webAudioStartLoop(name: String, volume: Double): Boolean

@JsFun(
    """() => {
    const s = globalThis.__yapAudio;
    if (!s || !s.music) return;
    try { s.music.stop(); } catch (e) {}
    s.music = null;
}"""
)
internal external fun webAudioStopLoop()
