package com.amrk000.ocr.presentation.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Vibrator
import android.util.Size
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amrk000.ocr.data.repository.ScanHistoryRepository
import com.amrk000.ocr.data.model.HistoryScan
import com.amrk000.ocr.domain.usecase.CaptureImageUseCase
import com.amrk000.ocr.domain.usecase.ScanTextUseCase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val application: Application,
                                        private val repository: ScanHistoryRepository) : AndroidViewModel(application) {
    private val scanResult = MutableLiveData<String>()
    private val capturedImage = MutableLiveData<Bitmap?>()

    private val imageCapture = ImageCapture.Builder().setTargetResolution(Size(1280, 720)).build()

    fun scanTextFromImage(image: InputImage) {
        viewModelScope.launch {
            ScanTextUseCase(repository)(image).collect{
                scanResult.value = it
            }
        }
    }

    fun captureImageAndScan(){
        viewModelScope.launch {
            CaptureImageUseCase()(application, imageCapture).collect{
                if(it != null) {
                    capturedImage.value = it

                    val inputImage = InputImage.fromBitmap(it, 0)
                    scanTextFromImage(inputImage)
                }
            }
        }
    }

    fun getScanResultObserver(): LiveData<String> {
        return scanResult
    }

    fun getCapturedImageObserver(): LiveData<Bitmap?>{
        return capturedImage
    }

    fun getImageCapture(): ImageCapture{
        return imageCapture;
    }

    fun onBackPressed(){
        capturedImage.value = null
    }
}