package com.example.brainnote.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FC)) // Clean premium SaaS background
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Top Section Title ---
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Các mẫu Card Task – 4 loại công việc",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Thiết kế tối ưu cho trải nghiệm trực quan, dễ thao tác và phân loại",
            fontSize = 15.sp,
            color = TextSlate,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        // --- Responsive Layout Configuration ---
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val width = maxWidth
            when {
                width >= 960.dp -> DesktopResponsiveLayout()
                width >= 600.dp -> TabletResponsiveLayout()
                else -> MobileResponsiveLayout()
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- Bottom Features Section ---
        Text(
            text = "Tính Năng Nổi Bật",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Grid for Bottom Features (2x2 on small, 4x1 on large screens)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            FeaturesBottomGrid(width = maxWidth)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DesktopResponsiveLayout() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) { Category1Section() }
        Column(modifier = Modifier.weight(1f)) { Category2Section() }
        Column(modifier = Modifier.weight(1f)) { Category3Section() }
        Column(modifier = Modifier.weight(1f)) { Category4Section() }
    }
}

@Composable
fun TabletResponsiveLayout() {
    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) { Category1Section() }
            Column(modifier = Modifier.weight(1f)) { Category2Section() }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) { Category3Section() }
            Column(modifier = Modifier.weight(1f)) { Category4Section() }
        }
    }
}

@Composable
fun MobileResponsiveLayout() {
    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
        Category1Section()
        Category2Section()
        Category3Section()
        Category4Section()
    }
}

@Composable
fun FeaturesBottomGrid(width: androidx.compose.ui.unit.Dp) {
    if (width >= 720.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureItem(
                icon = "⚡",
                title = "Nhanh chóng",
                description = "Tạo và ghi chú chỉ trong vài giây",
                modifier = Modifier.weight(1f)
            )
            FeatureItem(
                icon = "🎯",
                title = "Tập trung",
                description = "Tập trung vào điều quan trọng",
                modifier = Modifier.weight(1f)
            )
            FeatureItem(
                icon = "📈",
                title = "Hiệu quả",
                description = "Theo dõi tiến độ dễ dàng",
                modifier = Modifier.weight(1f)
            )
            FeatureItem(
                icon = "❤️",
                title = "Truyền cảm hứng",
                description = "Giao diện đẹp, tạo động lực",
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureItem(
                    icon = "⚡",
                    title = "Nhanh chóng",
                    description = "Ghi chú trong vài giây",
                    modifier = Modifier.weight(1f)
                )
                FeatureItem(
                    icon = "🎯",
                    title = "Tập trung",
                    description = "Vào điều quan trọng",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureItem(
                    icon = "📈",
                    title = "Hiệu quả",
                    description = "Theo dõi tiến độ",
                    modifier = Modifier.weight(1f)
                )
                FeatureItem(
                    icon = "❤️",
                    title = "Truyền cảm hứng",
                    description = "Giao diện tạo động lực",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun Category1Section() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CategoryHeader(
            title = "Ghi chú nhanh",
            description = "Ghi lại ý tưởng ngay lập tức",
            icon = { OutlineLightbulbIcon(color = ThemePurple) },
            color = ThemePurple
        )

        // Card 1
        TaskCard(
            title = "Ý tưởng ứng dụng học nhóm",
            tags = listOf("Cá nhân", "Ý tưởng"),
            description = "Một ứng dụng giúp sinh viên tạo nhóm học, chia sẻ tài liệu và thảo luận theo thời gian thực.",
            timeText = "18:30, Hôm nay",
            tagColors = Pair(ThemePurpleBg, ThemePurple),
            badgeText = "+2",
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        )

        // Card 2: Purple Decorative Card with Bulb Illustration
        DecorativeTaskCard(
            title = "Sáng tạo không giới hạn",
            description = "Lưu lại mọi suy nghĩ đột phá trước khi chúng biến mất.",
            bgBrush = Brush.linearGradient(
                colors = listOf(Color(0xFFA855F7), Color(0xFF7C4DFF))
            ),
            illustration = { LightbulbIllustration() },
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        )
    }
}

@Composable
fun Category2Section() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CategoryHeader(
            title = "Kế hoạch tuần",
            description = "Lập kế hoạch cho những ngày sắp tới",
            icon = { OutlineCalendarIcon(color = ThemeGreen) },
            color = ThemeGreen
        )

        // Card 1
        ProgressCard(
            title = "Kế hoạch học tập Tuần 20",
            badgeText = "Tuần 20",
            description = "Ôn tập các chương quan trọng, làm bài tập và chuẩn bị cho kỳ thi giữa kỳ.",
            dateText = "12 – 18 May, 2024",
            progress = 0.60f,
            theme = CardTheme(ThemeGreen, ThemeGreenBg, Color(0xFF15803D)),
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        )

        // Card 2: Decorative Card with Notebook Plant Illustration
        DecorativeTaskCard(
            title = "Sắp xếp thông minh",
            description = "Lên lịch trình cụ thể giúp nâng cao hiệu suất học tập.",
            bgBrush = Brush.linearGradient(
                colors = listOf(Color(0xFF4ADE80), Color(0xFF22C55E))
            ),
            illustration = { NotebookPlantIllustration() },
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        )
    }
}

@Composable
fun Category3Section() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CategoryHeader(
            title = "Mục tiêu",
            description = "Theo dõi tiến độ và mục tiêu",
            icon = { OutlineTargetIcon(color = ThemeOrange) },
            color = ThemeOrange
        )

        // Card 1
        GoalCard(
            title = "Đọc 20 cuốn sách trong năm 2024",
            badgeText = "Phát triển bản thân",
            dateText = "01/01/2024 – 31/12/2024",
            progress = 0.45f,
            theme = CardTheme(ThemeOrange, ThemeOrangeBg, Color(0xFFC2410C)),
            chartContent = {
                // Draws default empty placeholder when not hovered, draws actual chart
                OrangeLineChart()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        )

        // Card 2
        ProgressCard(
            title = "Chạy bộ 500km năm 2024",
            badgeText = "Sức khỏe",
            description = "Duy trì thói quen chạy bộ mỗi buổi sáng để cải thiện thể lực và sức bền.",
            dateText = "Hàng ngày",
            progress = 0.38f,
            theme = CardTheme(ThemeOrange, ThemeOrangeBg, Color(0xFFC2410C)),
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        )
    }
}

