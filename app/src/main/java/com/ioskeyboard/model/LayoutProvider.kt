package com.ioskeyboard.model

object LayoutProvider {

    fun getEnglishLayout(): KeyboardLayout {
        val rows = listOf(
            KeyRow(listOf(
                Key("q", 'q'.code), Key("w", 'w'.code), Key("e", 'e'.code),
                Key("r", 'r'.code), Key("t", 't'.code), Key("y", 'y'.code),
                Key("u", 'u'.code), Key("i", 'i'.code), Key("o", 'o'.code),
                Key("p", 'p'.code)
            )),
            KeyRow(listOf(
                Key("a", 'a'.code), Key("s", 's'.code), Key("d", 'd'.code),
                Key("f", 'f'.code), Key("g", 'g'.code), Key("h", 'h'.code),
                Key("j", 'j'.code), Key("k", 'k'.code), Key("l", 'l'.code)
            )),
            KeyRow(listOf(
                Key("⇧", KeyEvent.KEYCODE_SHIFT, type = KeyType.SHIFT, widthWeight = 1.3f),
                Key("z", 'z'.code), Key("x", 'x'.code), Key("c", 'c'.code),
                Key("v", 'v'.code), Key("b", 'b'.code), Key("n", 'n'.code),
                Key("m", 'm'.code),
                Key("⌫", KeyEvent.KEYCODE_BACKSPACE, type = KeyType.BACKSPACE, widthWeight = 1.3f)
            )),
            KeyRow(listOf(
                Key("123", KeyEvent.KEYCODE_NUMBERS, type = KeyType.NUMBERS_SWITCH, widthWeight = 1.2f),
                Key("🌐", KeyEvent.KEYCODE_GLOBE, type = KeyType.GLOBE, widthWeight = 1.0f),
                Key("", KeyEvent.KEYCODE_SPACE, type = KeyType.SPACE, widthWeight = 4.0f),
                Key("return", KeyEvent.KEYCODE_ENTER, type = KeyType.ENTER, widthWeight = 1.4f)
            ))
        )
        return KeyboardLayout(rows, "English")
    }

    fun getEnglishShiftedLayout(): KeyboardLayout {
        val rows = listOf(
            KeyRow(listOf(
                Key("Q", 'Q'.code), Key("W", 'W'.code), Key("E", 'E'.code),
                Key("R", 'R'.code), Key("T", 'T'.code), Key("Y", 'Y'.code),
                Key("U", 'U'.code), Key("I", 'I'.code), Key("O", 'O'.code),
                Key("P", 'P'.code)
            )),
            KeyRow(listOf(
                Key("A", 'A'.code), Key("S", 'S'.code), Key("D", 'D'.code),
                Key("F", 'F'.code), Key("G", 'G'.code), Key("H", 'H'.code),
                Key("J", 'J'.code), Key("K", 'K'.code), Key("L", 'L'.code)
            )),
            KeyRow(listOf(
                Key("⇧", KeyEvent.KEYCODE_SHIFT, type = KeyType.SHIFT, widthWeight = 1.3f),
                Key("Z", 'Z'.code), Key("X", 'X'.code), Key("C", 'C'.code),
                Key("V", 'V'.code), Key("B", 'B'.code), Key("N", 'N'.code),
                Key("M", 'M'.code),
                Key("⌫", KeyEvent.KEYCODE_BACKSPACE, type = KeyType.BACKSPACE, widthWeight = 1.3f)
            )),
            KeyRow(listOf(
                Key("123", KeyEvent.KEYCODE_NUMBERS, type = KeyType.NUMBERS_SWITCH, widthWeight = 1.2f),
                Key("🌐", KeyEvent.KEYCODE_GLOBE, type = KeyType.GLOBE, widthWeight = 1.0f),
                Key("", KeyEvent.KEYCODE_SPACE, type = KeyType.SPACE, widthWeight = 4.0f),
                Key("return", KeyEvent.KEYCODE_ENTER, type = KeyType.ENTER, widthWeight = 1.4f)
            ))
        )
        return KeyboardLayout(rows, "English")
    }

