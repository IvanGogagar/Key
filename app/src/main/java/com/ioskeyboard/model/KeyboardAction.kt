package com.ioskeyboard.model

sealed class KeyboardAction {
    data class TextInput(val text: String) : KeyboardAction()
    data class KeyPress(val keyCode: Int, val label: String) : KeyboardAction()
    data class Delete(val count: Int = 1) : KeyboardAction()
    object Enter : KeyboardAction()
    object SwitchLayout : KeyboardAction()
    object ToggleShift : KeyboardAction()
    object ToggleCapsLock : KeyboardAction()
    object Space : KeyboardAction()
}