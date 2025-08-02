package com.tamersarioglu.easyshare.domain.usecase

import com.tamersarioglu.easyshare.domain.model.DownloadState
import com.tamersarioglu.easyshare.domain.repository.VideoDownloadRepository
import javax.inject.Inject

class DownloadVideoUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(url: String, onProgress: (DownloadState) -> Unit) {
        if (url.isBlank()) {
            onProgress(DownloadState.Error("URL cannot be empty"))
            return
        }
        repository.downloadVideo(url, onProgress)
    }
}