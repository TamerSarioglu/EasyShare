package com.tamersarioglu.easyshare.domain.repository

import com.tamersarioglu.easyshare.domain.model.DownloadHistory
import kotlinx.coroutines.flow.Flow

interface DownloadHistoryRepository {
    
    fun getAllDownloads(): Flow<List<DownloadHistory>>
    
    suspend fun getDownloadById(id: Long): DownloadHistory?
    
    suspend fun insertDownload(download: DownloadHistory): Long
    
    suspend fun deleteDownload(download: DownloadHistory)
    
    suspend fun deleteDownloadById(id: Long)
    
    suspend fun clearAllDownloads()
    
    suspend fun getDownloadCount(): Int
}