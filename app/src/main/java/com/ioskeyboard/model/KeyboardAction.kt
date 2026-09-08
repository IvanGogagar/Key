package com.ioskeyboard.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class KeyboardAction {
    @Immutable
    data class TextInput(val text: String) : KeyboardAction()
    @Immutable
    data class Delete(val count: Int = 1) : KeyboardAction()
    @Immutable
    data object Enter : KeyboardAction()
    @Immutable
    data object Space : KeyboardAction()
}
