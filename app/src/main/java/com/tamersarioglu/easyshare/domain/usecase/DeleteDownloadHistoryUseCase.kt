package com.tamersarioglu.easyshare.domain.usecase

import com.tamersarioglu.easyshare.domain.repository.DownloadHistoryRepository
import javax.inject.Inject

class DeleteDownloadHistoryUseCase @Inject constructor(
    private val repository: DownloadHistoryRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteDownloadById(id)
    }
}