package com.ioskeyboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ioskeyboard.model.KeyboardAction
import com.ioskeyboard.model.KeyboardLayout
import com.ioskeyboard.model.KeyEvent
import com.ioskeyboard.model.LayoutProvider
import com.ioskeyboard.model.LayoutType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeyboardViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _currentLayout = MutableStateFlow(LayoutType.ENGLISH)
    val currentLayout: StateFlow<LayoutType> = _currentLayout.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _action = MutableSharedFlow<KeyboardAction>(
        extraBufferCapacity = 16
    )
    val action: SharedFlow<KeyboardAction> = _action.asSharedFlow()

    private var composedText = ""
    private var wordStartIndex = 0

    val keyboardLayout: KeyboardLayout
        get() = LayoutProvider.getLayout(_currentLayout.value)

    fun onKeyPress(keyCode: Int, label: String) {
        viewModelScope.launch(ioDispatcher) {
            when (keyCode) {
                KeyEvent.KEYCODE_SHIFT -> toggleShift()
                KeyEvent.KEYCODE_BACKSPACE -> deleteChar()
                KeyEvent.KEYCODE_SPACE -> insertSpace()
                KeyEvent.KEYCODE_ENTER -> insertNewline()
                KeyEvent.KEYCODE_GLOBE -> cycleLanguage()
                KeyEvent.KEYCODE_NUMBERS -> _currentLayout.value = LayoutType.NUMBERS
                KeyEvent.KEYCODE_ABC -> _currentLayout.value = LayoutType.ENGLISH
                else -> insertChar(label)
            }
        }
    }

    fun onLongPress(keyCode: Int) {
        viewModelScope.launch(ioDispatcher) {
            if (keyCode == KeyEvent.KEYCODE_BACKSPACE) {
                val count = composedText.length
                if (count > 0) {
                    composedText = ""
                    wordStartIndex = 0
                    _suggestions.value = emptyList()
                    _action.emit(KeyboardAction.Delete(count))
                }
            }
        }
    }

    fun onSuggestionSelected(suggestion: String) {
        viewModelScope.launch(ioDispatcher) {
            if (composedText.isNotEmpty() && wordStartIndex < composedText.length) {
                val prefix = composedText.substring(0, wordStartIndex)
                val deleteCount = composedText.length - wordStartIndex
                composedText = prefix + suggestion + " "
                wordStartIndex = composedText.length
                _suggestions.value = emptyList()
                _action.emit(KeyboardAction.Delete(deleteCount))
                _action.emit(KeyboardAction.TextInput(suggestion + " "))
            } else {
                composedText += suggestion + " "
                wordStartIndex = composedText.length
                _suggestions.value = emptyList()
                _action.emit(KeyboardAction.TextInput(suggestion + " "))
            }
        }
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    private suspend fun insertChar(label: String) {
        composedText += label
        updateWord()
        _action.emit(KeyboardAction.TextInput(label))
    }

    private suspend fun deleteChar() {
        if (composedText.isNotEmpty()) {
            composedText = composedText.dropLast(1)
            updateWord()
            _action.emit(KeyboardAction.Delete(1))
        }
    }

    private suspend fun insertSpace() {
        composedText += " "
        wordStartIndex = composedText.length
        _suggestions.value = emptyList()
        _action.emit(KeyboardAction.Space)
    }

    private suspend fun insertNewline() {
        composedText += "\n"
        wordStartIndex = composedText.length
        _suggestions.value = emptyList()
        _action.emit(KeyboardAction.Enter)
    }

    private fun updateWord() {
        if (composedText.isEmpty()) {
            wordStartIndex = 0
            _suggestions.value = emptyList()
            return
        }
        var start = composedText.length - 1
        while (start > 0 && composedText[start - 1] != ' ' && composedText[start - 1] != '\n') {
            start--
        }
        wordStartIndex = start
        val word = composedText.substring(start)
        _suggestions.value = if (word.length >= 2) {
            listOfNotNull(
                (word + "ing").takeIf { it != word },
                (word + "ed").takeIf { it != word },
                (word + "s").takeIf { it != word }
            )
        } else {
            emptyList()
        }
    }

    private fun toggleShift() {
        _currentLayout.update { current ->
            when (current) {
                LayoutType.ENGLISH -> LayoutType.ENGLISH_SHIFTED
                LayoutType.ENGLISH_SHIFTED -> LayoutType.ENGLISH
                LayoutType.RUSSIAN -> LayoutType.RUSSIAN_SHIFTED
                LayoutType.RUSSIAN_SHIFTED -> LayoutType.RUSSIAN
                else -> current
            }
        }
    }

    private fun cycleLanguage() {
        _currentLayout.update { current ->
            when (current) {
                LayoutType.ENGLISH, LayoutType.ENGLISH_SHIFTED -> LayoutType.RUSSIAN
                LayoutType.RUSSIAN, LayoutType.RUSSIAN_SHIFTED -> LayoutType.SYMBOLS
                else -> LayoutType.ENGLISH
            }
        }
    }
}
