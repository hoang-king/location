package com.example.location.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MapMarkerView(
    displayName: String,
    bearing: Float,
    isCurrentUser: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(80.dp), // Kích thước đủ cho hình nón
            contentAlignment = Alignment.Center
        ) {
            // Hình nón (cone)
            Canvas(
                modifier = Modifier
                    .size(60.dp)
                    .rotate(bearing) // Xoay hình nón theo bearing
            ) {
                val path = Path().apply {
                    moveTo(size.width / 2, size.height / 2)
                    // Vẽ hình cung 60 độ
                    arcTo(
                        rect = Rect(
                            Offset(0f, 0f),
                            Size(size.width, size.height)
                        ),
                        startAngleDegrees = -120f,
                        sweepAngleDegrees = 60f,
                        forceMoveTo = false
                    )
                    close()
                }

                drawPath(
                    path = path,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x884285F4),
                            Color(0x004285F4)
                        ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.width / 2
                    ),
                    style = Fill
                )
            }

            // Chấm xanh (Blue dot)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(if (isCurrentUser) Color(0xFF4285F4) else Color.Gray),
                contentAlignment = Alignment.Center
            ) {}
        }

        // Tên hiển thị
        Text(
            text = displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.offset(y = (-15).dp)
        )
    }
}
