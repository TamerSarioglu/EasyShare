package com.tamersarioglu.easyshare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_history")
data class DownloadHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val youtubeUrl: String,
    val videoTitle: String,
    val downloadPath: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val fileSize: Long? = null,
    val thumbnailUrl: String? = null
)