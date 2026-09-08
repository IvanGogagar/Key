package com.ioskeyboard.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.ioskeyboard.model.LayoutProvider
import com.ioskeyboard.model.LayoutType

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun KeyboardScreen(
    viewModel: KeyboardViewModel,
    modifier: Modifier = Modifier
) {
    val currentLayout by viewModel.currentLayout.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val keyboardLayout = LayoutProvider.getLayout(currentLayout)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(282.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            .drawBehind {
                drawIosBackground(isDarkTheme)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 3.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SuggestionBar(
                suggestions = suggestions,
                isDarkTheme = isDarkTheme,
                onSuggestionClick = { suggestion ->
                    viewModel.onSuggestionSelected(suggestion)
                },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedContent(
                targetState = currentLayout,
                transitionSpec = {
                    val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                    slideInHorizontally(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetX = { fullWidth -> (fullWidth * direction * 0.3f).toInt() }
                    ) + fadeIn(
                        animationSpec = tween(200)
                    ) togetherWith slideOutHorizontally(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        targetOffsetX = { fullWidth -> (-fullWidth * direction * 0.3f).toInt() }
                    ) + fadeOut(
                        animationSpec = tween(200)
                    )
                },
                label = "layoutTransition"
            ) { targetLayoutType ->
                val targetLayout = LayoutProvider.getLayout(targetLayoutType)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    targetLayout.rows.forEachIndexed { rowIndex, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (rowIndex == targetLayout.rows.lastIndex) {
                                        Modifier.padding(horizontal = 4.dp)
                                    } else {
                                        Modifier.padding(horizontal = 2.dp)
                                    }
                                ),
                            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.keys.forEach { key ->
                                KeyboardKey(
                                    key = key,
                                    isDarkTheme = isDarkTheme,
                                    onKeyPress = { viewModel.onKeyPress(key.code, key.label) },
                                    onLongPress = { viewModel.onLongPress(key.code) },
                                    modifier = Modifier.weight(key.widthWeight)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawIosBackground(isDarkTheme: Boolean) {
    if (isDarkTheme) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF3A3A3C),
                    Color(0xFF2C2C2E),
                    Color(0xFF1C1C1E)
                ),
                startY = 0f,
                endY = size.height
            )
        )
    } else {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFD5D5DA),
                    Color(0xFFC9C9CE),
                    Color(0xFFBEBEC3)
                ),
                startY = 0f,
                endY = size.height
            )
        )
    }

    val lineColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.04f)
    } else {
        Color.Black.copy(alpha = 0.05f)
    }
    for (i in 0..30) {
        val y = (size.height / 30) * i
        drawLine(
            color = lineColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 0.5f
        )
    }

    val highlightColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.02f)
    } else {
        Color.White.copy(alpha = 0.35f)
    }
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                highlightColor,
                Color.Transparent
            ),
            startY = 0f,
            endY = size.height * 0.15f
        )
    )
}
