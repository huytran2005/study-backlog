package com.example.brainnote.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun LightbulbIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val centerX = w * 0.5f
        val centerY = h * 0.45f
        val radius = w * 0.28f

        // Draw soft outer glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x66E9D5FF),
                    Color(0x33C084FC),
                    Color(0x00C084FC)
                ),
                center = Offset(centerX, centerY),
                radius = radius * 1.8f
            ),
            radius = radius * 1.8f,
            center = Offset(centerX, centerY)
        )

        // Draw bulb glass base glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFF3E8FF),
                    Color(0xFFE9D5FF),
                    Color(0xFFD8B4FE)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Bulb glass outline
        drawCircle(
            color = Color(0xFFC084FC),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = dpToPx(2))
        )

        // Metallic thread base of bulb
        val baseWidth = radius * 0.8f
        val baseHeight = h * 0.15f
        val baseLeft = centerX - baseWidth / 2
        val baseTop = centerY + radius * 0.8f

        // Draw 3 threads
        for (i in 0..2) {
            val threadTop = baseTop + (i * baseHeight / 3)
            drawRoundRect(
                color = Color(0xFF94A3B8),
                topLeft = Offset(baseLeft + dpToPx(2), threadTop),
                size = Size(baseWidth - dpToPx(4), (baseHeight / 3.2f)),
                cornerRadius = CornerRadius(dpToPx(4), dpToPx(4))
            )
        }

        // Contact point at bottom
        drawRoundRect(
            color = Color(0xFF475569),
            topLeft = Offset(centerX - baseWidth / 4, baseTop + baseHeight),
            size = Size(baseWidth / 2, dpToPx(8)),
            cornerRadius = CornerRadius(dpToPx(2), dpToPx(2))
        )

        // Filament inside bulb
        val filamentPath = Path().apply {
            moveTo(centerX - radius * 0.3f, centerY + radius * 0.4f)
            lineTo(centerX - radius * 0.2f, centerY - radius * 0.1f)
            quadraticTo(
                centerX, centerY - radius * 0.5f,
                centerX + radius * 0.2f, centerY - radius * 0.1f
            )
            lineTo(centerX + radius * 0.3f, centerY + radius * 0.4f)
        }

        drawPath(
            path = filamentPath,
            color = Color(0xFFA855F7),
            style = Stroke(width = dpToPx(3), cap = StrokeCap.Round)
        )

        // Glow center dot
        drawCircle(
            color = Color.White,
            radius = radius * 0.15f,
            center = Offset(centerX, centerY - radius * 0.1f)
        )
    }
}

