package com.ioskeyboard.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyboardKey(
    key: com.ioskeyboard.model.Key,
    isShiftActive: Boolean,
    colors: ColorScheme,
    onKeyPress: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressedState by interactionSource.collectIsPressedAsState()

    var showPopup by remember { mutableStateOf(false) }
    var keySize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }

    LaunchedEffect(isPressedState) {
        if (isPressedState) {
            vibrateKey(context)
            showPopup = true
            delay(500)
            showPopup = false
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressedState) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "keyScale"
    )

    val displayLabel = if (isShiftActive && key.label.length == 1) key.label.uppercase() else key.label
    val density = LocalDensity.current
    val popupOffsetY = with(density) { -(keySize.height + 8.dp.roundToPx()) }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .onGloballyPositioned { coordinates ->
                keySize = coordinates.size
            }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.surface,
                        colors.surface.copy(alpha = 0.9f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
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
                    drawInnerGlow(colors.primary.copy(alpha = 0.1f))
                }
        )

        Text(
            text = displayLabel,
            color = colors.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }

    if (showPopup) {
        Popup(
            alignment = Alignment.TopCenter,
            offset = IntOffset(0, popupOffsetY),
            properties = PopupProperties(focusable = false)
        ) {
            Text(
                text = displayLabel,
                color = colors.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        color = colors.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
            )
        }
    }
}

private fun DrawScope.drawInnerGlow(color: Color) {
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(color, Color.Transparent),
            center = center.copy(y = size.height * 0.2f),
            radius = size.width * 0.8f
        ),
        cornerRadius = CornerRadius(8.dp.toPx()),
        blendMode = BlendMode.Plus
    )
}

private fun vibrateKey(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(20)
    }
}
