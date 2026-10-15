package com.kglabs28.yapnplay.appengine.features.about.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult

/**
 * Provides app metadata. The ViewModel never touches a data source directly.
 */
interface AboutRepository {
    suspend fun appInfo(): AppResult<AppInfo>
}
