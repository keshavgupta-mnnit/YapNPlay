package com.kglabs28.yapnplay.appengine.features.profile.data.remote

import com.kglabs28.yapnplay.appengine.protocol.BlockUserBody
import com.kglabs28.yapnplay.appengine.protocol.BlockedUsers
import com.kglabs28.yapnplay.appengine.protocol.FriendRequests
import com.kglabs28.yapnplay.appengine.protocol.Friends
import com.kglabs28.yapnplay.appengine.protocol.Me
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestBody
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestReply
import com.kglabs28.yapnplay.appengine.protocol.UpdateProfileBody

/**
 * Profile and friends endpoints. Bodies are the shared contract types in
 * `protocol/` (see docs/backend-api-contract.md).
 */
interface ProfileApi {
    /** `GET /me` */
    suspend fun getMe(): Me

    /** `PATCH /me` */
    suspend fun updateMe(body: UpdateProfileBody): Me

    /** `GET /friends` */
    suspend fun getFriends(): Friends

    /** `GET /friends/requests` */
    suspend fun getFriendRequests(): FriendRequests

    /** `POST /friends/requests` */
    suspend fun sendFriendRequest(body: SendFriendRequestBody): SendFriendRequestReply

    /** `POST /friends/requests/{requestId}/accept` */
    suspend fun acceptFriendRequest(requestId: String)

    /** `POST /friends/requests/{requestId}/decline` */
    suspend fun declineFriendRequest(requestId: String)

    /** `DELETE /friends/{userId}` */
    suspend fun removeFriend(userId: String)

    /** `POST /blocks` */
    suspend fun blockUser(body: BlockUserBody)

    /** `GET /blocks` */
    suspend fun getBlockedUsers(): BlockedUsers

    /** `DELETE /blocks/{userId}` */
    suspend fun unblockUser(userId: String)
}
