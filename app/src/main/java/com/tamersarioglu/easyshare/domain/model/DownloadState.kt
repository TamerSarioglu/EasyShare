package com.tamersarioglu.easyshare.domain.model

sealed class DownloadState {
    data object Idle : DownloadState()
    data object Initializing : DownloadState()
    data class Progress(val progress: Float, val eta: String?) : DownloadState()
    data class Success(val filePath: String) : DownloadState()
    data class Error(val message: String) : DownloadState()
    data object Cancelled : DownloadState()
}