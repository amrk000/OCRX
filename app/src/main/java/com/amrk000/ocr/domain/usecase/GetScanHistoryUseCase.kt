package com.amrk000.ocr.domain.usecase

import android.app.Application
import android.content.Context
import com.amrk000.ocr.data.model.HistoryScan
import com.amrk000.ocr.data.repository.ScanHistoryRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetScanHistoryUseCase(private val repository: ScanHistoryRepository) {
    suspend operator fun invoke() : List<HistoryScan> {
        val data = repository.getData()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")

        for(x in data){
            x.date = Instant.parse(x.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(formatter)
        }

        return data
    }
}