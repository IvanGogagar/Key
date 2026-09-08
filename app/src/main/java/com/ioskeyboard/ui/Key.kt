package com.ioskeyboard.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.GenericShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ioskeyboard.model.Key
import com.ioskeyboard.model.KeyType

private val KEY_SHAPE = RoundedCornerShape(5.dp)
private const val KEY_HEIGHT_DP = 43
private const val POPUP_CORNER_RADIUS_DP = 9

private val iOSKeySpring = spring<Float>(
    dampingRatio = 0.55f,
    stiffness = 800f
)

private val popupSpring = spring<Float>(dampingRatio = 0.48f, stiffness = 1200f)
private val popupDismissTween = tween<Float>(durationMillis = 40, easing = LinearOutSlowInEasing)
private val popupAlphaEnterTween = tween<Float>(durationMillis = 100, easing = LinearOutSlowInEasing)

@Stable
private data class KeyColors(
    val topColor: Color,
    val bottomColor: Color,
    val border: Color,
    val text: Color,
    val glow: Color
)

private val LightCharReleased = KeyColors(
    topColor = Color(0xFFFFFFFF),
    bottomColor = Color(0xFFE4E5E9),
    border = Color(0x1F000000),
    text = Color.Black,
    glow = Color.White.copy(alpha = 0.35f)
)
private val LightCharPressed = KeyColors(
    topColor = Color(0xFFB8BCDB),
    bottomColor = Color(0xFFA6AEC2),
    border = Color(0x1F000000),
    text = Color.Black,
    glow = Color.White.copy(alpha = 0.35f)
)
private val DarkCharReleased = KeyColors(
    topColor = Color(0xFF636366),
    bottomColor = Color(0xFF4A4A4C),
    border = Color(0x26FFFFFF),
    text = Color.White,
    glow = Color.White.copy(alpha = 0.04f)
)
private val DarkCharPressed = KeyColors(
    topColor = Color(0xFF3A3A3C),
    bottomColor = Color(0xFF2C2C2E),
    border = Color(0x26FFFFFF),
    text = Color.White,
    glow = Color.White.copy(alpha = 0.04f)
)
private val EnterReleased = KeyColors(
    topColor = Color(0xFF007AFF),
    bottomColor = Color(0xFF0062CC),
    border = Color(0x1F000000),
    text = Color.White,
    glow = Color.White.copy(alpha = 0.12f)
)
private val EnterPressed = KeyColors(
    topColor = Color(0xFF0055BB),
    bottomColor = Color(0xFF004499),
    border = Color(0x1F000000),
    text = Color.White,
    glow = Color.White.copy(alpha = 0.12f)
)
private val DarkSpecialReleased = KeyColors(
    topColor = Color(0xFF636366),
    bottomColor = Color(0xFF4A4A4C),
    border = Color(0x26FFFFFF),
    text = Color.White,
    glow = Color.White.copy(alpha = 0.04f)
)
private val DarkSpecialPressed = KeyColors(
    topColor = Color(0xFF3A3A3C),
    bottomColor = Color(0xFF2C2C2E),
    border = Color(0x26FFFFFF),
    text = Color.White,
    glow = Color.White.copy(alpha = 0.04f)
)
private val LightSpecialReleased = KeyColors(
    topColor = Color(0xFFADB3BC),
    bottomColor = Color(0xFF9EA3AC),
    border = Color(0x1F000000),
    text = Color.Black,
    glow = Color.White.copy(alpha = 0.22f)
)
private val LightSpecialPressed = KeyColors(
    topColor = Color(0xFFD1D3D9),
    bottomColor = Color(0xFFC4C6CC),
    border = Color(0x1F000000),
    text = Color.Black,
    glow = Color.White.copy(alpha = 0.22f)
)

