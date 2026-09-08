package com.ioskeyboard.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SuggestionBar(
    suggestions: List<String>,
    colors: ColorScheme,
    onSuggestionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val visible = suggestions.isNotEmpty()

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surface)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            suggestions.forEach { suggestion ->
                Text(
                    text = suggestion,
                    color = colors.onSurface,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable { onSuggestionClick(suggestion) }
                )
            }
        }
    }
}
