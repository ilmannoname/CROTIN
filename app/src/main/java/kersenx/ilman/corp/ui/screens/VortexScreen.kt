package kersenx.ilman.corp.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
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
import kersenx.ilman.corp.shizuku.ShizukuManager
import kersenx.ilman.corp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VortexScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isRunning by remember { mutableStateOf(ShizukuManager.isShizukuRunning()) }
    var hasPermission by remember { mutableStateOf(ShizukuManager.hasPermission()) }
    var testOutput by remember { mutableStateOf("") }

    fun refreshStatus() {
        isRunning = ShizukuManager.isShizukuRunning()
        hasPermission = ShizukuManager.hasPermission()
    }

    LaunchedEffect(Unit) {
        refreshStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("VORTEX STATUS", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
        Text("Pusat Kontrol Shizuku & Info Pengembang", fontSize = 12.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(16.dp))

        // Card Status Shizuku
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("STATUS KONEKSI SHIZUKU", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Server Shizuku: ", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        if (isRunning) "AKTIF (BERJALAN)" else "TIDAK AKTIF",
                        color = if (isRunning) GamerGreen else RogRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Izin Akses Aplikasi: ", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        if (hasPermission) "DIIZINKAN" else "BELUM DIIZINKAN",
                        color = if (hasPermission) GamerGreen else RogRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!hasPermission && isRunning) {
                    Button(
                        onClick = {
                            ShizukuManager.requestPermission()
                            refreshStatus()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("MINTA IZIN SHIZUKU", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Info Pengembang (ilman - Ilman Corp)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("TENTANG PENGEMBANG", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Aplikasi: Kersen X", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("Developer: ilman", color = GamerGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Organization: Ilman Corp", color = TextSecondary, fontSize = 13.sp)
                Text("Package: kersenx.ilman.corp", color = TextSecondary, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(16.dp))
                Text("SOSIAL MEDIA PENGEMBANG:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/ilman.dll"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = RogRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("📷 @ilman.dll", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@ilman6x"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceCard),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("🎵 @ilman6x", fontSize = 12.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Info Perangkat
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("INFO PERANGKAT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Model: ${Build.MANUFACTURER} ${Build.MODEL}", color = TextPrimary, fontSize = 14.sp)
                Text("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})", color = TextSecondary, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    val res = ShizukuManager.execute("id && getprop ro.build.version.release")
                    testOutput = if (res.isSuccess) "Sukses!\n${res.stdout}" else "Error: ${res.stderr}"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("UJI EKSEKUSI SHELL BINDER", color = NeonCyan)
        }

        if (testOutput.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(testOutput, color = GamerGreen, fontSize = 12.sp)
        }
    }
}
