package red.line.tamirkar.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
fun SignatureScreen(
    onBack: () -> Unit,
    onSave: (android.net.Uri) -> Unit
) {
    val context = LocalContext.current
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("امضای مشتری") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("لطفاً از مشتری بخواهید در کادر زیر امضا کند تا تحویل دستگاه ثبت شود.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))

            var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val p = Path().apply { moveTo(offset.x, offset.y) }
                                currentPath = p
                                paths.add(p)
                            },
                            onDrag = { change, _ ->
                                currentPath?.lineTo(change.position.x, change.position.y)
                                // trigger recomposition by replacing last element
                                if (paths.isNotEmpty()) paths[paths.size - 1] = currentPath!!
                            }
                        )
                    }
            ) {
                canvasSize = size
                paths.forEach { path ->
                    drawPath(path = path, color = Color.Black, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f))
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { paths.clear() }, modifier = Modifier.weight(1f)) { Text("پاک کردن") }
                Button(
                    onClick = {
                        val width = if (canvasSize.width > 0) canvasSize.width.toInt() else 800
                        val height = if (canvasSize.height > 0) canvasSize.height.toInt() else 400
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        val androidCanvas = AndroidCanvas(bitmap)
                        androidCanvas.drawColor(AndroidColor.WHITE)
                        val paint = android.graphics.Paint().apply {
                            color = AndroidColor.BLACK
                            style = android.graphics.Paint.Style.STROKE
                            strokeWidth = 5f
                            isAntiAlias = true
                        }
                        paths.forEach { composePath ->
                            androidCanvas.drawPath(composePath.asAndroidPath(), paint)
                        }

                        val outDir = File(context.cacheDir, "signatures").apply { mkdirs() }
                        val file = File(outDir, "signature_${System.currentTimeMillis()}.png")
                        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        onSave(uri)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = paths.isNotEmpty()
                ) { Text("ثبت امضا") }
            }
        }
    }
}
