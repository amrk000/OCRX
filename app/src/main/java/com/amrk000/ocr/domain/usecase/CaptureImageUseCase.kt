package com.amrk000.ocr.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Size
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow

class CaptureImageUseCase {

    suspend operator fun invoke(context: Context, imageCapture: ImageCapture) : MutableStateFlow<Bitmap?>{
        val imageResult = MutableStateFlow<Bitmap?>(null)

        imageCapture.takePicture(ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val scannedImage = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.toBitmap().width,
                    image.toBitmap().height,
                    Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) },
                    true
                )

                imageResult.value = scannedImage

            }
        })

        return imageResult
    }
}