private fun getKeyColors(type: KeyType, dark: Boolean, pressed: Boolean): KeyColors = when {
    type == KeyType.CHARACTER -> if (dark) {
        if (pressed) DarkCharPressed else DarkCharReleased
    } else {
        if (pressed) LightCharPressed else LightCharReleased
    }
    type == KeyType.ENTER -> if (pressed) EnterPressed else EnterReleased
    else -> if (dark) {
        if (pressed) DarkSpecialPressed else DarkSpecialReleased
    } else {
        if (pressed) LightSpecialPressed else LightSpecialReleased
    }
}

private fun vibrateKey(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(5, 30))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(5)
        }
    } catch (_: Exception) { }
}

@Composable
fun KeyboardKey(
    key: Key,
    isDarkTheme: Boolean,
    onKeyPress: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var isPressed by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }
    var keySize by remember { mutableStateOf(IntSize.Zero) }

    val isCharacter = remember(key.type) { key.type == KeyType.CHARACTER }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = iOSKeySpring,
        label = "scale"
    )
    val translationY by animateFloatAsState(
        targetValue = if (isPressed) with(density) { 1.5f.dp.toPx() } else 0f,
        animationSpec = iOSKeySpring,
        label = "translationY"
    )

    val colors = remember(key.type, isDarkTheme, isPressed) {
        getKeyColors(key.type, isDarkTheme, isPressed)
    }

    Box(
        modifier = modifier
            .height(KEY_HEIGHT_DP.dp)
            .onGloballyPositioned { keySize = it.size }
            .shadow(
                elevation = 3.dp,
                shape = KEY_SHAPE,
                ambientColor = if (isDarkTheme) Color.Black.copy(alpha = 0.40f) else Color.Black.copy(alpha = 0.12f),
                spotColor = if (isDarkTheme) Color.Black.copy(alpha = 0.60f) else Color.Black.copy(alpha = 0.18f)
            )
            .scale(scale)
            .offset(IntOffset(0, translationY.toInt()))
            .clip(KEY_SHAPE)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.topColor, colors.bottomColor),
                    startY = 0f,
                    endY = Float.MAX_VALUE
                )
            )
            .border(0.5.dp, colors.border, KEY_SHAPE)
            .drawWithCache {
                val glowRadius = size.height * 0.8f
                val glowBrush = Brush.radialGradient(
                    colors = listOf(colors.glow, Color.Transparent),
                    center = Offset(center.x, 0f),
                    radius = glowRadius
                )
                val cornerRadius = CornerRadius(5.dp.toPx())
                onDrawWithContent {
                    drawRoundRect(
                        brush = glowBrush,
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                        cornerRadius = cornerRadius
                    )
                    drawContent()
                }
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    showPopup = isCharacter
                    vibrateKey(context)

                    var up = false
                    while (!up) {
                        val event = awaitPointerEvent()
                        for (change in event.changes) {
                            if (!change.pressed) {
                                up = true
                                break
                            }
                        }
                    }

                    isPressed = false
                    showPopup = false
                    if (isCharacter) onKeyPress() else onLongPress()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key.label,
            color = colors.text,
            fontSize = if (isCharacter) 22.sp else 15.sp,
            fontWeight = if (isCharacter) FontWeight.Light else FontWeight.Medium
        )
    }

    KeyPopupPreview(
        label = key.label,
        isDark = isDarkTheme,
        visible = showPopup && isPressed,
        keyWidthPx = keySize.width,
        keyHeightPx = keySize.height
    )
}

