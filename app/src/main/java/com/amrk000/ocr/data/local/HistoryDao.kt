package com.amrk000.ocr.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amrk000.ocr.data.model.HistoryScan

@Dao
interface HistoryDao {
    @Query("SELECT * FROM scans_history ORDER BY DATETIME(date, 'UTC') DESC")
    fun getData(): List<HistoryScan>

    @Query("SELECT COUNT(*) FROM scans_history")
    fun countData(): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRecord(tableRecord: HistoryScan): Long

    @Query("DELETE FROM scans_history WHERE id = (SELECT MIN(id) FROM scans_history)")
    fun deleteOldestRecord(): Int
}