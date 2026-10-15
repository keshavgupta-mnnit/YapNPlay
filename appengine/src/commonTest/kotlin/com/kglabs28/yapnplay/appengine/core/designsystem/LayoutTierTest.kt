package com.kglabs28.yapnplay.appengine.core.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tier thresholds: S below 740dp, M from 740dp, L from 960dp.
 * Reference windows: 640x360 -> S, 800x360 and 915x412 -> M, 1280x800 -> L.
 */
class LayoutTierTest {

    @Test
    fun `small phone is tier S`() {
        assertEquals(LayoutTier.S, layoutTierFor(640))
    }

    @Test
    fun `landscape phones are tier M`() {
        assertEquals(LayoutTier.M, layoutTierFor(800))
        assertEquals(LayoutTier.M, layoutTierFor(915))
    }

    @Test
    fun `tablet is tier L`() {
        assertEquals(LayoutTier.L, layoutTierFor(1280))
    }

    @Test
    fun `threshold boundaries`() {
        assertEquals(LayoutTier.S, layoutTierFor(739))
        assertEquals(LayoutTier.M, layoutTierFor(740))
        assertEquals(LayoutTier.M, layoutTierFor(959))
        assertEquals(LayoutTier.L, layoutTierFor(960))
    }

    @Test
    fun `every tier resolves dimens`() {
        for (tier in LayoutTier.entries) {
            val dimens = dimensForTier(tier)
            assertEquals(dimens, dimensForTier(tier))
        }
    }
}
