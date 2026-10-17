package kersenx.ilman.corp.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kersenx.ilman.corp.data.GameProfile
import kersenx.ilman.corp.data.GameRepository
import kersenx.ilman.corp.data.InstalledApp
import kersenx.ilman.corp.service.AxCornerService
import kersenx.ilman.corp.service.CrosshairService
import kersenx.ilman.corp.tweaks.PerformanceManager
import kersenx.ilman.corp.tweaks.PotatoGraphicsManager
import kersenx.ilman.corp.tweaks.ResolutionManager
import kersenx.ilman.corp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SpaceScreen() {
    val context = LocalContext.current
    val repository = remember { GameRepository(context) }
    val scope = rememberCoroutineScope()

    var apps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    var selectedApp by remember { mutableStateOf<InstalledApp?>(null) }
    var currentProfile by remember { mutableStateOf<GameProfile?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        apps = repository.getInstalledApps()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "KERSEN X SPACE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "Game Launcher & Per-App Config • by ilman",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (apps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(apps) { app ->
                    GameCard(app = app, onConfig = {
                        selectedApp = app
                        scope.launch {
                            currentProfile = repository.dao.getProfile(app.packageName)
                                ?: GameProfile(app.packageName, app.appName)
                            showDialog = true
                        }
                    }, onLaunch = {
                        scope.launch {
                            launchGameWithProfile(context, repository, app)
                        }
                    })
                }
            }
        }
    }

    if (showDialog && selectedApp != null && currentProfile != null) {
        GameConfigDialog(
            app = selectedApp!!,
            profile = currentProfile!!,
            onDismiss = { showDialog = false },
            onSave = { updated ->
                scope.launch {
                    repository.dao.insertOrUpdate(updated)
                    showDialog = false
                    Toast.makeText(context, "Profil ${app.appName} Disimpan!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun GameCard(app: InstalledApp, onConfig: () -> Unit, onLaunch: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = app.appName,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = if (app.isGame) "🎮 Game Terdeteksi" else "📱 Aplikasi",
                fontSize = 11.sp,
                color = if (app.isGame) GamerGreen else TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onConfig, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = "Config", tint = NeonCyan)
                }
                Button(
                    onClick = onLaunch,
                    colors = ButtonDefaults.buttonColors(containerColor = RogRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Launch", tint = Color.WHITE, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PLAY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.WHITE)
                }
            }
        }
    }
}

@Composable
fun GameConfigDialog(
    app: InstalledApp,
    profile: GameProfile,
    onDismiss: () -> Unit,
    onSave: (GameProfile) -> Unit
) {
    var widthText by remember { mutableStateOf(profile.customWidth?.toString() ?: "") }
    var heightText by remember { mutableStateOf(profile.customHeight?.toString() ?: "") }
    var densityText by remember { mutableStateOf(profile.customDensity?.toString() ?: "") }
    var modeX by remember { mutableStateOf(profile.isModeXEnabled) }
    var potatoMc by remember { mutableStateOf(profile.isPotatoMcEnabled) }
    var cornerPanel by remember { mutableStateOf(profile.isCornerEnabled) }
    var crosshair by remember { mutableStateOf(profile.isCrosshairEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Config Khusus: ${app.appName}", color = NeonCyan, fontSize = 16.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Resolusi Kustom (LxT):", color = TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = widthText,
                        onValueChange = { widthText = it },
                        label = { Text("Lebar") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = heightText,
                        onValueChange = { heightText = it },
                        label = { Text("Tinggi") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = densityText,
                    onValueChange = { densityText = it },
                    label = { Text("DPI Layar (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥 Mode X (ROG 4x MSAA)", color = TextPrimary, fontSize = 13.sp)
                    Switch(checked = modeX, onCheckedChange = { modeX = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎮 Grafik MC (Potato Mode)", color = TextPrimary, fontSize = 13.sp)
                    Switch(checked = potatoMc, onCheckedChange = { potatoMc = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚡ In-Game Corner Panel", color = TextPrimary, fontSize = 13.sp)
                    Switch(checked = cornerPanel, onCheckedChange = { cornerPanel = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎯 Custom Crosshair", color = TextPrimary, fontSize = 13.sp)
                    Switch(checked = crosshair, onCheckedChange = { crosshair = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val updated = profile.copy(
                    customWidth = widthText.toIntOrNull(),
                    customHeight = heightText.toIntOrNull(),
                    customDensity = densityText.toIntOrNull(),
                    isModeXEnabled = modeX,
                    isPotatoMcEnabled = potatoMc,
                    isCornerEnabled = cornerPanel,
                    isCrosshairEnabled = crosshair
                )
                onSave(updated)
            }, colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)) {
                Text("SIMPAN", color = DarkBg, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("BATAL", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )
}

suspend fun launchGameWithProfile(context: Context, repo: GameRepository, app: InstalledApp) {
    val profile = repo.dao.getProfile(app.packageName)

    if (profile?.isModeXEnabled == true) {
        PerformanceManager.enableModeX()
    }

    if (profile?.isPotatoMcEnabled == true) {
        PotatoGraphicsManager.enablePotatoGraphics()
    } else if (profile?.customWidth != null && profile?.customHeight != null) {
        ResolutionManager.applyResolution(profile.customWidth, profile.customHeight)
        profile.customDensity?.let { ResolutionManager.applyDensity(it) }
    }

    if (profile?.isCornerEnabled != false && android.provider.Settings.canDrawOverlays(context)) {
        val intent = Intent(context, AxCornerService::class.java)
        context.startService(intent)
    }

    if (profile?.isCrosshairEnabled == true && android.provider.Settings.canDrawOverlays(context)) {
        val chIntent = Intent(context, CrosshairService::class.java)
        context.startService(chIntent)
    }

    val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
    if (launchIntent != null) {
        context.startActivity(launchIntent)
    } else {
        Toast.makeText(context, "Gagal meluncurkan game", Toast.LENGTH_SHORT).show()
    }
}
