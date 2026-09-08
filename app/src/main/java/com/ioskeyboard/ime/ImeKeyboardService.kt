package com.ioskeyboard.ime

import android.view.View
import android.view.inputmethod.EditorInfo
import android.inputmethodservice.InputMethodService
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import com.ioskeyboard.model.KeyboardAction
import com.ioskeyboard.ui.KeyboardScreen
import com.ioskeyboard.ui.KeyboardViewModel
import com.ioskeyboard.ui.theme.IOSStyleKeyboardTheme
import kotlinx.coroutines.flow.collectLatest

class ImeKeyboardService : InputMethodService() {

    private var composeView: ComposeView? = null
    private val viewModel by lazy { KeyboardViewModel() }

    override fun onCreateInputView(): View {
        composeView?.disposeComposition()

        composeView = ComposeView(this).apply {
            setContent {
                IOSStyleKeyboardTheme {
                    LaunchedEffect(Unit) {
                        viewModel.action.collectLatest { action ->
                            val connection = currentInputConnection ?: return@collectLatest
                            when (action) {
                                is KeyboardAction.KeyPress -> sendKeyEvent(action.keyCode, action.label)
                                is KeyboardAction.Delete -> connection.deleteSurroundingText(action.count, 0)
                                is KeyboardAction.Enter -> connection.commitText("\n", 1)
                                is KeyboardAction.Space -> connection.commitText(" ", 1)
                                is KeyboardAction.TextInput -> connection.commitText(action.text, 1)
                                else -> {}
                            }
                        }
                    }
                    KeyboardScreen(viewModel = viewModel)
                }
            }
        }
        return composeView!!
    }

    private fun sendKeyEvent(keyCode: Int, label: String) {
        val connection = currentInputConnection ?: return
        when (keyCode) {
            67 -> connection.deleteSurroundingText(1, 0)
            32 -> connection.commitText(" ", 1)
            10 -> connection.commitText("\n", 1)
            else -> connection.commitText(label, 1)
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
    }

    override fun onDestroy() {
        composeView?.disposeComposition()
        super.onDestroy()
    }
}
