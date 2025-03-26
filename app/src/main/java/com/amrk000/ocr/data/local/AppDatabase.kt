package com.amrk000.ocr.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.amrk000.ocr.data.model.HistoryScan

@Database(entities = [HistoryScan::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): HistoryDao

}