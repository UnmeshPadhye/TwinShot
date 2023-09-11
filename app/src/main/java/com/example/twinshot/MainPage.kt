package com.example.twinshot

import android.widget.VideoView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.viewinterop.AndroidView
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
                VideoApp()
            } else {
                when {
                    windowState.isDualScreen() ->
                        VideoApp()
                    windowState.isSingleLandscape() -> RowView(focusManager, isFullScreen, updateFullScreen, currentPosition, updatePosition)
                    else -> ColumnView(focusManager, isFullScreen, updateFullScreen, currentPosition, updatePosition)
                }
            }
        },
        pane2 = {
            VideoApp()
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
        Greetings(name = "Column")
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
        Greetings(name = "Row")
    }
}

@Composable
fun VideoFeed(videoUri: String) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            VideoView(context).apply {
                setVideoPath(videoUri)
                start()
            }
        }
    )
}

@Composable
fun VideoApp() {
    val videoUri = "android.resource://com.example.twinshot/raw/zeta_ad"
    VideoFeed(videoUri)
}

@Composable
fun Greetings(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}