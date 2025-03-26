package com.amrk000.ocr.presentation.view.ui.components

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraView(context: Context, lifecycleOwner: LifecycleOwner, imageCapture: ImageCapture): Camera {
    val cameraProvider = ProcessCameraProvider.getInstance(LocalContext.current).get()

    val cameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val previewView = PreviewView(context).apply {
        scaleType = PreviewView.ScaleType.FILL_CENTER
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    }

    val preview = remember {androidx.camera.core.Preview.Builder().build().apply {
        setSurfaceProvider(previewView.surfaceProvider)
    }}

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            previewView
        },
        onRelease = {
            cameraProvider.unbind()
        }
    )

    return cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
}