@Composable
fun NotebookPlantIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Draw Green Notebook
        val bookW = w * 0.5f
        val bookH = h * 0.65f
        val bookX = w * 0.15f
        val bookY = h * 0.2f

        // Shadow under book
        drawRoundRect(
            color = Color(0x1F166534),
            topLeft = Offset(bookX + dpToPx(4), bookY + dpToPx(6)),
            size = Size(bookW, bookH),
            cornerRadius = CornerRadius(dpToPx(12), dpToPx(12))
        )

        // Book cover
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF4ADE80), Color(0xFF22C55E))
            ),
            topLeft = Offset(bookX, bookY),
            size = Size(bookW, bookH),
            cornerRadius = CornerRadius(dpToPx(12), dpToPx(12))
        )

        // Notebook spine
        drawRoundRect(
            color = Color(0x33166534),
            topLeft = Offset(bookX, bookY),
            size = Size(bookW * 0.15f, bookH),
            cornerRadius = CornerRadius(dpToPx(12), dpToPx(12))
        )

        // Binder rings
        for (i in 0..4) {
            val ringY = bookY + bookH * 0.15f + i * (bookH * 0.18f)
            drawCircle(
                color = Color.White,
                radius = dpToPx(4),
                center = Offset(bookX + bookW * 0.07f, ringY)
            )
        }

        // 2. Draw Plant
        val potX = w * 0.65f
        val potY = h * 0.55f
        val potW = w * 0.25f
        val potH = h * 0.25f

        // Draw Pot
        val potPath = Path().apply {
            moveTo(potX, potY)
            lineTo(potX + potW, potY)
            lineTo(potX + potW * 0.85f, potY + potH)
            lineTo(potX + potW * 0.15f, potY + potH)
            close()
        }
        drawPath(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFED7AA), Color(0xFFFDBA74))
            ),
            path = potPath
        )

        // Pot Rim
        drawRoundRect(
            color = Color(0xFFF97316),
            topLeft = Offset(potX - dpToPx(2), potY - dpToPx(4)),
            size = Size(potW + dpToPx(4), dpToPx(6)),
            cornerRadius = CornerRadius(dpToPx(2), dpToPx(2))
        )

        // Plant stem and leaves
        val stemPath = Path().apply {
            moveTo(potX + potW * 0.5f, potY)
            quadraticTo(potX + potW * 0.4f, potY - h * 0.2f, potX + potW * 0.3f, potY - h * 0.35f)
        }
        drawPath(
            path = stemPath,
            color = Color(0xFF15803D),
            style = Stroke(width = dpToPx(3), cap = StrokeCap.Round)
        )

        // Leaves
        val leaf1Path = Path().apply {
            moveTo(potX + potW * 0.35f, potY - h * 0.15f)
            quadraticTo(
                potX + potW * 0.05f, potY - h * 0.25f,
                potX + potW * 0.1f, potY - h * 0.1f
            )
            quadraticTo(
                potX + potW * 0.35f, potY - h * 0.08f,
                potX + potW * 0.35f, potY - h * 0.15f
            )
        }
        drawPath(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF86EFAC), Color(0xFF22C55E))
            ),
            path = leaf1Path
        )

        val leaf2Path = Path().apply {
            moveTo(potX + potW * 0.3f, potY - h * 0.35f)
            quadraticTo(
                potX + potW * 0.6f, potY - h * 0.45f,
                potX + potW * 0.55f, potY - h * 0.25f
            )
            quadraticTo(
                potX + potW * 0.3f, potY - h * 0.25f,
                potX + potW * 0.3f, potY - h * 0.35f
            )
        }
        drawPath(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF4ADE80), Color(0xFF166534))
            ),
            path = leaf2Path
        )
    }
}

@Composable
fun OrangeLineChart(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Define Points T1-T6
        // T1 to T6 mapped on X (0% to 100%) and Y (bottom to top, e.g. 70% to 20%)
        val points = listOf(
            Offset(w * 0.05f, h * 0.75f),
            Offset(w * 0.23f, h * 0.45f),
            Offset(w * 0.41f, h * 0.60f),
            Offset(w * 0.59f, h * 0.30f),
            Offset(w * 0.77f, h * 0.40f),
            Offset(w * 0.95f, h * 0.15f)
        )

        // Draw grid lines (horizontal helper lines)
        for (i in 1..3) {
            val gridY = h * (i * 0.25f)
            drawLine(
                color = Color(0x0A000000),
                start = Offset(0f, gridY),
                end = Offset(w, gridY),
                strokeWidth = dpToPx(1)
            )
        }

        // Draw gradient area under the line chart
        val fillPath = generateSmoothPath(points, h, true)

        drawPath(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x54FED7AA), Color(0x00FFEDD5))
            ),
            path = fillPath
        )

        // Draw the smooth orange curve
        val linePath = generateSmoothPath(points, h, false)

        drawPath(
            path = linePath,
            color = Color(0xFFFB923C),
            style = Stroke(width = dpToPx(3.5f), cap = StrokeCap.Round)
        )

        // Draw data points (dots)
        points.forEachIndexed { index, point ->
            val color = if (index == points.size - 1) Color(0xFFEA580C) else Color(0xFFFB923C)
            val r = if (index == points.size - 1) dpToPx(6) else dpToPx(4)

            // Outer ring for final point
            if (index == points.size - 1) {
                drawCircle(
                    color = Color(0x4DFB923C),
                    radius = r * 2f,
                    center = point
                )
            }

            drawCircle(
                color = color,
                radius = r,
                center = point
            )
        }

        // Draw flag icon at the last point T6 (Offset at points.last())
        val lastPoint = points.last()
        val flagX = lastPoint.x
        val flagY = lastPoint.y

        // Pole
        drawLine(
            color = Color(0xFFEA580C),
            start = Offset(flagX, flagY - dpToPx(2)),
            end = Offset(flagX, flagY - dpToPx(30)),
            strokeWidth = dpToPx(2),
            cap = StrokeCap.Round
        )

        // Flag triangle
        val flagPath = Path().apply {
            moveTo(flagX, flagY - dpToPx(30))
            lineTo(flagX - dpToPx(16), flagY - dpToPx(23))
            lineTo(flagX, flagY - dpToPx(16))
            close()
        }
        drawPath(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFB923C), Color(0xFFF97316))
            ),
            path = flagPath
        )
    }
}

