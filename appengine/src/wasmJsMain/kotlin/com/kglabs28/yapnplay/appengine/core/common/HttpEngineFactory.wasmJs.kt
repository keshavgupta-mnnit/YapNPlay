package com.kglabs28.yapnplay.appengine.core.common

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

/** The browser's own fetch and WebSocket. */
actual fun createHttpClientEngine(): HttpClientEngine = Js.create()
