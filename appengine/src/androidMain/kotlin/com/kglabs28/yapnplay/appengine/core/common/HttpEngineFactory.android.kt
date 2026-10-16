package com.kglabs28.yapnplay.appengine.core.common

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpClientEngine(): HttpClientEngine = OkHttp.create()