    fun getRussianLayout(): KeyboardLayout {
        val rows = listOf(
            KeyRow(listOf(
                Key("й", 'й'.code), Key("ц", 'ц'.code), Key("у", 'у'.code),
                Key("к", 'к'.code), Key("е", 'е'.code), Key("н", 'н'.code),
                Key("г", 'г'.code), Key("ш", 'ш'.code), Key("щ", 'щ'.code),
                Key("з", 'з'.code)
            )),
            KeyRow(listOf(
                Key("ф", 'ф'.code), Key("ы", 'ы'.code), Key("в", 'в'.code),
                Key("а", 'а'.code), Key("п", 'п'.code), Key("р", 'р'.code),
                Key("о", 'о'.code), Key("л", 'л'.code), Key("д", 'д'.code)
            )),
            KeyRow(listOf(
                Key("⇧", KeyEvent.KEYCODE_SHIFT, type = KeyType.SHIFT, widthWeight = 1.3f),
                Key("я", 'я'.code), Key("ч", 'ч'.code), Key("с", 'с'.code),
                Key("м", 'м'.code), Key("и", 'и'.code), Key("т", 'т'.code),
                Key("ь", 'ь'.code),
                Key("⌫", KeyEvent.KEYCODE_BACKSPACE, type = KeyType.BACKSPACE, widthWeight = 1.3f)
            )),
            KeyRow(listOf(
                Key("123", KeyEvent.KEYCODE_NUMBERS, type = KeyType.NUMBERS_SWITCH, widthWeight = 1.2f),
                Key("🌐", KeyEvent.KEYCODE_GLOBE, type = KeyType.GLOBE, widthWeight = 1.0f),
                Key("", KeyEvent.KEYCODE_SPACE, type = KeyType.SPACE, widthWeight = 4.0f),
                Key("return", KeyEvent.KEYCODE_ENTER, type = KeyType.ENTER, widthWeight = 1.4f)
            ))
        )
        return KeyboardLayout(rows, "Russian")
    }

    fun getRussianShiftedLayout(): KeyboardLayout {
        val rows = listOf(
            KeyRow(listOf(
                Key("Й", 'Й'.code), Key("Ц", 'Ц'.code), Key("У", 'У'.code),
                Key("К", 'К'.code), Key("Е", 'Е'.code), Key("Н", 'Н'.code),
                Key("Г", 'Г'.code), Key("Ш", 'Ш'.code), Key("Щ", 'Щ'.code),
                Key("З", 'З'.code)
            )),
            KeyRow(listOf(
                Key("Ф", 'Ф'.code), Key("Ы", 'Ы'.code), Key("В", 'В'.code),
                Key("А", 'А'.code), Key("П", 'П'.code), Key("Р", 'Р'.code),
                Key("О", 'О'.code), Key("Л", 'Л'.code), Key("Д", 'Д'.code)
            )),
            KeyRow(listOf(
                Key("⇧", KeyEvent.KEYCODE_SHIFT, type = KeyType.SHIFT, widthWeight = 1.3f),
                Key("Я", 'Я'.code), Key("Ч", 'Ч'.code), Key("С", 'С'.code),
                Key("М", 'М'.code), Key("И", 'И'.code), Key("Т", 'Т'.code),
                Key("Ь", 'Ь'.code),
                Key("⌫", KeyEvent.KEYCODE_BACKSPACE, type = KeyType.BACKSPACE, widthWeight = 1.3f)
            )),
            KeyRow(listOf(
                Key("123", KeyEvent.KEYCODE_NUMBERS, type = KeyType.NUMBERS_SWITCH, widthWeight = 1.2f),
                Key("🌐", KeyEvent.KEYCODE_GLOBE, type = KeyType.GLOBE, widthWeight = 1.0f),
                Key("", KeyEvent.KEYCODE_SPACE, type = KeyType.SPACE, widthWeight = 4.0f),
                Key("return", KeyEvent.KEYCODE_ENTER, type = KeyType.ENTER, widthWeight = 1.4f)
            ))
        )
        return KeyboardLayout(rows, "Russian")
    }

