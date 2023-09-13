package com.example.twinshot

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneLayout
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneMode
import com.microsoft.device.dualscreen.windowstate.WindowState
import java.util.concurrent.Executor
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState


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
                CameraPreview()
            } else {
                when {
                    windowState.isDualScreen() ->
                        CameraPreview()
                    windowState.isSingleLandscape() -> VideoApp()
                    else -> CameraPreview()
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

    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(
                width = 4.dp,
                color = Color.Red, // You can change the color as needed
                shape = RectangleShape
            )
            .padding(16.dp)
    ) {
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

}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Preview is incorrectly scaled in Compose on some devices without this
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                try {
                    // Must unbind the use-cases before rebinding them.
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview
                    )
                } catch (exc: Exception) {
                    Log.e("MainPage", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        })
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