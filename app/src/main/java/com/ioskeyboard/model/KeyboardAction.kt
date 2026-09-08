package com.ioskeyboard.model

sealed class KeyboardAction {
    data class TextInput(val text: String) : KeyboardAction()
    data class Delete(val count: Int = 1) : KeyboardAction()
    object Enter : KeyboardAction()
    object Space : KeyboardAction()
}
