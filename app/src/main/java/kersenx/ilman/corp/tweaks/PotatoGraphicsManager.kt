package kersenx.ilman.corp.tweaks

import kersenx.ilman.corp.shizuku.CommandResult

object PotatoGraphicsManager {

    var isPotatoActive = false
        private set

    suspend fun enablePotatoGraphics(): CommandResult {
        val displayInfo = ResolutionManager.getDisplayInfo()
        val isLandscape = displayInfo.physicalWidth > displayInfo.physicalHeight

        val shortEdge = if (isLandscape) displayInfo.physicalHeight else displayInfo.physicalWidth
        val longEdge = if (isLandscape) displayInfo.physicalWidth else displayInfo.physicalHeight

        val targetShort = (shortEdge * 0.5f).toInt()
        val targetLong = (longEdge * 0.5f).toInt()

        val targetW = if (isLandscape) targetLong else targetShort
        val targetH = if (isLandscape) targetShort else targetLong
        val targetDpi = (displayInfo.physicalDensity * 0.5f).toInt().coerceIn(160, 240)

        val r1 = ResolutionManager.applyResolution(targetW, targetH)
        if (!r1.isSuccess) return r1

        val r2 = ResolutionManager.applyDensity(targetDpi)
        if (r2.isSuccess) {
            isPotatoActive = true
        }
        return r2
    }

    suspend fun disablePotatoGraphics(): CommandResult {
        val res = ResolutionManager.reset()
        if (res.isSuccess) {
            isPotatoActive = false
        }
        return res
    }
}
