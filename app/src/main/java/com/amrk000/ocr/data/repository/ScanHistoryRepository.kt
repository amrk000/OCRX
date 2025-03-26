package com.amrk000.ocr.data.repository

import android.content.Context
import com.amrk000.ocr.data.local.AppDatabase
import com.amrk000.ocr.data.local.HistoryDao
import com.amrk000.ocr.data.model.HistoryScan
import com.amrk000.ocr.domain.repository.IScanHistoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ScanHistoryRepository @Inject constructor(@ApplicationContext private val context: Context,
    private val historyDao: HistoryDao
) : IScanHistoryRepository {
    companion object{
        val MAX_RECORDS = 100
    }

    override suspend fun getData(): List<HistoryScan>{
        return historyDao.getData()
    }

    override suspend fun countData(): Long{
        return historyDao.countData()
    }

    override suspend fun addRecord(tableRecord: HistoryScan): Long{
        return historyDao.addRecord(tableRecord)
    }

    override suspend fun deleteOldestRecord(): Int{
        return historyDao.deleteOldestRecord()
    }
}