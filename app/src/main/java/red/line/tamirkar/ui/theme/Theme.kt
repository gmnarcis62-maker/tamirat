package red.line.tamirkar.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Red40,
    onPrimary = Color.White,
    primaryContainer = Red95,
    onPrimaryContainer = Red10,

    secondary = Slate30,
    onSecondary = Color.White,
    secondaryContainer = Slate95,
    onSecondaryContainer = Slate10,

    tertiary = VipGoldDark,
    onTertiary = Color.White,
    tertiaryContainer = VipGoldContainer,
    onTertiaryContainer = Color(0xFF3D2E00),

    background = Neutral99,
    onBackground = Neutral10,
    surface = Color.White,
    onSurface = Neutral10,
    surfaceVariant = Neutral95,
    onSurfaceVariant = Slate30,

    error = StatusCancelled,
    onError = Color.White,

    outline = Neutral90,
    outlineVariant = Neutral95
)

private val DarkColors = darkColorScheme(
    primary = Red80,
    onPrimary = Red10,
    primaryContainer = Red30,
    onPrimaryContainer = Red90,

    secondary = Slate80,
    onSecondary = Slate10,
    secondaryContainer = Slate30,
    onSecondaryContainer = Slate90,

    tertiary = VipGold,
    onTertiary = Color(0xFF3D2E00),
    tertiaryContainer = Color(0xFF544400),
    onTertiaryContainer = VipGoldContainer,

    background = Neutral6,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = Neutral20,
    onSurfaceVariant = Slate90,

    error = Color(0xFFFFB4A9),
    onError = Color(0xFF680003),

    outline = Neutral20,
    outlineVariant = Neutral10
)

@Composable
fun TamirkarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // با توجه به هویت رنگی برند، رنگ ثابت پیشنهاد می‌شود
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TamirkarTypography,
        shapes = TamirkarShapes,
        content = content
    )
}
