package com.kglabs28.yapnplay.appengine.features.profile.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.FriendDto
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.ProfileDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay

/**
 * Profile API client. TODAY: serves from [FakeDataProvider] with a
 * network-like delay so the UI behaves exactly as it will in production.
 *
 * LATER (backend ready): uncomment the real call below, delete the fake
 * `delay + return` line. Nothing else changes — the interface signature
 * stays, so remote manager, repository, view model and screen are untouched.
 */
class ProfileApiClient(
    private val client: HttpClient,
) : ProfileApi {

    override suspend fun getProfile(): ProfileDto {
        // --- production path (uncomment when backend is live) ---
        // return client.get(AppConstants.SERVER_BASE_URL + "/v1/profile").body()

        // --- fake path (delete when switching to production) ---
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return FakeDataProvider.Profile.dto
    }

    override suspend fun getFriends(): List<FriendDto> {
        // --- production path (uncomment when backend is live) ---
        // return client.get(AppConstants.SERVER_BASE_URL + "/v1/friends").body()

        // --- fake path (delete when switching to production) ---
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return FakeDataProvider.Profile.friendDtos
    }
}