@Composable
fun ClipboardIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val boardW = w * 0.65f
        val boardH = h * 0.75f
        val boardX = w * 0.15f
        val boardY = h * 0.15f

        // Shadow
        drawRoundRect(
            color = Color(0x15F43F5E),
            topLeft = Offset(boardX + dpToPx(4), boardY + dpToPx(6)),
            size = Size(boardW, boardH),
            cornerRadius = CornerRadius(dpToPx(12), dpToPx(12))
        )

        // Clipboard pink base
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFDA4AF), Color(0xFFF43F5E))
            ),
            topLeft = Offset(boardX, boardY),
            size = Size(boardW, boardH),
            cornerRadius = CornerRadius(dpToPx(12), dpToPx(12))
        )

        // Inner page
        val pageX = boardX + boardW * 0.1f
        val pageY = boardY + boardH * 0.15f
        val pageW = boardW * 0.8f
        val pageH = boardH * 0.75f
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(pageX, pageY),
            size = Size(pageW, pageH),
            cornerRadius = CornerRadius(dpToPx(6), dpToPx(6))
        )

        // Top Clip (metal)
        val clipW = boardW * 0.4f
        val clipH = boardH * 0.12f
        val clipX = boardX + (boardW - clipW) / 2
        val clipY = boardY - clipH / 3
        drawRoundRect(
            color = Color(0xFF94A3B8),
            topLeft = Offset(clipX, clipY),
            size = Size(clipW, clipH),
            cornerRadius = CornerRadius(dpToPx(4), dpToPx(4))
        )

        // Lines on page
        for (i in 0..2) {
            val lineY = pageY + pageH * 0.25f + i * (pageH * 0.22f)
            // Draw dummy checklist box
            drawRoundRect(
                color = Color(0xFFFDA4AF),
                topLeft = Offset(pageX + pageW * 0.1f, lineY),
                size = Size(dpToPx(8), dpToPx(8)),
                cornerRadius = CornerRadius(dpToPx(2), dpToPx(2))
            )
            // Draw horizontal dummy line
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(pageX + pageW * 0.25f, lineY + dpToPx(4)),
                end = Offset(pageX + pageW * 0.85f, lineY + dpToPx(4)),
                strokeWidth = dpToPx(2),
                cap = StrokeCap.Round
            )
        }
    }
}

// Simple extension helper to translate density-independent dp to pixel float in Canvas
private fun androidx.compose.ui.graphics.drawscope.DrawScope.dpToPx(dp: Float): Float {
    return dp * density
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.dpToPx(dp: Int): Float {
    return dp * density
}

private fun generateSmoothPath(points: List<Offset>, h: Float, isClosed: Boolean): Path {
    val path = Path().apply {
        if (isClosed) {
            moveTo(points.first().x, h)
            lineTo(points.first().x, points.first().y)
        } else {
            moveTo(points.first().x, points.first().y)
        }
        for (i in 1 until points.size) {
            val pPrev = points[i - 1]
            val pCurr = points[i]
            val cpX = (pPrev.x + pCurr.x) / 2
            quadraticTo(cpX, pPrev.y, pCurr.x, pCurr.y)
        }
        if (isClosed) {
            lineTo(points.last().x, h)
            close()
        }
    }
    return path
}

