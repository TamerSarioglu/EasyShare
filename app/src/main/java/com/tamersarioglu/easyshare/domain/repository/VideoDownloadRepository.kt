package com.tamersarioglu.easyshare.domain.repository

import android.content.Context
import com.tamersarioglu.easyshare.domain.model.DownloadState

interface VideoDownloadRepository {
    suspend fun downloadVideo(url: String, onProgress: (DownloadState) -> Unit)
    suspend fun updateYoutubeDL(context: Context): Boolean
    fun cancelDownload()
}