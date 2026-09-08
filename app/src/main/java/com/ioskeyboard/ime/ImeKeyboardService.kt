package com.ioskeyboard.ime

import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodService
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ioskeyboard.model.KeyboardAction
import com.ioskeyboard.ui.KeyboardScreen
import com.ioskeyboard.ui.KeyboardViewModel
import com.ioskeyboard.ui.theme.IOSStyleKeyboardTheme
import kotlinx.coroutines.flow.collectLatest

class ImeKeyboardService : InputMethodService() {
    private var composeView: ComposeView? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onCreateInputView(): View {
        val service = this
        composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                IOSStyleKeyboardTheme {
                    val viewModel: KeyboardViewModel = viewModel()
                    val lifecycleOwner = LocalLifecycleOwner.current
                    val currentInputConnection = service.currentInputConnection

                    LaunchedEffect(lifecycleOwner, viewModel) {
                        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.action.collectLatest { action ->
                                when (action) {
                                    is KeyboardAction.KeyPress -> {
                                        sendKeyEvent(action.keyCode, action.label)
                                    }
                                    is KeyboardAction.Delete -> {
                                        if (currentInputConnection != null) {
                                            currentInputConnection.deleteSurroundingText(action.count, 0)
                                        }
                                    }
                                    is KeyboardAction.Enter -> {
                                        if (currentInputConnection != null) {
                                            currentInputConnection.commitText("\n", 1)
                                        }
                                    }
                                    is KeyboardAction.Space -> {
                                        if (currentInputConnection != null) {
                                            currentInputConnection.commitText(" ", 1)
                                        }
                                    }
                                    is KeyboardAction.SwitchLayout -> {
                                    }
                                    is KeyboardAction.ToggleShift -> {
                                    }
                                    is KeyboardAction.ToggleCapsLock -> {
                                    }
                                    is KeyboardAction.TextInput -> {
                                        if (currentInputConnection != null) {
                                            currentInputConnection.commitText(action.text, 1)
                                        }
                                    }
                                }
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
            59 -> {
            }
            67 -> {
                connection.deleteSurroundingText(1, 0)
            }
            32 -> {
                connection.commitText(" ", 1)
            }
            10 -> {
                connection.commitText("\n", 1)
            }
            1000 -> {
            }
            else -> {
                connection.commitText(label, 1)
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        composeView?.disposeComposition()
    }

    override fun onDestroy() {
        super.onDestroy()
        composeView?.disposeComposition()
    }
}
