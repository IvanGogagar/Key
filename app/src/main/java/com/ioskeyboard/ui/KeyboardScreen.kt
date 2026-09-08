package com.ioskeyboard.ui

import android.os.Build
import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ioskeyboard.model.KeyRow
import com.ioskeyboard.model.LayoutProvider
import com.ioskeyboard.model.LayoutType

private const val KEYBOARD_HEIGHT_RATIO = 0.34f
private const val MIN_KEYBOARD_HEIGHT_DP = 260
private const val MAX_KEYBOARD_HEIGHT_DP = 320
private const val ROW_SPACING_DP = 5
private const val KEY_SPACING_DP = 5
private const val BOTTOM_ROW_PADDING_DP = 2
private const val CORNER_RADIUS_DP = 14
private const val HORIZONTAL_PADDING_DP = 3
private const val VERTICAL_PADDING_DP = 2

private val LightGradientStops = arrayOf(
    0.00f to Color(0xFFE3E5EB),
    0.25f to Color(0xFFD6D9E0),
    0.60f to Color(0xFFCBD0D9),
    1.00f to Color(0xFFBCC1CD)
)

private val DarkGradientStops = arrayOf(
    0.00f to Color(0xFF3A3A3C),
    0.30f to Color(0xFF2C2C2E),
    0.70f to Color(0xFF1E1E20),
    1.00f to Color(0xFF121214)
)

private val SCANLINE_STEP_DP = 3.dp
private val HIGHLIGHT_HEIGHT_DP = 1.5.dp
private const val HIGHLIGHT_ALPHA_LIGHT = 0.45f
private const val HIGHLIGHT_ALPHA_DARK = 0.015f
private const val NOISE_ALPHA_LIGHT = 0.025f
private const val NOISE_ALPHA_DARK = 0.018f
private const val BLUR_RADIUS = 25f

private val keyboardShape = RoundedCornerShape(
    topStart = CORNER_RADIUS_DP.dp,
    topEnd = CORNER_RADIUS_DP.dp
)

private val scanlineStroke = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.5f)

@Composable
fun KeyboardScreen(
    viewModel: KeyboardViewModel,
    modifier: Modifier = Modifier
) {
    val currentLayoutType by viewModel.currentLayout.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val onKeyPress = rememberUpdatedState(viewModel::onKeyPress)
    val onLongPressStart = rememberUpdatedState(viewModel::onLongPressStart)
    val onLongPressEnd = rememberUpdatedState(viewModel::onLongPressEnd)
    val onSuggestionSelected = rememberUpdatedState(viewModel::onSuggestionSelected)

    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = config.screenHeightDp * density.density
    val keyboardHeightDp = remember(screenHeightPx) {
        val raw = (screenHeightPx * KEYBOARD_HEIGHT_RATIO / density.density).toInt()
        raw.coerceIn(MIN_KEYBOARD_HEIGHT_DP, MAX_KEYBOARD_HEIGHT_DP)
    }

    val layout = remember(currentLayoutType) { LayoutProvider.getLayout(currentLayoutType) }
    val cachedRows: State<List<KeyRow>> = remember(layout) { derivedStateOf { layout.rows } }

    val blurEnabled = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(keyboardHeightDp.dp)
            .shadow(
                elevation = 12.dp,
                shape = keyboardShape,
                ambientColor = Color.Black.copy(alpha = 0.18f),
                spotColor = Color.Black.copy(alpha = 0.30f)
            )
            .clip(keyboardShape)
    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    if (blurEnabled) {
                        renderEffect = RenderEffect
                            .createBlurEffect(BLUR_RADIUS, BLUR_RADIUS, Shader.TileMode.MIRROR)
                            .asComposeRenderEffect()
                    } else {
                        alpha = 0.92f
                    }
                }
                .drawWithCache {
                    val width = size.width
                    val height = size.height

                    val gradientBrush = Brush.verticalGradient(
                        colorStops = if (isDarkTheme) DarkGradientStops else LightGradientStops,
                        startY = 0f,
                        endY = height
                    )

                    val highlightTopPx = with(density) { HIGHLIGHT_HEIGHT_DP.toPx() }
                    val highlightColor = if (isDarkTheme) {
                        Color.White.copy(alpha = HIGHLIGHT_ALPHA_DARK)
                    } else {
                        Color.White.copy(alpha = HIGHLIGHT_ALPHA_LIGHT)
                    }
                    val highlightBrush = Brush.verticalGradient(
                        colors = listOf(highlightColor, Color.Transparent),
                        startY = 0f,
                        endY = highlightTopPx
                    )

                    val scanlineStepPx = with(density) { SCANLINE_STEP_DP.toPx() }
                    val scanlineColor = if (isDarkTheme) {
                        Color.White.copy(alpha = NOISE_ALPHA_DARK)
                    } else {
                        Color.Black.copy(alpha = NOISE_ALPHA_LIGHT)
                    }
                    val scanlineCount = (height / scanlineStepPx).toInt().coerceAtMost(120)
                    val scanlinePath = Path().apply {
                        var y = 0f
                        var i = 0
                        while (i < scanlineCount) {
                            moveTo(0f, y)
                            lineTo(width, y)
                            y += scanlineStepPx
                            i++
                        }
                    }

                    onDrawWithContent {
                        drawRect(brush = gradientBrush)
                        drawRect(brush = highlightBrush)
                        drawPath(scanlinePath, color = scanlineColor, style = scanlineStroke)
                    }
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = HORIZONTAL_PADDING_DP.dp,
                    vertical = VERTICAL_PADDING_DP.dp
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SuggestionBar(
                suggestions = suggestions,
                isDarkTheme = isDarkTheme,
                onSuggestionClick = { onSuggestionSelected.value(it) },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedContent(
                targetState = currentLayoutType,
                transitionSpec = {
                    val isForward = targetState.ordinal > initialState.ordinal

                    slideInHorizontally(
                        animationSpec = tween(150),
                        initialOffsetX = { if (isForward) it / 3 else -it / 3 }
                    ) + fadeIn(
                        animationSpec = tween(150)
                    ) + scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(150)
                    ) togetherWith
                    slideOutHorizontally(
                        animationSpec = tween(150),
                        targetOffsetX = { if (isForward) -it / 3 else it / 3 }
                    ) + fadeOut(
                        animationSpec = tween(150)
                    ) + scaleOut(
                        targetScale = 0.96f,
                        animationSpec = tween(150)
                    ) using SizeTransform(clip = false)
                },
                label = "layoutAnim"
            ) { targetLayoutType ->
                val targetLayout = remember(targetLayoutType) { LayoutProvider.getLayout(targetLayoutType) }
                val rows = targetLayout.rows

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(ROW_SPACING_DP.dp)
                ) {
                    rows.forEachIndexed { rowIndex, row ->
                        val isLastRow = rowIndex == rows.lastIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = if (isLastRow) BOTTOM_ROW_PADDING_DP.dp else 2.dp
                                ),
                            horizontalArrangement = Arrangement.spacedBy(
                                KEY_SPACING_DP.dp,
                                Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.keys.forEach { key ->
                                KeyboardKey(
                                    key = key,
                                    isDarkTheme = isDarkTheme,
                                    onKeyPress = { onKeyPress.value(key.code, key.label) },
                                    onLongPressStart = { onLongPressStart.value(key.code) },
                                    onLongPressEnd = { onLongPressEnd.value(key.code) },
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
