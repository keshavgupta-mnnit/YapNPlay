package com.kglabs28.yapnplay.appengine.features.about.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.about.domain.AboutRepository
import com.kglabs28.yapnplay.appengine.features.about.domain.AppInfo

/**
 * Repository implementation: maps source data to domain, errors to typed
 * [YapNPlayError]. Never throws.
 */
class AboutRepositoryImpl(private val source: StaticAppInfoSource) : AboutRepository {
    override suspend fun appInfo(): AppResult<AppInfo> = try {
        AppResult.Success(source.load())
    } catch (e: Exception) {
        AppResult.Failure(YapNPlayError.Unknown(e.message ?: "app info failed"))
    }
}
