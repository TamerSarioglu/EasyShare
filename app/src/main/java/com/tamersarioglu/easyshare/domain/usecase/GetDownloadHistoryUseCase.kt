package com.tamersarioglu.easyshare.domain.usecase

import com.tamersarioglu.easyshare.domain.model.DownloadHistory
import com.tamersarioglu.easyshare.domain.repository.DownloadHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDownloadHistoryUseCase @Inject constructor(
    private val repository: DownloadHistoryRepository
) {
    operator fun invoke(): Flow<List<DownloadHistory>> {
        return repository.getAllDownloads()
    }
}