package com.ioskeyboard.ime

import android.view.View
import android.view.ViewParent
import android.view.inputmethod.EditorInfo
import android.inputmethodservice.InputMethodService
import android.widget.FrameLayout
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ioskeyboard.model.KeyboardAction
import com.ioskeyboard.ui.KeyboardScreen
import com.ioskeyboard.ui.KeyboardViewModel
import com.ioskeyboard.ui.theme.IOSStyleKeyboardTheme
import kotlinx.coroutines.flow.collectLatest

class ImeKeyboardService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner {

    private var composeView: ComposeView? = null
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateController by lazy { SavedStateRegistryController.create(this) }
    private val viewModel by lazy { KeyboardViewModel() }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED

        composeView?.disposeComposition()

        composeView = ComposeView(this).apply {
            setContent {
                IOSStyleKeyboardTheme(darkTheme = viewModel.isDarkTheme.collectAsState().value) {
                    LaunchedEffect(Unit) {
                        viewModel.action.collectLatest { action ->
                            val connection = currentInputConnection ?: return@collectLatest
                            when (action) {
                                is KeyboardAction.TextInput -> connection.commitText(action.text, 1)
                                is KeyboardAction.Delete -> connection.deleteSurroundingText(action.count, 0)
                                is KeyboardAction.Enter -> connection.commitText("\n", 1)
                                is KeyboardAction.Space -> connection.commitText(" ", 1)
                                else -> {}
                            }
                        }
                    }
                    KeyboardScreen(viewModel = viewModel)
                }
            }
        }

        val container = object : FrameLayout(this) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                var p: ViewParent? = parent
                while (p is View) {
                    p.setViewTreeLifecycleOwner(this@ImeKeyboardService)
                    p.setViewTreeSavedStateRegistryOwner(this@ImeKeyboardService)
                    p = p.parent
                }
            }
        }.apply {
            setViewTreeLifecycleOwner(this@ImeKeyboardService)
            setViewTreeSavedStateRegistryOwner(this@ImeKeyboardService)
            addView(composeView)
        }

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        return container
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
