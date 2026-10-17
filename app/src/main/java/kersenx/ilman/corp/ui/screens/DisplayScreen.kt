package kersenx.ilman.corp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kersenx.ilman.corp.tweaks.DisplayInfo
import kersenx.ilman.corp.tweaks.PotatoGraphicsManager
import kersenx.ilman.corp.tweaks.ResolutionManager
import kersenx.ilman.corp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DisplayScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var displayInfo by remember { mutableStateOf<DisplayInfo?>(null) }
    var widthInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var densityInput by remember { mutableStateOf("") }

    fun refresh() {
        scope.launch {
            val info = ResolutionManager.getDisplayInfo()
            displayInfo = info
            widthInput = info.currentWidth.toString()
            heightInput = info.currentHeight.toString()
            densityInput = info.currentDensity.toString()
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("PENGUBAH RESOLUSI", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
        Text("Mesin Resolusi Shizuku (Standar Pixels) • Kersen X", fontSize = 12.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("STATUS LAYAR SAAT INI", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Spacer(modifier = Modifier.height(8.dp))
                if (displayInfo != null) {
                    val info = displayInfo!!
                    Text("Resolusi Fisik: ${info.physicalWidth} x ${info.physicalHeight}", color = TextPrimary, fontSize = 14.sp)
                    Text("Resolusi Aktif: ${info.currentWidth} x ${info.currentHeight}", color = if (info.isModified) RogRed else GamerGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("DPI Layar: ${info.currentDensity} (Fisik: ${info.physicalDensity})", color = TextSecondary, fontSize = 13.sp)
                } else {
                    Text("Memuat data display...", color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("PRESET CEPAT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    displayInfo?.let {
                        val w = (it.physicalWidth * 0.67f).toInt()
                        val h = (it.physicalHeight * 0.67f).toInt()
                        widthInput = w.toString()
                        heightInput = h.toString()
                        densityInput = ((it.physicalDensity * 0.67f).toInt()).toString()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceCard),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("720p HD", fontSize = 12.sp, color = TextPrimary)
            }

            Button(
                onClick = {
                    scope.launch {
                        PotatoGraphicsManager.enablePotatoGraphics()
                        refresh()
                        Toast.makeText(context, "Grafik MC Diaktifkan!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = GamerGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🎮 Grafik MC", fontSize = 12.sp, color = DarkBg, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("PENGATURAN KUSTOM", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = widthInput,
                onValueChange = { widthInput = it },
                label = { Text("Lebar (px)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = heightInput,
                onValueChange = { heightInput = it },
                label = { Text("Tinggi (px)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = densityInput,
            onValueChange = { densityInput = it },
            label = { Text("DPI Layar (Density)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val w = widthInput.toIntOrNull()
                val h = heightInput.toIntOrNull()
                val d = densityInput.toIntOrNull()

                if (w != null && h != null) {
                    scope.launch {
                        val res = ResolutionManager.applyResolution(w, h)
                        if (d != null) ResolutionManager.applyDensity(d)
                        refresh()
                        if (res.isSuccess) {
                            Toast.makeText(context, "Resolusi Berhasil Diterapkan!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Gagal: ${res.stderr}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Masukkan nilai resolusi yang valid", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("TERAPKAN RESOLUSI", color = DarkBg, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                scope.launch {
                    ResolutionManager.reset()
                    refresh()
                    Toast.makeText(context, "Resolusi Kembali Normal", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("RESET KE DEFAULT", color = RogRed)
        }
    }
}
