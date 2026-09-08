package com.ioskeyboard.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyboardKey(
    key: Key,
    isDarkTheme: Boolean,
    onKeyPress: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressedState by interactionSource.collectIsPressedAsState()

    var showPopup by remember { mutableStateOf(false) }
    var keySize by remember { mutableStateOf(IntSize.Zero) }

    val isCharacter = key.type == KeyType.CHARACTER

    LaunchedEffect(isPressedState) {
        if (isPressedState) {
            if (isCharacter) {
                vibrateKey(context)
                showPopup = true
            }
        } else {
            showPopup = false
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressedState) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "keyScale"
    )

    val density = LocalDensity.current
    val popupOffsetY = with(density) { -(keySize.height + 8.dp.roundToPx()) }

    val keyShape = RoundedCornerShape(6.dp)

    val keyColors = getKeyColors(key, isDarkTheme, isPressedState)

    Box(
        modifier = modifier
            .height(42.dp)
            .onGloballyPositioned { coordinates ->
                keySize = coordinates.size
            }
            .shadow(
                elevation = if (isPressedState) 0.5.dp else 1.5.dp,
                shape = keyShape,
                ambientColor = if (isDarkTheme) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.15f),
                spotColor = if (isDarkTheme) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.25f)
            )
            .clip(keyShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        keyColors.topColor,
                        keyColors.bottomColor
                    ),
                    startY = 0f,
                    endY = Float.MAX_VALUE
                )
            )
            .border(
                width = 0.5.dp,
                color = keyColors.borderColor,
                shape = keyShape
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onKeyPress,
                onLongClick = onLongPress
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawInnerGlow(keyColors.glowColor)
                }
        )

        Text(
            text = key.label,
            color = keyColors.textColor,
            fontSize = if (isCharacter) 22.sp else 14.sp,
            fontWeight = if (isCharacter) FontWeight.Light else FontWeight.Medium
        )
    }

    if (showPopup && isPressedState) {
        Popup(
            alignment = Alignment.TopCenter,
            offset = IntOffset(0, popupOffsetY),
            properties = PopupProperties(focusable = false)
        ) {
            Box(
                modifier = Modifier
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isDarkTheme) {
                                listOf(
                                    Color(0xFF5A5A5E),
                                    Color(0xFF4A4A4E)
                                )
                            } else {
                                listOf(
                                    Color(0xFFFFFFFF),
                                    Color(0xFFF0F0F0)
                                )
                            }
                        )
                    )
                    .border(
                        width = 0.5.dp,
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = key.label,
                    color = if (isDarkTheme) Color.White else Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

private data class KeyColors(
    val topColor: Color,
    val bottomColor: Color,
    val borderColor: Color,
    val textColor: Color,
    val glowColor: Color
)

@Composable
private fun getKeyColors(key: Key, isDarkTheme: Boolean, isPressed: Boolean): KeyColors {
    return when (key.type) {
        KeyType.CHARACTER -> {
            if (isDarkTheme) {
                KeyColors(
                    topColor = if (isPressed) Color(0xFF3A3A3C) else Color(0xFF636366),
                    bottomColor = if (isPressed) Color(0xFF2C2C2E) else Color(0xFF4A4A4C),
                    borderColor = Color.White.copy(alpha = 0.08f),
                    textColor = Color.White,
                    glowColor = Color.White.copy(alpha = 0.05f)
                )
            } else {
                KeyColors(
                    topColor = if (isPressed) Color(0xFFD1D3D9) else Color(0xFFFFFFFF),
                    bottomColor = if (isPressed) Color(0xFFC4C6CC) else Color(0xFFEDEDF0),
                    borderColor = Color.Black.copy(alpha = 0.12f),
                    textColor = Color.Black,
                    glowColor = Color.White.copy(alpha = 0.4f)
                )
            }
        }
        KeyType.SHIFT -> {
            if (isDarkTheme) {
                KeyColors(
                    topColor = if (isPressed) Color(0xFF3A3A3C) else Color(0xFF636366),
                    bottomColor = if (isPressed) Color(0xFF2C2C2E) else Color(0xFF4A4A4C),
                    borderColor = Color.White.copy(alpha = 0.08f),
                    textColor = Color.White,
                    glowColor = Color.White.copy(alpha = 0.05f)
                )
            } else {
                KeyColors(
                    topColor = if (isPressed) Color(0xFFD1D3D9) else Color(0xFFADB3BC),
                    bottomColor = if (isPressed) Color(0xFFC4C6CC) else Color(0xFF9EA3AC),
                    borderColor = Color.Black.copy(alpha = 0.15f),
                    textColor = Color.Black,
                    glowColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }
        KeyType.BACKSPACE -> {
            if (isDarkTheme) {
                KeyColors(
                    topColor = if (isPressed) Color(0xFF3A3A3C) else Color(0xFF636366),
                    bottomColor = if (isPressed) Color(0xFF2C2C2E) else Color(0xFF4A4A4C),
                    borderColor = Color.White.copy(alpha = 0.08f),
                    textColor = Color.White,
                    glowColor = Color.White.copy(alpha = 0.05f)
                )
            } else {
                KeyColors(
                    topColor = if (isPressed) Color(0xFFD1D3D9) else Color(0xFFADB3BC),
                    bottomColor = if (isPressed) Color(0xFFC4C6CC) else Color(0xFF9EA3AC),
                    borderColor = Color.Black.copy(alpha = 0.15f),
                    textColor = Color.Black,
                    glowColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }
        KeyType.SPACE -> {
            if (isDarkTheme) {
                KeyColors(
                    topColor = if (isPressed) Color(0xFF3A3A3C) else Color(0xFF636366),
                    bottomColor = if (isPressed) Color(0xFF2C2C2E) else Color(0xFF4A4A4C),
                    borderColor = Color.White.copy(alpha = 0.08f),
                    textColor = Color.Transparent,
                    glowColor = Color.White.copy(alpha = 0.05f)
                )
            } else {
                KeyColors(
                    topColor = if (isPressed) Color(0xFFD1D3D9) else Color(0xFFFFFFFF),
                    bottomColor = if (isPressed) Color(0xFFC4C6CC) else Color(0xFFEDEDF0),
                    borderColor = Color.Black.copy(alpha = 0.12f),
                    textColor = Color.Transparent,
                    glowColor = Color.White.copy(alpha = 0.4f)
                )
            }
        }
        KeyType.ENTER -> {
            KeyColors(
                topColor = if (isPressed) Color(0xFF005BBB) else Color(0xFF007AFF),
                bottomColor = if (isPressed) Color(0xFF004A99) else Color(0xFF0062CC),
                borderColor = Color.Black.copy(alpha = 0.1f),
                textColor = Color.White,
                glowColor = Color.White.copy(alpha = 0.15f)
            )
        }
        else -> {
            if (isDarkTheme) {
                KeyColors(
                    topColor = if (isPressed) Color(0xFF3A3A3C) else Color(0xFF636366),
                    bottomColor = if (isPressed) Color(0xFF2C2C2E) else Color(0xFF4A4A4C),
                    borderColor = Color.White.copy(alpha = 0.08f),
                    textColor = Color.White,
                    glowColor = Color.White.copy(alpha = 0.05f)
                )
            } else {
                KeyColors(
                    topColor = if (isPressed) Color(0xFFD1D3D9) else Color(0xFFADB3BC),
                    bottomColor = if (isPressed) Color(0xFFC4C6CC) else Color(0xFF9EA3AC),
                    borderColor = Color.Black.copy(alpha = 0.15f),
                    textColor = Color.Black,
                    glowColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}

private fun DrawScope.drawInnerGlow(color: Color) {
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(color, Color.Transparent),
            center = center.copy(y = size.height * 0.15f),
            radius = size.width * 0.9f
        ),
        cornerRadius = CornerRadius(6.dp.toPx()),
        blendMode = BlendMode.Plus
    )
}

private fun vibrateKey(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    } catch (_: Exception) { }
}
