package com.ioskeyboard.model

object LayoutProvider {
    fun getEnglishLayout(): KeyboardLayout {
        val rows = listOf(
            KeyRow(listOf(Key("q", KeyEvent.KEYCODE_Q), Key("w", KeyEvent.KEYCODE_W), Key("e", KeyEvent.KEYCODE_E), Key("r", KeyEvent.KEYCODE_R), Key("t", KeyEvent.KEYCODE_T), Key("y", KeyEvent.KEYCODE_Y), Key("u", KeyEvent.KEYCODE_U), Key("i", KeyEvent.KEYCODE_I), Key("o", KeyEvent.KEYCODE_O), Key("p", KeyEvent.KEYCODE_P))),
            KeyRow(listOf(Key("a", KeyEvent.KEYCODE_A), Key("s", KeyEvent.KEYCODE_S), Key("d", KeyEvent.KEYCODE_D), Key("f", KeyEvent.KEYCODE_F), Key("g", KeyEvent.KEYCODE_G), Key("h", KeyEvent.KEYCODE_H), Key("j", KeyEvent.KEYCODE_J), Key("k", KeyEvent.KEYCODE_K), Key("l", KeyEvent.KEYCODE_L))),
            KeyRow(listOf(Key("⇧", KeyEvent.KEYCODE_SHIFT_LEFT, isSpecial = true), Key("z", KeyEvent.KEYCODE_Z), Key("x", KeyEvent.KEYCODE_X), Key("c", KeyEvent.KEYCODE_C), Key("v", KeyEvent.KEYCODE_V), Key("b", KeyEvent.KEYCODE_B), Key("n", KeyEvent.KEYCODE_N), Key("m", KeyEvent.KEYCODE_M), Key("⌫", KeyEvent.KEYCODE_DEL, isSpecial = true))),
            KeyRow(listOf(Key("123", KeyEvent.KEYCODE_UNKNOWN, isSpecial = true), Key("🌐", KeyEvent.KEYCODE_LANGUAGE_SWITCH, isSpecial = true), Key("␣", KeyEvent.KEYCODE_SPACE, isSpecial = true), Key("⏎", KeyEvent.KEYCODE_ENTER, isSpecial = true)))
        )
        return KeyboardLayout(rows, "English")
    }

    fun getRussianLayout(): KeyboardLayout {
        val rows = listOf(
            KeyRow(listOf(Key("й", KeyEvent.KEYCODE_Q), Key("ц", KeyEvent.KEYCODE_W), Key("у", KeyEvent.KEYCODE_E), Key("к", KeyEvent.KEYCODE_R), Key("е", KeyEvent.KEYCODE_T), Key("н", KeyEvent.KEYCODE_Y), Key("г", KeyEvent.KEYCODE_U), Key("ш", KeyEvent.KEYCODE_I), Key("щ", KeyEvent.KEYCODE_O), Key("з", KeyEvent.KEYCODE_P))),
            KeyRow(listOf(Key("ф", KeyEvent.KEYCODE_A), Key("ы", KeyEvent.KEYCODE_S), Key("в", KeyEvent.KEYCODE_D), Key("а", KeyEvent.KEYCODE_F), Key("п", KeyEvent.KEYCODE_G), Key("р", KeyEvent.KEYCODE_H), Key("о", KeyEvent.KEYCODE_J), Key("л", KeyEvent.KEYCODE_K), Key("д", KeyEvent.KEYCODE_L))),
            KeyRow(listOf(Key("⇧", KeyEvent.KEYCODE_SHIFT_LEFT, isSpecial = true), Key("я", KeyEvent.KEYCODE_Z), Key("ч", KeyEvent.KEYCODE_X), Key("с", KeyEvent.KEYCODE_C), Key("м", KeyEvent.KEYCODE_V), Key("и", KeyEvent.KEYCODE_B), Key("т", KeyEvent.KEYCODE_N), Key("ь", KeyEvent.KEYCODE_M), Key("⌫", KeyEvent.KEYCODE_DEL, isSpecial = true))),
            KeyRow(listOf(Key("123", KeyEvent.KEYCODE_UNKNOWN, isSpecial = true), Key("🌐", KeyEvent.KEYCODE_LANGUAGE_SWITCH, isSpecial = true), Key("␣", KeyEvent.KEYCODE_SPACE, isSpecial = true), Key("⏎", KeyEvent.KEYCODE_ENTER, isSpecial = true)))
        )
        return KeyboardLayout(rows, "Russian")
    }

    fun getSymbolsLayout(): KeyboardLayout {
        val rows = listOf(
            KeyRow(listOf(Key("1", KeyEvent.KEYCODE_1), Key("2", KeyEvent.KEYCODE_2), Key("3", KeyEvent.KEYCODE_3), Key("4", KeyEvent.KEYCODE_4), Key("5", KeyEvent.KEYCODE_5), Key("6", KeyEvent.KEYCODE_6), Key("7", KeyEvent.KEYCODE_7), Key("8", KeyEvent.KEYCODE_8), Key("9", KeyEvent.KEYCODE_9), Key("0", KeyEvent.KEYCODE_0))),
            KeyRow(listOf(Key("-", KeyEvent.KEYCODE_MINUS), Key("/", KeyEvent.KEYCODE_SLASH), Key(":", KeyEvent.KEYCODE_SEMICOLON), Key(";", KeyEvent.KEYCODE_S), Key("(", KeyEvent.KEYCODE_LEFT_BRACKET), Key(")", KeyEvent.KEYCODE_RIGHT_BRACKET), Key("@", KeyEvent.KEYCODE_AT), Key("\"", KeyEvent.KEYCODE_APOSTROPHE), Key("#", KeyEvent.KEYCODE_POUND))),
            KeyRow(listOf(Key("⇧", KeyEvent.KEYCODE_SHIFT_LEFT, isSpecial = true), Key(".", KeyEvent.KEYCODE_PERIOD), Key("?", KeyEvent.KEYCODE_COMMA), Key("!", KeyEvent.KEYCODE_EXCLAMATION), Key("'", KeyEvent.KEYCODE_APOSTROPHE), Key("&", KeyEvent.KEYCODE_AMPERSAND), Key("+", KeyEvent.KEYCODE_PLUS), Key("⌫", KeyEvent.KEYCODE_DEL, isSpecial = true))),
            KeyRow(listOf(Key("ABC", KeyEvent.KEYCODE_UNKNOWN, isSpecial = true), Key("🌐", KeyEvent.KEYCODE_LANGUAGE_SWITCH, isSpecial = true), Key("␣", KeyEvent.KEYCODE_SPACE, isSpecial = true), Key("⏎", KeyEvent.KEYCODE_ENTER, isSpecial = true)))
        )
        return KeyboardLayout(rows, "Symbols")
    }

    fun getLayout(type: LayoutType): KeyboardLayout = when (type) {
        LayoutType.ENGLISH -> getEnglishLayout()
        LayoutType.RUSSIAN -> getRussianLayout()
        LayoutType.SYMBOLS -> getSymbolsLayout()
        LayoutType.NUMBERS -> getSymbolsLayout()
    }
}