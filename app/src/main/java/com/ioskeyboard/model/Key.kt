package com.ioskeyboard.model

import androidx.compose.runtime.Immutable

@Immutable
enum class KeyType {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    SPACE,
    ENTER,
    GLOBE,
    NUMBERS_SWITCH,
    ABC_SWITCH,
    SPECIAL
}

@Immutable
data class Key(
    val label: String,
    val code: Int,
    val type: KeyType = KeyType.CHARACTER,
    val widthWeight: Float = 1f
)

@Immutable
data class KeyRow(
    val keys: List<Key>
)

@Immutable
data class KeyboardLayout(
    val rows: List<KeyRow>,
    val name: String
)

@Immutable
enum class LayoutType {
    ENGLISH,
    ENGLISH_SHIFTED,
    RUSSIAN,
    RUSSIAN_SHIFTED,
    SYMBOLS,
    NUMBERS
}
