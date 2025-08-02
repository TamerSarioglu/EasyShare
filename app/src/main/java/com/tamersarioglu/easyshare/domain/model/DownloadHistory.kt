package com.tamersarioglu.easyshare.domain.model

data class DownloadHistory(
    val id: Long = 0,
    val youtubeUrl: String,
    val videoTitle: String,
    val downloadPath: String,
    val downloadedAt: Long,
    val fileSize: Long? = null,
    val thumbnailUrl: String? = null
) {
    fun getFormattedDate(): String {
        val date = java.util.Date(downloadedAt)
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    fun getFormattedFileSize(): String {
        return fileSize?.let { size ->
            when {
                size < 1024 -> "$size B"
                size < 1024 * 1024 -> "${size / 1024} KB"
                size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
                else -> "${size / (1024 * 1024 * 1024)} GB"
            }
        } ?: "Unknown size"
    }
}