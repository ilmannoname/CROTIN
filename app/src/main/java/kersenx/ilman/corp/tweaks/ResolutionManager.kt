package kersenx.ilman.corp.tweaks

import kersenx.ilman.corp.shizuku.CommandResult
import kersenx.ilman.corp.shizuku.ShizukuManager

data class DisplayInfo(
    val physicalWidth: Int,
    val physicalHeight: Int,
    val overrideWidth: Int?,
    val overrideHeight: Int?,
    val physicalDensity: Int,
    val overrideDensity: Int?
) {
    val currentWidth: Int get() = overrideWidth ?: physicalWidth
    val currentHeight: Int get() = overrideHeight ?: physicalHeight
    val currentDensity: Int get() = overrideDensity ?: physicalDensity
    val isModified: Boolean get() = overrideWidth != null || overrideDensity != null
}

object ResolutionManager {

    suspend fun getDisplayInfo(): DisplayInfo {
        val sizeRes = ShizukuManager.execute("wm size")
        val densityRes = ShizukuManager.execute("wm density")

        var physW = 1080
        var physH = 2400
        var overW: Int? = null
        var overH: Int? = null

        val sizeLines = sizeRes.stdout.lines()
        for (line in sizeLines) {
            if (line.contains("Physical size:")) {
                val parts = line.substringAfter("Physical size:").trim().split("x")
                if (parts.size == 2) {
                    physW = parts[0].toIntOrNull() ?: 1080
                    physH = parts[1].toIntOrNull() ?: 2400
                }
            } else if (line.contains("Override size:")) {
                val parts = line.substringAfter("Override size:").trim().split("x")
                if (parts.size == 2) {
                    overW = parts[0].toIntOrNull()
                    overH = parts[1].toIntOrNull()
                }
            }
        }

        var physD = 440
        var overD: Int? = null
        val densityLines = densityRes.stdout.lines()
        for (line in densityLines) {
            if (line.contains("Physical density:")) {
                physD = line.substringAfter("Physical density:").trim().toIntOrNull() ?: 440
            } else if (line.contains("Override density:")) {
                overD = line.substringAfter("Override density:").trim().toIntOrNull()
            }
        }

        return DisplayInfo(physW, physH, overW, overH, physD, overD)
    }

    suspend fun applyResolution(width: Int, height: Int): CommandResult {
        return ShizukuManager.execute("wm size ${width}x${height}")
    }

    suspend fun applyDensity(density: Int): CommandResult {
        return ShizukuManager.execute("wm density $density")
    }

    suspend fun reset(): CommandResult {
        return ShizukuManager.execute("wm size reset && wm density reset")
    }
}
