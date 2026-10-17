package kersenx.ilman.corp.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val isGame: Boolean
)

class GameRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    val dao = db.gameProfileDao()

    suspend fun getInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        val list = mutableListOf<InstalledApp>()

        for (pi in packages) {
            val appInfo = pi.applicationInfo ?: continue
            val launchIntent = pm.getLaunchIntentForPackage(pi.packageName) ?: continue
            val isGame = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                appInfo.category == ApplicationInfo.CATEGORY_GAME
            } else {
                (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }
            val name = appInfo.loadLabel(pm).toString()
            list.add(InstalledApp(pi.packageName, name, isGame))
        }
        list.sortedByDescending { it.isGame }
    }
}
