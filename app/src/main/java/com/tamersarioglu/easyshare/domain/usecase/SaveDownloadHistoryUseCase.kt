package com.tamersarioglu.easyshare.domain.usecase

import com.tamersarioglu.easyshare.domain.model.DownloadHistory
import com.tamersarioglu.easyshare.domain.repository.DownloadHistoryRepository
import javax.inject.Inject

class SaveDownloadHistoryUseCase @Inject constructor(
    private val repository: DownloadHistoryRepository
) {
    suspend operator fun invoke(
        youtubeUrl: String,
        videoTitle: String,
        downloadPath: String,
        fileSize: Long? = null,
        thumbnailUrl: String? = null
    ): Long {
        val downloadHistory = DownloadHistory(
            youtubeUrl = youtubeUrl,
            videoTitle = videoTitle,
            downloadPath = downloadPath,
            downloadedAt = System.currentTimeMillis(),
            fileSize = fileSize,
            thumbnailUrl = thumbnailUrl
        )
        return repository.insertDownload(downloadHistory)
    }
}