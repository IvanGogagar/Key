package com.ioskeyboard.ime

import android.view.View
import android.view.inputmethod.EditorInfo
import android.inputmethodservice.InputMethodService
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
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

class ImeKeyboardService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private var composeView: ComposeView? = null

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private val viewModel by lazy {
        ViewModelProvider(this)[KeyboardViewModel::class.java]
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val viewModelStore: ViewModelStore
        get() = store

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        composeView?.disposeComposition()

        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(this@ImeKeyboardService)
            setViewTreeSavedStateRegistryOwner(this@ImeKeyboardService)
            setContent {
                IOSStyleKeyboardTheme {
                    LaunchedEffect(viewModel) {
                        viewModel.action.collectLatest { action ->
                            val connection = currentInputConnection ?: return@collectLatest
                            when (action) {
                                is KeyboardAction.KeyPress -> {
                                    sendKeyEvent(action.keyCode, action.label)
                                }
                                is KeyboardAction.Delete -> {
                                    connection.deleteSurroundingText(action.count, 0)
                                }
                                is KeyboardAction.Enter -> {
                                    connection.commitText("\n", 1)
                                }
                                is KeyboardAction.Space -> {
                                    connection.commitText(" ", 1)
                                }
                                is KeyboardAction.SwitchLayout -> {}
                                is KeyboardAction.ToggleShift -> {}
                                is KeyboardAction.ToggleCapsLock -> {}
                                is KeyboardAction.TextInput -> {
                                    connection.commitText(action.text, 1)
                                }
                            }
                        }
                    }

                    KeyboardScreen(viewModel = viewModel)
                }
            }
        }

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        return composeView!!
    }

    private fun sendKeyEvent(keyCode: Int, label: String) {
        val connection = currentInputConnection ?: return
        when (keyCode) {
            59 -> {}
            67 -> connection.deleteSurroundingText(1, 0)
            32 -> connection.commitText(" ", 1)
            10 -> connection.commitText("\n", 1)
            1000 -> {}
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
        store.clear()
        super.onDestroy()
    }
}