    fun getSymbolsLayout(): KeyboardLayout {
        val rows = listOf(
            KeyRow(listOf(
                Key("[", '['.code), Key("]", ']'.code), Key("{", '{'.code),
                Key("}", '}'.code), Key("#", '#'.code), Key("%", '%'.code),
                Key("^", '^'.code), Key("*", '*'.code), Key("+", '+'.code),
                Key("=", '='.code)
            )),
            KeyRow(listOf(
                Key("_", '_'.code), Key("\\", '\\'.code), Key("|", '|'.code),
                Key("~", '~'.code), Key("<", '<'.code), Key(">", '>'.code),
                Key("$", '$'.code), Key("€", '€'.code), Key("£", '£'.code)
            )),
            KeyRow(listOf(
                Key("⇧", KeyEvent.KEYCODE_SHIFT, type = KeyType.SHIFT, widthWeight = 1.3f),
                Key(".", '.'.code), Key(",", ','.code), Key("?", '?'.code),
                Key("!", '!'.code), Key("'", '\''.code), Key("\"", '"'.code),
                Key(":", ':'.code), Key(";", ';'.code),
                Key("⌫", KeyEvent.KEYCODE_BACKSPACE, type = KeyType.BACKSPACE, widthWeight = 1.3f)
            )),
            KeyRow(listOf(
                Key("123", KeyEvent.KEYCODE_NUMBERS, type = KeyType.NUMBERS_SWITCH, widthWeight = 1.2f),
                Key("🌐", KeyEvent.KEYCODE_GLOBE, type = KeyType.GLOBE, widthWeight = 1.0f),
                Key("", KeyEvent.KEYCODE_SPACE, type = KeyType.SPACE, widthWeight = 4.0f),
                Key("return", KeyEvent.KEYCODE_ENTER, type = KeyType.ENTER, widthWeight = 1.4f)
            ))
        )
        return KeyboardLayout(rows, "Symbols")
    }

    fun getNumbersLayout(): KeyboardLayout {
        val rows = listOf(
            KeyRow(listOf(
                Key("1", '1'.code), Key("2", '2'.code), Key("3", '3'.code),
                Key("4", '4'.code), Key("5", '5'.code), Key("6", '6'.code),
                Key("7", '7'.code), Key("8", '8'.code), Key("9", '9'.code),
                Key("0", '0'.code)
            )),
            KeyRow(listOf(
                Key("-", '-'.code), Key("/", '/'.code), Key(":", ':'.code),
                Key(";", ';'.code), Key("(", '('.code), Key(")", ')'.code),
                Key("$", '$'.code), Key("@", '@'.code)
            )),
            KeyRow(listOf(
                Key("⇧", KeyEvent.KEYCODE_SHIFT, type = KeyType.SHIFT, widthWeight = 1.3f),
                Key(".", '.'.code), Key(",", ','.code), Key("?", '?'.code),
                Key("!", '!'.code), Key("'", '\''.code), Key("\"", '"'.code),
                Key("#", '#'.code), Key("%", '%'.code),
                Key("⌫", KeyEvent.KEYCODE_BACKSPACE, type = KeyType.BACKSPACE, widthWeight = 1.3f)
            )),
            KeyRow(listOf(
                Key("#+=", KeyEvent.KEYCODE_NUMBERS, type = KeyType.ABC_SWITCH, widthWeight = 1.2f),
                Key("🌐", KeyEvent.KEYCODE_GLOBE, type = KeyType.GLOBE, widthWeight = 1.0f),
                Key("", KeyEvent.KEYCODE_SPACE, type = KeyType.SPACE, widthWeight = 4.0f),
                Key("return", KeyEvent.KEYCODE_ENTER, type = KeyType.ENTER, widthWeight = 1.4f)
            ))
        )
        return KeyboardLayout(rows, "Numbers")
    }

    fun getLayout(type: LayoutType): KeyboardLayout = when (type) {
        LayoutType.ENGLISH -> getEnglishLayout()
        LayoutType.ENGLISH_SHIFTED -> getEnglishShiftedLayout()
        LayoutType.RUSSIAN -> getRussianLayout()
        LayoutType.RUSSIAN_SHIFTED -> getRussianShiftedLayout()
        LayoutType.SYMBOLS -> getSymbolsLayout()
        LayoutType.NUMBERS -> getNumbersLayout()
    }
}