@Composable
fun Category4Section() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CategoryHeader(
            title = "Nhiệm vụ",
            description = "Checklist các việc cần hoàn thành",
            icon = { OutlineClipboardIcon(color = ThemePink) },
            color = ThemePink
        )

        // Card 1
        TaskCard(
            title = "Nộp báo cáo dự án UI/UX",
            tags = listOf("Work", "Cao"),
            description = "Hoàn thiện báo cáo và slide trình bày dự án cuối kỳ.",
            timeText = "20 May, 2024",
            tagColors = Pair(ThemePinkBg, Color(0xFFBE123C)),
            badgeText = "Gấp",
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        )

        // Card 2 Checklist Card
        ChecklistCard(
            title = "Checklist báo cáo UI/UX",
            tags = listOf("Checklist"),
            tagColors = Pair(ThemePinkBg, Color(0xFFBE123C)),
            checklistItems = listOf(
                Pair("Hoàn thiện nội dung báo cáo", true),
                Pair("Thiết kế slide trình bày", true),
                Pair("Kiểm tra và chỉnh sửa", false),
                Pair("Nộp báo cáo", false)
            ),
            illustration = { ClipboardIllustration() },
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        )
    }
}

// Outline vector drawings for Category Headers
@Composable
fun OutlineLightbulbIcon(color: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val cy = h * 0.42f
        val r = w * 0.28f

        drawCircle(
            color = color,
            radius = r,
            center = Offset(cx, cy),
            style = Stroke(width = dpToPx(1.8f))
        )
        // Thread lines
        drawLine(
            color = color,
            start = Offset(cx - r * 0.5f, cy + r),
            end = Offset(cx + r * 0.5f, cy + r),
            strokeWidth = dpToPx(1.8f)
        )
        drawLine(
            color = color,
            start = Offset(cx - r * 0.4f, cy + r + dpToPx(3)),
            end = Offset(cx + r * 0.4f, cy + r + dpToPx(3)),
            strokeWidth = dpToPx(1.8f)
        )
    }
}

@Composable
fun OutlineCalendarIcon(color: Color) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, dpToPx(3)),
            size = Size(w, h - dpToPx(3)),
            cornerRadius = CornerRadius(dpToPx(3), dpToPx(3)),
            style = Stroke(width = dpToPx(1.8f))
        )
        // Binder tabs
        drawLine(
            color = color,
            start = Offset(w * 0.25f, 0f),
            end = Offset(w * 0.25f, dpToPx(5)),
            strokeWidth = dpToPx(1.8f),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.75f, 0f),
            end = Offset(w * 0.75f, dpToPx(5)),
            strokeWidth = dpToPx(1.8f),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun OutlineTargetIcon(color: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        drawCircle(
            color = color,
            radius = size.width * 0.4f,
            center = Offset(cx, cy),
            style = Stroke(width = dpToPx(1.8f))
        )
        drawCircle(
            color = color,
            radius = size.width * 0.22f,
            center = Offset(cx, cy),
            style = Stroke(width = dpToPx(1.5f))
        )
        drawCircle(
            color = color,
            radius = size.width * 0.08f,
            center = Offset(cx, cy)
        )
    }
}

@Composable
fun OutlineClipboardIcon(color: Color) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val clipW = w * 0.45f
        val clipH = h * 0.15f

        drawRoundRect(
            color = color,
            topLeft = Offset(0f, clipH / 2),
            size = Size(w, h - clipH / 2),
            cornerRadius = CornerRadius(dpToPx(3), dpToPx(3)),
            style = Stroke(width = dpToPx(1.8f))
        )

        drawRoundRect(
            color = color,
            topLeft = Offset((w - clipW) / 2, 0f),
            size = Size(clipW, clipH),
            cornerRadius = CornerRadius(dpToPx(2), dpToPx(2))
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.dpToPx(dp: Float): Float {
    return dp * density
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.dpToPx(dp: Int): Float {
    return dp.toFloat() * density
}
