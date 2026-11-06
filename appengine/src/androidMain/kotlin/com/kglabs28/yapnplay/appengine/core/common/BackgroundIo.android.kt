package com.kglabs28.yapnplay.appengine.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val BackgroundIo: CoroutineDispatcher = Dispatchers.IO
