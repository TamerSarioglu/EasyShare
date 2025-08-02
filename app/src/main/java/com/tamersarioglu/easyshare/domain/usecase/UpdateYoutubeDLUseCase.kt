package com.tamersarioglu.easyshare.domain.usecase

import android.content.Context
import com.tamersarioglu.easyshare.domain.repository.VideoDownloadRepository
import javax.inject.Inject

class UpdateYoutubeDLUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(context: Context): Boolean {
        return repository.updateYoutubeDL(context)
    }
}