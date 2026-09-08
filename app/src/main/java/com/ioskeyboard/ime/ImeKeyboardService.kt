package com.ioskeyboard.ime

import android.view.View
import android.view.inputmethod.EditorInfo
import android.inputmethodservice.InputMethodService
import android.widget.FrameLayout
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.ioskeyboard.model.KeyboardAction
import com.ioskeyboard.ui.KeyboardScreen
import com.ioskeyboard.ui.KeyboardViewModel
import com.ioskeyboard.ui.theme.IOSStyleKeyboardTheme
import kotlinx.coroutines.flow.collectLatest

class ImeKeyboardService : InputMethodService(), LifecycleOwner {

    private var container: FrameLayout? = null
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModel by lazy { KeyboardViewModel() }

    override val lifecycle: Lifecycle get() = lifecycleRegistry

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED

        container = FrameLayout(this).apply {
            setViewTreeLifecycleOwner(this@ImeKeyboardService)
        }

        val composeView = ComposeView(this).apply {
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

        container!!.addView(composeView)
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        return container!!
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
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        container?.let {
            for (i in 0 until it.childCount) {
                val child = it.getChildAt(i)
                if (child is ComposeView) child.disposeComposition()
            }
        }
        super.onDestroy()
    }
}
