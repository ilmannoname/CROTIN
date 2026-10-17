package kersenx.ilman.corp.shizuku

import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuManager {

    const val SHIZUKU_REQ_CODE = 1001

    fun isShizukuInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("moe.shizuku.privileged.api", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isShizukuRunning(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Throwable) {
            false
        }
    }

    fun hasPermission(): Boolean {
        if (!isShizukuRunning()) return false
        return try {
            if (Shizuku.isPreV11()) {
                false
            } else {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Throwable) {
            false
        }
    }

    fun requestPermission(requestCode: Int = SHIZUKU_REQ_CODE) {
        if (isShizukuRunning() && !hasPermission()) {
            try {
                Shizuku.requestPermission(requestCode)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    suspend fun execute(command: String): CommandResult = withContext(Dispatchers.IO) {
        if (!isShizukuRunning() || !hasPermission()) {
            return@withContext CommandResult(-1, "", "Shizuku tidak aktif atau izin belum diberikan")
        }

        try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            val stdout = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            val stderr = BufferedReader(InputStreamReader(process.errorStream)).use { it.readText() }
            val exitCode = process.waitFor()
            CommandResult(exitCode, stdout.trim(), stderr.trim())
        } catch (e: Throwable) {
            CommandResult(-1, "", e.message ?: "Unknown execution error")
        }
    }
}
