package com.kglabs28.yapnplay.appengine.features.profile.data.remote

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.protocol.BlockUserBody
import com.kglabs28.yapnplay.appengine.protocol.BlockedUsers
import com.kglabs28.yapnplay.appengine.protocol.FriendRequests
import com.kglabs28.yapnplay.appengine.protocol.Friends
import com.kglabs28.yapnplay.appengine.protocol.Me
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestBody
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestReply
import com.kglabs28.yapnplay.appengine.protocol.UpdateProfileBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Profile and friends over HTTP. The shared [HttpClient] adds the
 * backend address and the bearer header (userId.secret).
 */
class ProfileApiClient(
    private val client: HttpClient,
) : ProfileApi {

    override suspend fun getMe(): Me = client.get(BackendPaths.ME).body()

    override suspend fun updateMe(body: UpdateProfileBody): Me = client.patch(BackendPaths.ME) {
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun getFriends(): Friends = client.get(BackendPaths.FRIENDS).body()

    override suspend fun getFriendRequests(): FriendRequests = client.get(BackendPaths.FRIEND_REQUESTS).body()

    override suspend fun sendFriendRequest(body: SendFriendRequestBody): SendFriendRequestReply =
        client.post(BackendPaths.FRIEND_REQUESTS) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    override suspend fun acceptFriendRequest(requestId: String) {
        client.post(BackendPaths.acceptFriendRequest(requestId))
    }

    override suspend fun declineFriendRequest(requestId: String) {
        client.post(BackendPaths.declineFriendRequest(requestId))
    }

    override suspend fun removeFriend(userId: String) {
        client.delete(BackendPaths.friend(userId))
    }

    override suspend fun blockUser(body: BlockUserBody) {
        client.post(BackendPaths.BLOCKS) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    override suspend fun getBlockedUsers(): BlockedUsers = client.get(BackendPaths.BLOCKS).body()

    override suspend fun unblockUser(userId: String) {
        client.delete(BackendPaths.block(userId))
    }
}
