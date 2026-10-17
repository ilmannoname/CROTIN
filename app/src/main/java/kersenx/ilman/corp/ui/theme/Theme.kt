package kersenx.ilman.corp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = RogRed,
    tertiary = GamerGreen,
    background = DarkBg,
    surface = DarkSurface,
    onPrimary = DarkBg,
    onSecondary = TextPrimary,
    onTertiary = DarkBg,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun KersenXTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
