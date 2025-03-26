package com.amrk000.ocr.domain.repository

import android.content.Context
import com.amrk000.ocr.data.local.AppDatabase
import com.amrk000.ocr.data.model.HistoryScan

interface IScanHistoryRepository {
    companion object{
        val MAX_RECORDS = 100
    }

    suspend fun getData(): List<HistoryScan>

    suspend fun countData(): Long

    suspend fun addRecord(tableRecord: HistoryScan): Long

    suspend fun deleteOldestRecord(): Int
}