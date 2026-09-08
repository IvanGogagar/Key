package com.ioskeyboard.ime

import android.view.View
import android.view.ViewParent
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

    private var composeView: ComposeView? = null
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModel by lazy { KeyboardViewModel() }

    override val lifecycle: Lifecycle get() = lifecycleRegistry

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED

        composeView?.disposeComposition()

        composeView = object : ComposeView(this) {
            override fun shouldCreateCompositionOnAttachedToWindow() = false
        }.apply {
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    var p: ViewParent? = v.parent
                    while (p is View) {
                        p.setViewTreeLifecycleOwner(this@ImeKeyboardService)
                        p = p.parent
                    }
                    (v as ComposeView).createComposition()
                }

                override fun onViewDetachedFromWindow(v: View) {}
            })
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

        val container = FrameLayout(this).apply {
            setViewTreeLifecycleOwner(this@ImeKeyboardService)
            addView(composeView)
        }

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        return container
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
        composeView?.disposeComposition()
        super.onDestroy()
    }
}
