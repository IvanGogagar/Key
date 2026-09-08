package com.ioskeyboard.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val BAR_SHAPE = RoundedCornerShape(8.dp)
private const val MAX_SUGGESTIONS = 3
private val BAR_HEIGHT = 42.dp
private val SEPARATOR_WIDTH = 0.5.dp
private val SEPARATOR_HEIGHT = 22.dp
private val SeparatorColor = Color(0x29000000)

private val barEnter: EnterTransition = expandVertically(
    animationSpec = spring(stiffness = Spring.StiffnessMedium)
) + fadeIn(spring(stiffness = Spring.StiffnessMediumLow))

private val barExit: ExitTransition = shrinkVertically(
    animationSpec = spring(stiffness = Spring.StiffnessHigh)
) + fadeOut(spring(stiffness = Spring.StiffnessHigh))

private val slotScaleSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessHigh
)
private val slotTranslationYSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessMedium
)
private val slotDismissTween = tween<Float>(durationMillis = 150, easing = LinearOutSlowInEasing)
private val slotResetTween = tween<Float>(durationMillis = 100)
private val textSlideTween = tween<IntOffset>(durationMillis = 120, easing = LinearOutSlowInEasing)
private val textFadeTween = tween<Float>(durationMillis = 120, easing = LinearOutSlowInEasing)

@Composable
fun SuggestionBar(
    suggestions: List<String>,
    isDarkTheme: Boolean,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val visible = suggestions.isNotEmpty()
    val items = remember(suggestions) { suggestions.take(MAX_SUGGESTIONS) }
    val currentOnSuggestionClick by rememberUpdatedState(onSuggestionClick)

    val darkBgColor = remember { Color(0xFF636366).copy(alpha = 0.85f) }
    val lightBgColor = remember { Color(0xFFFFFFFF).copy(alpha = 0.92f) }
    val darkBorderColor = remember { Color.White.copy(alpha = 0.06f) }
    val lightBorderColor = remember { Color.Black.copy(alpha = 0.08f) }
    val darkTextColor = remember { Color.White }
    val lightTextColor = remember { Color.Black }

    AnimatedVisibility(
        visible = visible,
        enter = barEnter,
        exit = barExit,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BAR_HEIGHT)
                    .clip(BAR_SHAPE)
                    .background(if (isDarkTheme) darkBgColor else lightBgColor)
                    .border(
                        SEPARATOR_WIDTH,
                        if (isDarkTheme) darkBorderColor else lightBorderColor,
                        BAR_SHAPE
                    )
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, suggestion ->
                    val isLast = index == items.lastIndex

                    SuggestionSlot(
                        text = suggestion,
                        textColor = if (isDarkTheme) darkTextColor else lightTextColor,
                        onClick = { currentOnSuggestionClick(suggestion) },
                        modifier = Modifier.weight(1f)
                    )

                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(SEPARATOR_WIDTH)
                                .height(SEPARATOR_HEIGHT)
                                .background(SeparatorColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionSlot(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isTapped by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    val slotScale by animateFloatAsState(
        targetValue = if (isTapped) 1.08f else 1f,
        animationSpec = slotScaleSpring,
        label = "slotScale"
    )
    val slotTranslationY by animateFloatAsState(
        targetValue = if (isTapped) with(density) { (-15).dp.toPx() } else 0f,
        animationSpec = if (isTapped) slotDismissTween else slotTranslationYSpring,
        label = "slotTranslationY"
    )
    val slotAlpha by animateFloatAsState(
        targetValue = if (isTapped) 0f else 1f,
        animationSpec = if (isTapped) slotDismissTween else slotResetTween,
        label = "slotAlpha"
    )

    LaunchedEffect(isTapped) {
        if (isTapped) {
            delay(120)
            isTapped = false
            onClick()
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .scale(slotScale)
            .offset { IntOffset(0, slotTranslationY.toInt()) }
            .alpha(slotAlpha)
            .clickable(enabled = !isTapped) { isTapped = true },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = text,
            transitionSpec = {
                slideInVertically(
                    animationSpec = textSlideTween,
                    initialOffsetY = { it }
                ) + fadeIn(
                    animationSpec = textFadeTween
                ) togetherWith
                slideOutVertically(
                    animationSpec = textSlideTween,
                    targetOffsetY = { -it }
                ) + fadeOut(
                    animationSpec = textFadeTween
                )
            },
            label = "slotText"
        ) { targetText ->
            Text(
                text = targetText,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
