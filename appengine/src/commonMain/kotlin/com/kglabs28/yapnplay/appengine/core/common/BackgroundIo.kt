package com.kglabs28.yapnplay.appengine.core.common

import kotlinx.coroutines.CoroutineDispatcher

/** Where blocking work (network, storage) runs: the IO pool on the phone and desktop; the browser has a single thread. */
expect val BackgroundIo: CoroutineDispatcher
