package com.amrk000.ocr.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amrk000.ocr.data.model.HistoryScan
import com.amrk000.ocr.data.repository.ScanHistoryRepository
import com.amrk000.ocr.domain.usecase.GetScanHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val application: Application,
                       private val repository: ScanHistoryRepository) : AndroidViewModel(application) {
    private val history = MutableLiveData<List<HistoryScan>>()
    private val scan = MutableLiveData<String>()

    fun loadHistory(){
        viewModelScope.launch {
            val result = GetScanHistoryUseCase(repository)
            history.value = result.invoke()
        }
    }

    fun getHistoryObserver(): LiveData<List<HistoryScan>> {
        return history
    }

    fun setScan(text: String){
        scan.value = text
    }

    fun getScanObserver(): LiveData<String> {
        return scan
    }
}