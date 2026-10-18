package com.kglabs28.yapnplay.appengine.features.profile.data.remote

import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.protocol.BlockUserBody
import com.kglabs28.yapnplay.appengine.protocol.FriendRequests
import com.kglabs28.yapnplay.appengine.protocol.Friends
import com.kglabs28.yapnplay.appengine.protocol.Me
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestBody
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestReply
import com.kglabs28.yapnplay.appengine.protocol.UpdateProfileBody
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay

/**
 * Profile API client. TODAY: served by [FakeProfileServer] with a
 * network-like delay so the UI behaves exactly as it will in production.
 *
 * LATER (backend ready): uncomment each real call, delete the fake lines.
 * Nothing else changes — the interface stays, so remote manager,
 * repository, view model and screen are untouched. The bearer header
 * (userId.secret) is added by the shared HttpClient.
 */
class ProfileApiClient(
    private val client: HttpClient,
    private val fake: FakeProfileServer,
) : ProfileApi {

    override suspend fun getMe(): Me {
        // return client.get(AppConstants.SERVER_BASE_URL + "/me").body()
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return fake.me()
    }

    override suspend fun updateMe(body: UpdateProfileBody): Me {
        // return client.patch(AppConstants.SERVER_BASE_URL + "/me") { contentType(ContentType.Application.Json); setBody(body) }.body()
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return fake.updateMe(body)
    }

    override suspend fun getFriends(): Friends {
        // return client.get(AppConstants.SERVER_BASE_URL + "/friends").body()
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return fake.friends()
    }

    override suspend fun getFriendRequests(): FriendRequests {
        // return client.get(AppConstants.SERVER_BASE_URL + "/friends/requests").body()
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return fake.requests()
    }

    override suspend fun sendFriendRequest(body: SendFriendRequestBody): SendFriendRequestReply {
        // return client.post(AppConstants.SERVER_BASE_URL + "/friends/requests") { contentType(ContentType.Application.Json); setBody(body) }.body()
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return fake.sendRequest(body)
    }

    override suspend fun acceptFriendRequest(requestId: String) {
        // client.post(AppConstants.SERVER_BASE_URL + "/friends/requests/$requestId/accept")
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        fake.accept(requestId)
    }

    override suspend fun declineFriendRequest(requestId: String) {
        // client.post(AppConstants.SERVER_BASE_URL + "/friends/requests/$requestId/decline")
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        fake.decline(requestId)
    }

    override suspend fun removeFriend(userId: String) {
        // client.delete(AppConstants.SERVER_BASE_URL + "/friends/$userId")
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        fake.removeFriend(userId)
    }

    override suspend fun blockUser(body: BlockUserBody) {
        // client.post(AppConstants.SERVER_BASE_URL + "/blocks") { contentType(ContentType.Application.Json); setBody(body) }
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        fake.block(body)
    }
}
