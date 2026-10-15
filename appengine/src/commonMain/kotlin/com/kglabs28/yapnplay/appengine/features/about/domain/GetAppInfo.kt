package com.kglabs28.yapnplay.appengine.features.about.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult

/**
 * Use case: load the info shown on the About screen.
 * Exists because it is real logic (repository call + result mapping),
 * not a pass-through.
 */
class GetAppInfo(private val repository: AboutRepository) {
    suspend operator fun invoke(): AppResult<AppInfo> = repository.appInfo()
}
