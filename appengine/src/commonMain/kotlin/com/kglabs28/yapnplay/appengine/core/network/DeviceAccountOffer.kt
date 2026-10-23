package com.kglabs28.yapnplay.appengine.core.network

/** An account already made on this phone, offered back before a new one is made. */
data class DeviceAccountOffer(val name: String, val avatarId: String, val level: Int)
