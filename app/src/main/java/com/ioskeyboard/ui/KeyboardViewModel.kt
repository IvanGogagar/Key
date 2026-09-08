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

    private val _isShifted = MutableStateFlow(false)
    val isShifted: StateFlow<Boolean> = _isShifted.asStateFlow()

    private val _isCapsLock = MutableStateFlow(false)
    val isCapsLock: StateFlow<Boolean> = _isCapsLock.asStateFlow()

    private val _currentText = MutableStateFlow("")
    val currentText: StateFlow<String> = _currentText.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _keyboardHeight = MutableStateFlow(280f)
    val keyboardHeight: StateFlow<Float> = _keyboardHeight.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _action = MutableSharedFlow<KeyboardAction>()
    val action: SharedFlow<KeyboardAction> = _action.asSharedFlow()

    val keyboardLayout: KeyboardLayout
        get() = LayoutProvider.getLayout(_currentLayout.value)

    init {
        viewModelScope.launch {
            action.collect { action ->
                when (action) {
                    is KeyboardAction.TextInput -> commitText(action.text)
                    is KeyboardAction.Delete -> deleteLastChar(action.count)
                    is KeyboardAction.Enter -> commitText("\n")
                    is KeyboardAction.SwitchLayout -> switchLayout()
                    is KeyboardAction.ToggleShift -> toggleShift()
                    is KeyboardAction.ToggleCapsLock -> toggleCapsLock()
                    is KeyboardAction.Space -> addSpace()
                    is KeyboardAction.KeyPress -> handleKeyPress(action.keyCode, action.label)
                }
            }
        }
    }

    private fun handleKeyPress(keyCode: Int, label: String) {
        when (keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT -> toggleShift()
            KeyEvent.KEYCODE_DEL -> deleteLastChar()
            KeyEvent.KEYCODE_SPACE -> addSpace()
            KeyEvent.KEYCODE_ENTER -> commitText("\n")
            KeyEvent.KEYCODE_LANGUAGE_SWITCH -> switchLayout()
            else -> {
                val char = if (_isShifted.value) label.uppercase() else label
                commitText(char)
                if (_isShifted.value && !_isCapsLock.value) {
                    _isShifted.value = false
                }
                updateSuggestions(_currentText.value + char)
            }
        }
    }

    fun onKeyPress(keyCode: Int, label: String) {
        viewModelScope.launch {
            _action.emit(KeyboardAction.KeyPress(keyCode, label))
        }
    }

    fun onLongPress(keyCode: Int) {
        viewModelScope.launch {
            when (keyCode) {
                KeyEvent.KEYCODE_DEL -> {
                    _currentText.value = ""
                    _suggestions.value = emptyList()
                    _action.emit(KeyboardAction.Delete(100))
                }
            }
        }
    }

    private fun commitText(text: String) {
        _currentText.value += text
    }

    private fun addSpace() {
        _currentText.value += " "
        updateSuggestions(_currentText.value)
    }

    private fun deleteLastChar(count: Int = 1) {
        if (_currentText.value.isNotEmpty()) {
            _currentText.value = _currentText.value.dropLast(count)
            updateSuggestions(_currentText.value)
        }
    }

    private fun updateSuggestions(text: String) {
        _suggestions.value = generateSuggestions(text)
    }

    private fun generateSuggestions(text: String): List<String> {
        val words = text.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (words.isEmpty()) return listOf("", "", "")
        val lastWord = words.last()
        return listOf(
            lastWord + "1",
            lastWord + "2",
            lastWord + "3"
        )
    }

    fun setKeyboardHeight(height: Float) {
        _keyboardHeight.value = height
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    private fun switchLayout() {
        _currentLayout.value = when (_currentLayout.value) {
            LayoutType.ENGLISH -> LayoutType.RUSSIAN
            LayoutType.RUSSIAN -> LayoutType.SYMBOLS
            LayoutType.SYMBOLS -> LayoutType.ENGLISH
            LayoutType.NUMBERS -> LayoutType.ENGLISH
        }
    }

    private fun toggleShift() {
        _isShifted.value = !_isShifted.value
    }

    private fun toggleCapsLock() {
        _isCapsLock.value = !_isCapsLock.value
        if (_isCapsLock.value) {
            _isShifted.value = true
        }
    }
}
