package com.kglabs28.yapnplay.appengine.features.profile.data.remote

import com.kglabs28.yapnplay.appengine.protocol.Friends
import com.kglabs28.yapnplay.appengine.protocol.Me

/**
 * Profile endpoints. Bodies are the shared contract types in `protocol/`
 * (see docs/backend-api-contract.md).
 */
interface ProfileApi {
    /** `GET /me` */
    suspend fun getMe(): Me

    /** `GET /friends` — provisional, post-POC friends feature. */
    suspend fun getFriends(): Friends
}
