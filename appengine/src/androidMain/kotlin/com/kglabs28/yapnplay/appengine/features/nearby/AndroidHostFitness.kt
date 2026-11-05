package com.kglabs28.yapnplay.appengine.features.nearby

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import com.kglabs28.yapnplay.appengine.features.nearby.domain.HostFitness

/** [HostFitness] from the phone's battery, charger, memory and cores. */
class AndroidHostFitness(private val context: Context) : HostFitness {
    override fun score(): Int = runCatching {
        val battery = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100)
        val charging = battery.isCharging
        val memory = ActivityManager.MemoryInfo().also { (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(it) }
        val ramGb = memory.totalMem / BYTES_PER_GB
        val cores = Runtime.getRuntime().availableProcessors()
        val score = level * BATTERY_SHARE / 100 +
            (if (charging) CHARGING_BONUS else 0) +
            (ramGb.coerceAtMost(MAX_RAM_GB) * RAM_SHARE / MAX_RAM_GB).toInt() +
            cores.coerceAtMost(MAX_CORES) * CORE_SHARE / MAX_CORES
        // Nearly flat and not charging: only if nobody else can.
        if (level < LOW_BATTERY && !charging) score.coerceAtMost(LOW_BATTERY_CAP) else score.coerceIn(0, 100)
    }.getOrDefault(DEFAULT_SCORE)

    private companion object {
        const val BYTES_PER_GB = 1_073_741_824.0
        const val BATTERY_SHARE = 60
        const val CHARGING_BONUS = 15
        const val RAM_SHARE = 15
        const val MAX_RAM_GB = 8.0
        const val CORE_SHARE = 10
        const val MAX_CORES = 8
        const val LOW_BATTERY = 15
        const val LOW_BATTERY_CAP = 5
        const val DEFAULT_SCORE = 30
    }
}
