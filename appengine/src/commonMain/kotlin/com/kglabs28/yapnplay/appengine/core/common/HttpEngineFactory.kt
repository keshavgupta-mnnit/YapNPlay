package com.kglabs28.yapnplay.appengine.core.common

import io.ktor.client.engine.HttpClientEngine

/**
 * KMP expects the HTTP engine from the platform: OkHttp on Android/JVM.
 * One expect here, one actual per platform source set.
 */
expect fun createHttpClientEngine(): HttpClientEngine
