package com.ioskeyboard.ui

import android.os.Build
import android.os.Build.VERSION_CODES.S
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.ioskeyboard.model.KeyboardLayout

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun KeyboardScreen(
    viewModel: KeyboardViewModel,
    modifier: Modifier = Modifier
) {
    val currentLayout by viewModel.currentLayout.collectAsState()
    val isShifted by viewModel.isShifted.collectAsState()
    val isCapsLock by viewModel.isCapsLock.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val keyboardHeight by viewModel.keyboardHeight.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val layout = viewModel.keyboardLayout
    val colorScheme = MaterialTheme.colorScheme
    val isShiftActive = isShifted || isCapsLock

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(keyboardHeight.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(colorScheme.background)
            .drawBehind {
                drawBlurBackground(colorScheme)
            }
            .then(
                if (Build.VERSION.SDK_INT >= S) {
                    Modifier.graphicsLayer {
                        alpha = 0.99f
                    }
                } else {
                    Modifier
                }
            ),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SuggestionBar(
                suggestions = suggestions,
                colors = colorScheme,
                onSuggestionClick = { suggestion ->
                    viewModel.onKeyPress(0, suggestion)
                },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedContent(
                targetState = layout,
                transitionSpec = {
                    val direction = if (targetState.name > initialState.name) 1 else -1
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { fullWidth -> fullWidth * direction }
                    ) + fadeIn(tween(300)) togetherWith
                    slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { fullWidth -> -fullWidth * direction }
                    ) + fadeOut(tween(300))
                },
                label = "layoutTransition"
            ) { targetLayout ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    targetLayout.rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.keys.forEach { key ->
                                val keyWeight = when (key.code) {
                                    32, 59, 67, 1000, 10, -1 -> 1.5f
                                    else -> 1f
                                }
                                KeyboardKey(
                                    key = key,
                                    isShiftActive = isShiftActive,
                                    colors = colorScheme,
                                    onKeyPress = { viewModel.onKeyPress(key.code, key.label) },
                                    onLongPress = { viewModel.onLongPress(key.code) },
                                    modifier = Modifier.weight(keyWeight)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBlurBackground(colorScheme: androidx.compose.material3.ColorScheme) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorScheme.background.copy(alpha = 0.95f),
            colorScheme.background.copy(alpha = 0.85f)
        ),
        startY = 0f,
        endY = size.height
    )
    drawRect(brush = gradient)

    val noiseAlpha = 0.03f
    for (i in 0 until 20) {
        for (j in 0 until 20) {
            val x = (size.width / 20) * i + (size.width / 40)
            val y = (size.height / 20) * j + (size.height / 40)
            drawCircle(
                color = if (colorScheme.onBackground == Color.White) Color.White else Color.Black,
                radius = 1.dp.toPx() / 2,
                center = Offset(x, y),
                alpha = noiseAlpha
            )
        }
    }
}
