package com.tamersarioglu.easyshare.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.tamersarioglu.easyshare.data.local.dao.DownloadHistoryDao
import com.tamersarioglu.easyshare.data.local.entity.DownloadHistoryEntity

@Database(
    entities = [DownloadHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EasyShareDatabase : RoomDatabase() {
    
    abstract fun downloadHistoryDao(): DownloadHistoryDao
    
    companion object {
        const val DATABASE_NAME = "easyshare_database"
    }
}