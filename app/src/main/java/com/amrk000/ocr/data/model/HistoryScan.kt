package com.amrk000.ocr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scans_history")
data class HistoryScan(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var text: String,
    var date: String
)
