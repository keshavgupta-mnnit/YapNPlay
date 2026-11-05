package com.kglabs28.yapnplay.appengine.features.nearby.presentation

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRadio

/** One thing Nearby play needs: the permission, a switch on the phone, or the radios actually starting. */
sealed interface NearbyCheck {
    data object Permission : NearbyCheck
    data class Radio(val radio: NearbyRadio) : NearbyCheck

    /** Everything was on but Nearby still wouldn't start: tapping tries again. */
    data object Start : NearbyCheck
}

/** One checklist row, ready to draw: tick when [ok], else a tap fixes it; [detail] says why when known. */
data class NearbyReadyItemUi(val check: NearbyCheck, val label: String, val ok: Boolean, val detail: String? = null)

/** The "Get ready for Nearby" checklist; Enter Lobby only once [allReady]. */
data class NearbyReadyUi(val items: List<NearbyReadyItemUi>) {
    val allReady: Boolean get() = items.all { it.ok }
    val readyCount: Int get() = items.count { it.ok }
}
