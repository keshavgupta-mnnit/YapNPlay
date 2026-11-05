package com.kglabs28.yapnplay.appengine.features.nearby.presentation

import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRadio
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.GetNearbyRadiosUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.OpenNearbyRadioSettingsUseCase

/**
 * Builds the "Get ready for Nearby" checklist (the permission, then each
 * switch this phone needs on, then — when it failed — Nearby starting) and
 * fixes a switch by opening its settings. The permission is the screen's
 * to ask for. Shared by the Quick Play hub (before hosting) and the invite
 * popup (before joining nearby).
 */
class NearbyReadyChecklist(
    private val radios: GetNearbyRadiosUseCase,
    private val openRadioSettings: OpenNearbyRadioSettingsUseCase,
) {
    /**
     * The checklist now; [permitted]: the Nearby permission is granted.
     * [startFailure]: everything was on but Nearby wouldn't start (its reason, or "" when it gave none).
     */
    fun build(permitted: Boolean, startFailure: String? = null): NearbyReadyUi = NearbyReadyUi(
        listOf(NearbyReadyItemUi(NearbyCheck.Permission, NearbyStrings.checkPermission, permitted)) +
            radios().map { (radio, on) -> NearbyReadyItemUi(NearbyCheck.Radio(radio), radio.label(), on) } +
            listOfNotNull(
                startFailure?.let { NearbyReadyItemUi(NearbyCheck.Start, NearbyStrings.checkStart, ok = false, detail = it.ifBlank { NearbyStrings.checkStartRetry }) },
            ),
    )

    fun openSettings(radio: NearbyRadio) = openRadioSettings(radio)

    private fun NearbyRadio.label(): String = when (this) {
        NearbyRadio.BLUETOOTH -> NearbyStrings.checkBluetooth
        NearbyRadio.WIFI -> NearbyStrings.checkWifi
        NearbyRadio.LOCATION -> NearbyStrings.checkLocation
    }
}
