package com.example.twinshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneLayout
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneMode
import com.microsoft.device.dualscreen.windowstate.WindowState


@Composable
fun MainPage(windowState: WindowState) {
    val focusManager = LocalFocusManager.current

    var isFullScreen by rememberSaveable {
        mutableStateOf(false)
    }
    val updateFullScreen: (Boolean) -> Unit = { newValue -> isFullScreen = newValue }

    var currentPosition by rememberSaveable {
        mutableStateOf(0L)
    }
    val updatePosition: (Long) -> Unit = { newPosition -> currentPosition = newPosition }

    val paneMode = if (isFullScreen) TwoPaneMode.VerticalSingle else TwoPaneMode.TwoPane

    TwoPaneLayout(
        paneMode = paneMode,
        pane1 = {
            if (isFullScreen) {
                Greetings(name = "hello")
            } else {
                when {
                    windowState.isDualScreen() -> Greetings(name = "hello")
                    windowState.isSingleLandscape() -> RowView(focusManager, isFullScreen, updateFullScreen, currentPosition, updatePosition)
                    else -> ColumnView(focusManager, isFullScreen, updateFullScreen, currentPosition, updatePosition)
                }
            }
        },
        pane2 = {
            Greetings(name = "pane2")
            Greeting(name = "pane2")
        }
    )
}

@Composable
fun ColumnView(
    focusManager: FocusManager,
    isFullScreen: Boolean,
    updateFullScreen: (Boolean) -> Unit,
    currentPosition: Long,
    updatePosition: (Long) -> Unit
) {
    Column {
        Greetings(name = "hello")
    }
}

@Composable
fun RowView(
    focusManager: FocusManager,
    isFullScreen: Boolean,
    updateFullScreen: (Boolean) -> Unit,
    currentPosition: Long,
    updatePosition: (Long) -> Unit
) {
    Row {
        Greetings(name = "hello")
    }
}

@Composable
fun Greetings(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}