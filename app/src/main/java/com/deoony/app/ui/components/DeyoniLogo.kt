package com.deoony.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DeyoniLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(38.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8E24AA), // Elegant radiant Purple 600
                        Color(0xFF673AB7)  // Deep Royal Violet
                    )
                ),
                shape = CircleShape
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                
                // Draw white tiny checklist sheet
                val path = Path().apply {
                    moveTo(w * 0.25f, h * 0.15f)
                    lineTo(w * 0.75f, h * 0.15f)
                    lineTo(w * 0.75f, h * 0.85f)
                    // zig-zag bottom
                    lineTo(w * 0.65f, h * 0.78f)
                    lineTo(w * 0.55f, h * 0.85f)
                    lineTo(w * 0.45f, h * 0.78f)
                    lineTo(w * 0.35f, h * 0.85f)
                    lineTo(w * 0.25f, h * 0.78f)
                    close()
                }
                drawPath(path, color = Color.White)
                
                // Draw modern tiny list lines
                val lineThickness = h * 0.05f
                val lineLeft = w * 0.35f
                val lineRight = w * 0.65f
                
                // First horizontal line
                drawLine(
                    color = Color(0xFF673AB7),
                    start = Offset(lineLeft, h * 0.32f),
                    end = Offset(lineRight, h * 0.32f),
                    strokeWidth = lineThickness,
                    cap = StrokeCap.Round
                )
                // Second horizontal line
                drawLine(
                    color = Color(0xFF673AB7),
                    start = Offset(lineLeft, h * 0.47f),
                    end = Offset(lineRight, h * 0.47f),
                    strokeWidth = lineThickness,
                    cap = StrokeCap.Round
                )
                // Third short horizontal line
                drawLine(
                    color = Color(0xFF673AB7),
                    start = Offset(lineLeft, h * 0.62f),
                    end = Offset(w * 0.52f, h * 0.62f),
                    strokeWidth = lineThickness,
                    cap = StrokeCap.Round
                )
                
                // Beautiful green badge checked badge status indicator at bottom right
                drawCircle(
                    color = Color(0xFF00E676), // Bright neon green for delightful checkmark success
                    radius = w * 0.18f,
                    center = Offset(w * 0.78f, h * 0.74f)
                )
                
                // Miniature tick check symbol inside the badge
                val tickPath = Path().apply {
                    moveTo(w * 0.71f, h * 0.74f)
                    lineTo(w * 0.77f, h * 0.80f)
                    lineTo(w * 0.85f, h * 0.68f)
                }
                drawPath(
                    tickPath,
                    color = Color.White,
                    style = Stroke(
                        width = w * 0.045f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
