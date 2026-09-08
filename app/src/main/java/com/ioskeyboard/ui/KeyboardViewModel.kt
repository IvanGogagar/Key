package com.ioskeyboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ioskeyboard.model.KeyboardAction
import com.ioskeyboard.model.KeyboardLayout
import com.ioskeyboard.model.KeyEvent
import com.ioskeyboard.model.LayoutProvider
import com.ioskeyboard.model.LayoutType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KeyboardViewModel : ViewModel() {

    private val _currentLayout = MutableStateFlow(LayoutType.ENGLISH)
    val currentLayout: StateFlow<LayoutType> = _currentLayout.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _currentText = MutableStateFlow("")
    val currentText: StateFlow<String> = _currentText.asStateFlow()

    private val _action = MutableSharedFlow<KeyboardAction>()
    val action: SharedFlow<KeyboardAction> = _action.asSharedFlow()

    private var lastWordStartIndex: Int = 0

    val keyboardLayout: KeyboardLayout
        get() = LayoutProvider.getLayout(_currentLayout.value)

    fun onKeyPress(keyCode: Int, label: String) {
        viewModelScope.launch {
            when (keyCode) {
                KeyEvent.KEYCODE_SHIFT -> handleShift()
                KeyEvent.KEYCODE_BACKSPACE -> {
                    handleBackspace()
                }
                KeyEvent.KEYCODE_SPACE -> {
                    _currentText.value += " "
                    lastWordStartIndex = _currentText.value.length
                    _suggestions.value = emptyList()
                    _action.emit(KeyboardAction.Space)
                }
                KeyEvent.KEYCODE_ENTER -> {
                    _currentText.value += "\n"
                    lastWordStartIndex = _currentText.value.length
                    _suggestions.value = emptyList()
                    _action.emit(KeyboardAction.Enter)
                }
                KeyEvent.KEYCODE_GLOBE -> switchLanguage()
                KeyEvent.KEYCODE_NUMBERS -> switchToNumbers()
                KeyEvent.KEYCODE_ABC -> switchToABC()
                else -> {
                    handleCharacterInput(label)
                }
            }
        }
    }

    fun onLongPress(keyCode: Int) {
        viewModelScope.launch {
            when (keyCode) {
                KeyEvent.KEYCODE_BACKSPACE -> {
                    val count = _currentText.value.length
                    _currentText.value = ""
                    lastWordStartIndex = 0
                    _suggestions.value = emptyList()
                    if (count > 0) {
                        _action.emit(KeyboardAction.Delete(count))
                    }
                }
            }
        }
    }

    fun onSuggestionSelected(suggestion: String) {
        viewModelScope.launch {
            if (_currentText.value.isNotEmpty() && lastWordStartIndex < _currentText.value.length) {
                val beforeWord = _currentText.value.substring(0, lastWordStartIndex)
                val deleteCount = _currentText.value.length - lastWordStartIndex
                _currentText.value = beforeWord + suggestion + " "
                lastWordStartIndex = _currentText.value.length
                _action.emit(KeyboardAction.Delete(deleteCount))
                _action.emit(KeyboardAction.TextInput(suggestion + " "))
            } else {
                _currentText.value += suggestion + " "
                lastWordStartIndex = _currentText.value.length
                _action.emit(KeyboardAction.TextInput(suggestion + " "))
            }
            _suggestions.value = emptyList()
        }
    }

    private suspend fun handleCharacterInput(label: String) {
        _currentText.value += label
        updateCurrentWord()
        _action.emit(KeyboardAction.TextInput(label))
    }

    private suspend fun handleBackspace() {
        if (_currentText.value.isNotEmpty()) {
            _currentText.value = _currentText.value.dropLast(1)
            updateCurrentWord()
            _action.emit(KeyboardAction.Delete(1))
        }
    }

    private fun updateCurrentWord() {
        val text = _currentText.value
        if (text.isEmpty()) {
            _suggestions.value = emptyList()
            lastWordStartIndex = 0
            return
        }

        var wordStart = text.length - 1
        while (wordStart > 0 && text[wordStart - 1] != ' ' && text[wordStart - 1] != '\n') {
            wordStart--
        }
        lastWordStartIndex = wordStart

        val currentWord = text.substring(wordStart)
        if (currentWord.isEmpty()) {
            _suggestions.value = emptyList()
        } else {
            _suggestions.value = generateSuggestions(currentWord)
        }
    }

    private fun generateSuggestions(word: String): List<String> {
        if (word.length < 2) return emptyList()
        return listOf(
            word + "ing",
            word + "ed",
            word + "s"
        ).filter { it != word }
    }

    private fun handleShift() {
        val current = _currentLayout.value
        val newLayout = when (current) {
            LayoutType.ENGLISH -> LayoutType.ENGLISH_SHIFTED
            LayoutType.ENGLISH_SHIFTED -> LayoutType.ENGLISH
            LayoutType.RUSSIAN -> LayoutType.RUSSIAN_SHIFTED
            LayoutType.RUSSIAN_SHIFTED -> LayoutType.RUSSIAN
            else -> current
        }
        _currentLayout.value = newLayout
    }

    private fun switchLanguage() {
        val current = _currentLayout.value
        _currentLayout.value = when (current) {
            LayoutType.ENGLISH, LayoutType.ENGLISH_SHIFTED -> LayoutType.RUSSIAN
            LayoutType.RUSSIAN, LayoutType.RUSSIAN_SHIFTED -> LayoutType.SYMBOLS
            LayoutType.SYMBOLS -> LayoutType.ENGLISH
            LayoutType.NUMBERS -> LayoutType.ENGLISH
        }
    }

    private fun switchToNumbers() {
        _currentLayout.value = LayoutType.NUMBERS
    }

    private fun switchToABC() {
        _currentLayout.value = LayoutType.ENGLISH
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}
