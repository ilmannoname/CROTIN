package kersenx.ilman.corp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kersenx.ilman.corp.shizuku.ShizukuManager
import kersenx.ilman.corp.ui.screens.CornerScreen
import kersenx.ilman.corp.ui.screens.DisplayScreen
import kersenx.ilman.corp.ui.screens.SpaceScreen
import kersenx.ilman.corp.ui.screens.VortexScreen
import kersenx.ilman.corp.ui.theme.*
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity(), Shizuku.OnRequestPermissionResultListener {

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        if (!ShizukuManager.hasPermission()) {
            ShizukuManager.requestPermission()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addRequestPermissionResultListener(this)

        setContent {
            KersenXTheme {
                MainAppScaffold()
            }
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        // Shizuku permission callback
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeRequestPermissionResultListener(this)
    }
}

@Composable
fun MainAppScaffold() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = NeonCyan
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Space") },
                    label = { Text("Space") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = DarkSurfaceCard,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AspectRatio, contentDescription = "Display") },
                    label = { Text("Display") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = DarkSurfaceCard,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Bolt, contentDescription = "Corner") },
                    label = { Text("Corner") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = DarkSurfaceCard,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Vortex") },
                    label = { Text("Vortex") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = DarkSurfaceCard,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = DarkBg
        ) {
            when (selectedTab) {
                0 -> SpaceScreen()
                1 -> DisplayScreen()
                2 -> CornerScreen()
                3 -> VortexScreen()
            }
        }
    }
}
