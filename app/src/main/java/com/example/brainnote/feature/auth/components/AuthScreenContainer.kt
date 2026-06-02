package com.example.brainnote.feature.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A shared container for authentication screens to reduce layout duplication.
 * Provides a standard white background, safe area padding, keyboard avoiding (imePadding),
 * and a vertically scrollable column.
 *
 * @param modifier Custom modifier to be applied to the outer box.
 * @param horizontalAlignment Alignment for the inner column contents.
 * @param innerPadding Padding values for the inner column.
 * @param content The composable content inside the container.
 */
@Composable
fun AuthScreenContainer(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    innerPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding() // Absolute edge-to-edge system padding
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // Keyboard avoiding support
                .verticalScroll(rememberScrollState()) // Responsive scaling
                .padding(innerPadding),
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}
