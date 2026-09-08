package com.ioskeyboard.model

data class Key(
    val label: String,
    val code: Int,
    val isLongPress: Boolean = false,
    val longPressLabel: String? = null,
    val isSpecial: Boolean = false
)

data class KeyRow(
    val keys: List<Key>
)

data class KeyboardLayout(
    val rows: List<KeyRow>,
    val name: String
)

enum class LayoutType {
    ENGLISH,
    RUSSIAN,
    SYMBOLS,
    NUMBERS
}