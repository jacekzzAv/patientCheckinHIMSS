package com.avalue.checkin.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Overlay for insurance card capture with guide frame.
 * Standard credit/insurance card ratio is 1.586:1 (3.375" x 2.125")
 */
@Composable
fun CardOverlay(
    modifier: Modifier = Modifier,
    cardWidthRatio: Float = 0.85f,  // Card width as ratio of screen width
    verticalOffsetRatio: Float = 0f // Center by default
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        // Calculate card dimensions (standard card aspect ratio)
        val cardAspectRatio = 1.586f
        val cardWidth = canvasWidth * cardWidthRatio
        val cardHeight = cardWidth / cardAspectRatio
        
        // Center the card frame
        val centerX = canvasWidth / 2
        val centerY = (canvasHeight / 2) + (canvasHeight * verticalOffsetRatio)
        
        val cardLeft = centerX - (cardWidth / 2)
        val cardTop = centerY - (cardHeight / 2)
        
        val cornerRadius = 16.dp.toPx()
        
        val cardRect = RoundRect(
            left = cardLeft,
            top = cardTop,
            right = cardLeft + cardWidth,
            bottom = cardTop + cardHeight,
            cornerRadius = CornerRadius(cornerRadius)
        )
        
        // Draw semi-transparent overlay with card cutout
        val overlayPath = Path().apply {
            addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
            addRoundRect(cardRect)
        }
        
        drawPath(
            path = overlayPath,
            color = Color.Black.copy(alpha = 0.65f),
            blendMode = BlendMode.SrcOver
        )
        
        // Draw card border
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cardLeft, cardTop),
            size = Size(cardWidth, cardHeight),
            cornerRadius = CornerRadius(cornerRadius),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Draw corner brackets for visual guidance
        val bracketLength = 40.dp.toPx()
        val bracketThickness = 4.dp.toPx()
        val bracketColor = Color.White
        val bracketInset = 8.dp.toPx()
        
        // Top-left corner
        drawLine(
            color = bracketColor,
            start = Offset(cardLeft + bracketInset, cardTop + bracketInset),
            end = Offset(cardLeft + bracketInset + bracketLength, cardTop + bracketInset),
            strokeWidth = bracketThickness
        )
        drawLine(
            color = bracketColor,
            start = Offset(cardLeft + bracketInset, cardTop + bracketInset),
            end = Offset(cardLeft + bracketInset, cardTop + bracketInset + bracketLength),
            strokeWidth = bracketThickness
        )
        
        // Top-right corner
        drawLine(
            color = bracketColor,
            start = Offset(cardLeft + cardWidth - bracketInset, cardTop + bracketInset),
            end = Offset(cardLeft + cardWidth - bracketInset - bracketLength, cardTop + bracketInset),
            strokeWidth = bracketThickness
        )
        drawLine(
            color = bracketColor,
            start = Offset(cardLeft + cardWidth - bracketInset, cardTop + bracketInset),
            end = Offset(cardLeft + cardWidth - bracketInset, cardTop + bracketInset + bracketLength),
            strokeWidth = bracketThickness
        )
        
        // Bottom-left corner
        drawLine(
            color = bracketColor,
            start = Offset(cardLeft + bracketInset, cardTop + cardHeight - bracketInset),
            end = Offset(cardLeft + bracketInset + bracketLength, cardTop + cardHeight - bracketInset),
            strokeWidth = bracketThickness
        )
        drawLine(
            color = bracketColor,
            start = Offset(cardLeft + bracketInset, cardTop + cardHeight - bracketInset),
            end = Offset(cardLeft + bracketInset, cardTop + cardHeight - bracketInset - bracketLength),
            strokeWidth = bracketThickness
        )
        
        // Bottom-right corner
        drawLine(
            color = bracketColor,
            start = Offset(cardLeft + cardWidth - bracketInset, cardTop + cardHeight - bracketInset),
            end = Offset(cardLeft + cardWidth - bracketInset - bracketLength, cardTop + cardHeight - bracketInset),
            strokeWidth = bracketThickness
        )
        drawLine(
            color = bracketColor,
            start = Offset(cardLeft + cardWidth - bracketInset, cardTop + cardHeight - bracketInset),
            end = Offset(cardLeft + cardWidth - bracketInset, cardTop + cardHeight - bracketInset - bracketLength),
            strokeWidth = bracketThickness
        )
    }
}
