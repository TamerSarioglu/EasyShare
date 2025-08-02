package com.tamersarioglu.easyshare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tamersarioglu.easyshare.data.local.entity.DownloadHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadHistoryDao {
    
    @Query("SELECT * FROM download_history ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadHistoryEntity>>
    
    @Query("SELECT * FROM download_history WHERE id = :id")
    suspend fun getDownloadById(id: Long): DownloadHistoryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadHistoryEntity): Long
    
    @Delete
    suspend fun deleteDownload(download: DownloadHistoryEntity)
    
    @Query("DELETE FROM download_history WHERE id = :id")
    suspend fun deleteDownloadById(id: Long)
    
    @Query("DELETE FROM download_history")
    suspend fun clearAllDownloads()
    
    @Query("SELECT COUNT(*) FROM download_history")
    suspend fun getDownloadCount(): Int
}