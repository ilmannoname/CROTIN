package kersenx.ilman.corp.tweaks

import kersenx.ilman.corp.shizuku.CommandResult
import kersenx.ilman.corp.shizuku.ShizukuManager

object MemoryLoosener {
    suspend fun quickBoost(): CommandResult {
        return ShizukuManager.execute("am kill-all && sync")
    }
}
