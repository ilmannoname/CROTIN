package kersenx.ilman.corp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_profiles")
data class GameProfile(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val customWidth: Int? = null,
    val customHeight: Int? = null,
    val customDensity: Int? = null,
    val isModeXEnabled: Boolean = false,
    val isPotatoMcEnabled: Boolean = false,
    val isTouchBoostEnabled: Boolean = false,
    val isCrosshairEnabled: Boolean = false,
    val isCornerEnabled: Boolean = true
)
