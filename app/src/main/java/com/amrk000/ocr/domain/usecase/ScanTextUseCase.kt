package com.amrk000.ocr.domain.usecase

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.amrk000.ocr.data.model.HistoryScan
import com.amrk000.ocr.data.repository.ScanHistoryRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant

class ScanTextUseCase(private val repository: ScanHistoryRepository)  {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend operator fun invoke(image: InputImage): MutableStateFlow<String> {
        val text = MutableStateFlow("")

        textRecognizer.process(image).addOnSuccessListener { result ->
            text.value = result.text

            runBlocking {
                if(repository.countData() >= ScanHistoryRepository.MAX_RECORDS){
                    repository.deleteOldestRecord()
                }

                if(result.text.isNotEmpty()) {
                    repository.addRecord(
                        HistoryScan(
                            text = result.text,
                            date = Instant.now().toString()
                        )
                    )
                }
            }
        }

        return text
    }
}