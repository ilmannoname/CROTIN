package kersenx.ilman.corp.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kersenx.ilman.corp.service.AxCornerService
import kersenx.ilman.corp.service.CrosshairService
import kersenx.ilman.corp.service.SessionMonitorService
import kersenx.ilman.corp.tweaks.MemoryLoosener
import kersenx.ilman.corp.tweaks.TouchEnhancer
import kersenx.ilman.corp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CornerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isCornerOn by remember { mutableStateOf(false) }
    var isCrosshairOn by remember { mutableStateOf(false) }
    var isMonitorOn by remember { mutableStateOf(false) }
    var isTouchEnhancerOn by remember { mutableStateOf(false) }

    fun checkOverlayPermission(): Boolean {
        return if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
            Toast.makeText(context, "Berikan izin Tampilkan di Atas Aplikasi Lain", Toast.LENGTH_LONG).show()
            false
        } else true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("KERSEN CORNER & TOOLS", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
        Text("Modul Tweak & Overlay In-Game • Kersen X", fontSize = 12.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(16.dp))

        ToolCard(
            title = "In-Game Corner Panel",
            desc = "Tombol melayang di pojok layar untuk menu instan saat main game",
            checked = isCornerOn,
            onCheckedChange = { checked ->
                if (checked) {
                    if (checkOverlayPermission()) {
                        context.startService(Intent(context, AxCornerService::class.java))
                        isCornerOn = true
                    }
                } else {
                    context.stopService(Intent(context, AxCornerService::class.java))
                    isCornerOn = false
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToolCard(
            title = "Custom Crosshair",
            desc = "Titik bidik presisi di tengah layar untuk game battle royale / FPS",
            checked = isCrosshairOn,
            onCheckedChange = { checked ->
                if (checked) {
                    if (checkOverlayPermission()) {
                        context.startService(Intent(context, CrosshairService::class.java))
                        isCrosshairOn = true
                    }
                } else {
                    context.stopService(Intent(context, CrosshairService::class.java))
                    isCrosshairOn = false
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToolCard(
            title = "Real-Time Session Monitor",
            desc = "HUD kecil di sudut atas menampilkan FPS dan pemakaian RAM",
            checked = isMonitorOn,
            onCheckedChange = { checked ->
                if (checked) {
                    if (checkOverlayPermission()) {
                        context.startService(Intent(context, SessionMonitorService::class.java))
                        isMonitorOn = true
                    }
                } else {
                    context.stopService(Intent(context, SessionMonitorService::class.java))
                    isMonitorOn = false
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToolCard(
            title = "HW Touch Enhancer",
            desc = "Mengurangi latensi sentuhan layar via Hardware Composer",
            checked = isTouchEnhancerOn,
            onCheckedChange = { checked ->
                isTouchEnhancerOn = checked
                scope.launch {
                    TouchEnhancer.applyTouchTweaks(checked)
                    Toast.makeText(context, if (checked) "Touch Response Ditingkatkan" else "Touch Default", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    MemoryLoosener.quickBoost()
                    Toast.makeText(context, "Memory Loosener: RAM Dibersihkan!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = RogRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("⚡ QUICK BOOST (MEMORY LOOSENER)", color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ToolCard(title: String, desc: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(desc, fontSize = 12.sp, color = TextSecondary)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
