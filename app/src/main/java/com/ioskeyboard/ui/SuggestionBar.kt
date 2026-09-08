package com.ioskeyboard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SuggestionBar(
    suggestions: List<String>,
    isDarkTheme: Boolean,
    onSuggestionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val visible = suggestions.isNotEmpty()

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(150)
        ) + expandVertically(
            animationSpec = tween(200)
        ),
        exit = fadeOut(
            animationSpec = tween(100)
        ) + shrinkVertically(
            animationSpec = tween(150)
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = if (isDarkTheme) {
                            Color(0xFF636366).copy(alpha = 0.8f)
                        } else {
                            Color(0xFFFFFFFF).copy(alpha = 0.9f)
                        }
                    )
                    .border(
                        width = 0.5.dp,
                        color = if (isDarkTheme) {
                            Color.White.copy(alpha = 0.08f)
                        } else {
                            Color.Black.copy(alpha = 0.1f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                suggestions.take(3).forEachIndexed { index, suggestion ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSuggestionClick(suggestion) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = suggestion,
                            color = if (isDarkTheme) Color.White else Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    if (index < suggestions.take(3).size - 1) {
                        Box(
                            modifier = Modifier
                                .width(0.5.dp)
                                .height(20.dp)
                                .background(
                                    color = if (isDarkTheme) {
                                        Color.White.copy(alpha = 0.15f)
                                    } else {
                                        Color.Black.copy(alpha = 0.12f)
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}
