package com.tamersarioglu.easyshare.data.repository

import com.tamersarioglu.easyshare.data.local.dao.DownloadHistoryDao
import com.tamersarioglu.easyshare.data.local.entity.DownloadHistoryEntity
import com.tamersarioglu.easyshare.domain.model.DownloadHistory
import com.tamersarioglu.easyshare.domain.repository.DownloadHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadHistoryRepositoryImpl @Inject constructor(
    private val downloadHistoryDao: DownloadHistoryDao
) : DownloadHistoryRepository {
    
    override fun getAllDownloads(): Flow<List<DownloadHistory>> {
        return downloadHistoryDao.getAllDownloads().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getDownloadById(id: Long): DownloadHistory? {
        return downloadHistoryDao.getDownloadById(id)?.toDomainModel()
    }
    
    override suspend fun insertDownload(download: DownloadHistory): Long {
        return downloadHistoryDao.insertDownload(download.toEntity())
    }
    
    override suspend fun deleteDownload(download: DownloadHistory) {
        downloadHistoryDao.deleteDownload(download.toEntity())
    }
    
    override suspend fun deleteDownloadById(id: Long) {
        downloadHistoryDao.deleteDownloadById(id)
    }
    
    override suspend fun clearAllDownloads() {
        downloadHistoryDao.clearAllDownloads()
    }
    
    override suspend fun getDownloadCount(): Int {
        return downloadHistoryDao.getDownloadCount()
    }
}

// Extension functions for mapping between domain and data models
private fun DownloadHistoryEntity.toDomainModel(): DownloadHistory {
    return DownloadHistory(
        id = id,
        youtubeUrl = youtubeUrl,
        videoTitle = videoTitle,
        downloadPath = downloadPath,
        downloadedAt = downloadedAt,
        fileSize = fileSize,
        thumbnailUrl = thumbnailUrl
    )
}

private fun DownloadHistory.toEntity(): DownloadHistoryEntity {
    return DownloadHistoryEntity(
        id = id,
        youtubeUrl = youtubeUrl,
        videoTitle = videoTitle,
        downloadPath = downloadPath,
        downloadedAt = downloadedAt,
        fileSize = fileSize,
        thumbnailUrl = thumbnailUrl
    )
}