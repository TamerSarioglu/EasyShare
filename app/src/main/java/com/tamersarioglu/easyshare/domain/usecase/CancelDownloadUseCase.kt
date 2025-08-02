package com.tamersarioglu.easyshare.domain.usecase

import com.tamersarioglu.easyshare.domain.repository.VideoDownloadRepository
import javax.inject.Inject

class CancelDownloadUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    operator fun invoke() {
        repository.cancelDownload()
    }
}