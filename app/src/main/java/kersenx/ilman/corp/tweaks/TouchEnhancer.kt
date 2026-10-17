package kersenx.ilman.corp.tweaks

import kersenx.ilman.corp.shizuku.CommandResult
import kersenx.ilman.corp.shizuku.ShizukuManager

object TouchEnhancer {
    suspend fun applyTouchTweaks(enabled: Boolean): CommandResult {
        return if (enabled) {
            ShizukuManager.execute("setprop debug.hwui.fps_divisor 1 && settings put system pointer_speed 4 2>/dev/null || true")
        } else {
            ShizukuManager.execute("settings put system pointer_speed 0 2>/dev/null || true")
        }
    }
}
