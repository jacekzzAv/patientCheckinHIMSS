package com.avalue.checkin.components

import android.graphics.Bitmap
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PathData(
    val path: Path,
    val color: Color = Color.Black,
    val strokeWidth: Float = 5f
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignatureCanvas(
    modifier: Modifier = Modifier,
    strokeColor: Color = Color.Black,
    strokeWidth: Dp = 4.dp,
    backgroundColor: Color = Color.White,
    signatureState: SignatureState? = null,
    onSignatureChanged: (hasSignature: Boolean) -> Unit = {}
) {
    val state = signatureState ?: rememberSignatureState()
    var currentPath by remember { mutableStateOf<Path?>(null) }
    
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidth.toPx() }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            currentPath = Path().apply {
                                moveTo(event.x, event.y)
                            }
                            if (!state.hasSignature) {
                                state.markHasSignature()
                                onSignatureChanged(true)
                            }
                            true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            currentPath?.lineTo(event.x, event.y)
                            // Force recomposition
                            currentPath = currentPath?.let { Path().apply { addPath(it) } }
                            true
                        }
                        MotionEvent.ACTION_UP -> {
                            currentPath?.let { path ->
                                state.addPath(PathData(path, strokeColor, strokeWidthPx))
                            }
                            currentPath = null
                            true
                        }
                        else -> false
                    }
                }
        ) {
            // Draw all completed paths
            state.paths.forEach { pathData ->
                drawPath(
                    path = pathData.path,
                    color = pathData.color,
                    style = Stroke(
                        width = pathData.strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            
            // Draw current path being drawn
            currentPath?.let { path ->
                drawPath(
                    path = path,
                    color = strokeColor,
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

@Composable
fun rememberSignatureState(): SignatureState {
    return remember { SignatureState() }
}

class SignatureState {
    val paths = mutableStateListOf<PathData>()
    var hasSignature by mutableStateOf(false)
        private set
    
    fun clear() {
        paths.clear()
        hasSignature = false
    }
    
    fun addPath(path: PathData) {
        paths.add(path)
        hasSignature = true
    }

    fun markHasSignature() {
        hasSignature = true
    }
    
    fun getPaths(): List<PathData> = paths.toList()
    
    fun toBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 8f
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
            isAntiAlias = true
        }
        
        paths.forEach { pathData ->
            paint.color = pathData.color.toArgb()
            paint.strokeWidth = pathData.strokeWidth
            canvas.drawPath(pathData.path.asAndroidPath(), paint)
        }
        
        return bitmap
    }
}