@Composable
private fun KeyPopupPreview(
    label: String,
    isDark: Boolean,
    visible: Boolean,
    keyWidthPx: Int,
    keyHeightPx: Int
) {
    val density = LocalDensity.current
    val cornerRadiusPx = with(density) { POPUP_CORNER_RADIUS_DP.dp.toPx() }

    val scaleX by animateFloatAsState(
        targetValue = if (visible) 1f else 0.4f,
        animationSpec = if (visible) popupSpring else popupDismissTween,
        label = "popupScaleX"
    )
    val scaleY by animateFloatAsState(
        targetValue = if (visible) 1f else 0.4f,
        animationSpec = if (visible) popupSpring else popupDismissTween,
        label = "popupScaleY"
    )
    val animAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = if (visible) popupAlphaEnterTween else popupDismissTween,
        label = "popupAlpha"
    )
    val animTranslationY by animateFloatAsState(
        targetValue = if (visible) {
            with(density) { -(keyHeightPx + 10.dp.roundToPx()).toFloat() }
        } else {
            with(density) { 20.dp.toPx() }
        },
        animationSpec = if (visible) popupSpring else popupDismissTween,
        label = "popupTranslationY"
    )

    val w = if (keyWidthPx > 0) keyWidthPx.toFloat() else with(density) { 40.dp.toPx() }
    val h = if (keyHeightPx > 0) keyHeightPx.toFloat() else with(density) { KEY_HEIGHT_DP.dp.toPx() }

    val bubbleW = w * 1.4f
    val bubbleH = h * 1.2f
    val stemW = w
    val stemH = h * 0.3f
    val totalH = bubbleH + stemH

    val popupPath = remember(bubbleW, bubbleH, stemW, stemH, cornerRadiusPx) {
        buildPopupPath(bubbleW, bubbleH, stemW, stemH, cornerRadiusPx)
    }

    val popupShape = remember(popupPath) {
        GenericShape<Nothing> { _, _ -> addPath(popupPath) }
    }

    val bgColor = remember(isDark) { if (isDark) Color(0xFF5A5A5E) else Color.White }
    val bgBottom = remember(isDark) { if (isDark) Color(0xFF48484A) else Color(0xFFF2F2F2) }
    val borderColor = remember(isDark) { if (isDark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.12f) }
    val textColor = remember(isDark) { if (isDark) Color.White else Color.Black }

    val popupBgBrush = remember(bgColor, bgBottom, totalH) {
        Brush.verticalGradient(
            colors = listOf(bgColor, bgBottom),
            startY = 0f,
            endY = totalH
        )
    }
    val popupStroke = remember {
        androidx.compose.ui.graphics.drawscope.Stroke(width = 0.5f)
    }

    Popup(
        alignment = Alignment.TopCenter,
        offset = IntOffset(0, animTranslationY.toInt()),
        properties = PopupProperties(focusable = false, clippingEnabled = false)
    ) {
        Box(
            modifier = Modifier
                .size(with(density) { bubbleW.toDp() }, with(density) { totalH.toDp() })
                .scale(scaleX, scaleY)
                .alpha(animAlpha)
                .shadow(12.dp, popupShape, spotColor = Color.Black.copy(alpha = 0.35f))
                .drawBehind {
                    drawPath(path = popupPath, brush = popupBgBrush)
                    drawPath(path = popupPath, color = borderColor, style = popupStroke)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.offset(y = with(density) { -(stemH / 2).toDp() })
            )
        }
    }
}

private fun buildPopupPath(
    bubbleW: Float,
    bubbleH: Float,
    stemW: Float,
    stemH: Float,
    cr: Float
): Path {
    val path = Path()
    val stemLeft = (bubbleW - stemW) / 2f
    val stemRight = stemLeft + stemW
    val stemTop = stemH
    val stemBot = stemH + bubbleH

    path.moveTo(stemLeft, stemBot)
    path.lineTo(stemLeft, stemTop + cr)
    path.quadraticBezierTo(stemLeft, stemTop, stemLeft + cr, stemTop)
    path.lineTo(stemRight - cr, stemTop)
    path.quadraticBezierTo(stemRight, stemTop, stemRight, stemTop + cr)
    path.lineTo(stemRight, stemBot)

    path.lineTo(stemRight - cr, stemBot)
    path.quadraticBezierTo(stemRight, stemBot, stemRight, stemBot - cr)
    path.lineTo(stemRight, cr)
    path.quadraticBezierTo(stemRight, 0f, stemRight - cr, 0f)
    path.lineTo(cr, 0f)
    path.quadraticBezierTo(0f, 0f, 0f, cr)
    path.lineTo(0f, stemBot - cr)
    path.quadraticBezierTo(0f, stemBot, cr, stemBot)

    path.close()
    return path
}
