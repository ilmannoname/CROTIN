package kersenx.ilman.corp.tweaks

import kersenx.ilman.corp.shizuku.CommandResult
import kersenx.ilman.corp.shizuku.ShizukuManager

object PerformanceManager {

    var isModeXActive = false
        private set

    suspend fun enableModeX(): CommandResult {
        val commands = listOf(
            "settings put global debug.egl.force_msaa 1",
            "service call SurfaceFlinger 1008 i32 1",
            "setprop debug.hwui.renderer skiagl",
            "setprop debug.hwui.fps_divisor 1",
            "am kill-all"
        )
        val result = ShizukuManager.execute(commands.joinToString(" && "))
        if (result.isSuccess) {
            isModeXActive = true
        }
        return result
    }

    suspend fun disableModeX(): CommandResult {
        val commands = listOf(
            "settings put global debug.egl.force_msaa 0",
            "service call SurfaceFlinger 1008 i32 0"
        )
        val result = ShizukuManager.execute(commands.joinToString(" && "))
        isModeXActive = false
        return result
    }
}
