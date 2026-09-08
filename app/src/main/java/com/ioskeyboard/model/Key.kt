package com.ioskeyboard.model

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

data class Key(
    val label: String,
    val code: Int,
    val type: KeyType = KeyType.CHARACTER,
    val widthWeight: Float = 1f
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
    ENGLISH_SHIFTED,
    RUSSIAN,
    RUSSIAN_SHIFTED,
    SYMBOLS,
    NUMBERS
}
