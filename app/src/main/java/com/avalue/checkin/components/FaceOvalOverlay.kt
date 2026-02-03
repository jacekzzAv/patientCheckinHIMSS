package com.avalue.checkin.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun FaceOvalOverlay(
    modifier: Modifier = Modifier,
    ovalWidthRatio: Float = 0.55f,  // Oval width as ratio of screen width
    ovalHeightRatio: Float = 0.45f, // Oval height as ratio of screen height
    verticalOffsetRatio: Float = -0.05f // Slight offset upward
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        // Calculate oval dimensions
        val ovalWidth = canvasWidth * ovalWidthRatio
        val ovalHeight = canvasHeight * ovalHeightRatio
        
        // Center the oval with optional vertical offset
        val centerX = canvasWidth / 2
        val centerY = (canvasHeight / 2) + (canvasHeight * verticalOffsetRatio)
        
        val ovalLeft = centerX - (ovalWidth / 2)
        val ovalTop = centerY - (ovalHeight / 2)
        
        val ovalRect = Rect(
            left = ovalLeft,
            top = ovalTop,
            right = ovalLeft + ovalWidth,
            bottom = ovalTop + ovalHeight
        )
        
        // Draw semi-transparent overlay
        val overlayPath = Path().apply {
            // Full screen rectangle
            addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
            // Subtract the oval (creates the cutout)
            addOval(ovalRect)
        }
        
        // Draw the dark overlay with cutout
        drawPath(
            path = overlayPath,
            color = Color.Black.copy(alpha = 0.6f),
            blendMode = BlendMode.SrcOver
        )
        
        // Draw oval border
        drawOval(
            color = Color.White,
            topLeft = Offset(ovalLeft, ovalTop),
            size = Size(ovalWidth, ovalHeight),
            style = Stroke(width = 4.dp.toPx())
        )
        
        // Draw corner guides for better visual feedback
        val guideLength = 30.dp.toPx()
        val guideOffset = 10.dp.toPx()
        
        // We'll draw small guide marks at top, bottom, left, right of oval
        val guideColor = Color.White.copy(alpha = 0.8f)
        val guideStroke = Stroke(width = 3.dp.toPx())
        
        // Top guide
        drawLine(
            color = guideColor,
            start = Offset(centerX - guideLength/2, ovalTop - guideOffset),
            end = Offset(centerX + guideLength/2, ovalTop - guideOffset),
            strokeWidth = guideStroke.width
        )
        
        // Bottom guide
        drawLine(
            color = guideColor,
            start = Offset(centerX - guideLength/2, ovalTop + ovalHeight + guideOffset),
            end = Offset(centerX + guideLength/2, ovalTop + ovalHeight + guideOffset),
            strokeWidth = guideStroke.width
        )
    }
}
