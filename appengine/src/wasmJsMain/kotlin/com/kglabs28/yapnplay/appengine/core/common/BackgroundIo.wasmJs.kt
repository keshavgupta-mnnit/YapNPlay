package com.kglabs28.yapnplay.appengine.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** The browser has one thread: suspending network calls never block it. */
actual val BackgroundIo: CoroutineDispatcher = Dispatchers.Default
