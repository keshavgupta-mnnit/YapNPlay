package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.data.NearbyIdentity

/** This phone's account id with no server to ask (the saved account, or the phone's own id). */
class GetMyPlayerIdUseCase(private val identity: NearbyIdentity) {
    operator fun invoke(): String = identity.me().userId
}
