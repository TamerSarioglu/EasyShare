package com.tamersarioglu.easyshare.data.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import com.tamersarioglu.easyshare.domain.model.DownloadState
import com.tamersarioglu.easyshare.domain.repository.VideoDownloadRepository
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.YoutubeDLResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoDownloadRepositoryImpl @Inject constructor() : VideoDownloadRepository {

    private companion object {
        const val TAG = "VideoDownloadRepository"
    }

    private var isCancelled = false

    override suspend fun downloadVideo(url: String, onProgress: (DownloadState) -> Unit) {
        onProgress(DownloadState.Initializing)
        isCancelled = false

        val formatOptions = listOf(
            "best[height<=720][ext=mp4]",
            "best[height<=480][ext=mp4]",
            "worst[ext=mp4]",
            "best[height<=720]",
            "best[height<=480]",
            "worst"
        )

        for ((index, format) in formatOptions.withIndex()) {
            try {
                Log.d(TAG, "Trying format: $format (attempt ${index + 1}/${formatOptions.size})")

                val request = createDownloadRequest(url, format)
                val response: YoutubeDLResponse = withContext(Dispatchers.IO) {
                    if (isCancelled) {
                        throw InterruptedException("Download cancelled by user")
                    }
                    YoutubeDL.getInstance().execute(request)
                }

                if (response.exitCode == 0) {
                    Log.d(TAG, "Download successful with format: $format")
                    val filePath = findDownloadedFile()
                    onProgress(DownloadState.Success(filePath))
                    return
                } else {
                    if (index == formatOptions.size - 1) {
                        val errorMessage = "Download failed with all format options. Exit code: ${response.exitCode}. Output: ${response.out}"
                        Log.e(TAG, errorMessage)
                        onProgress(DownloadState.Error(errorMessage))
                        return
                    }
                    Log.w(TAG, "Format $format failed (exit code: ${response.exitCode}), trying next option...")
                }

            } catch (e: InterruptedException) {
                Log.i(TAG, "Download was cancelled")
                onProgress(DownloadState.Cancelled)
                return
            } catch (e: Exception) {
                Log.w(TAG, "Format $format failed with exception: ${e.message}")

                if (index == formatOptions.size - 1) {
                    Log.e(TAG, "Download error with all format options", e)
                    val errorMessage = mapExceptionToUserMessage(e)
                    onProgress(DownloadState.Error(errorMessage))
                    return
                }
            }
        }
    }

    override suspend fun updateYoutubeDL(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                YoutubeDL.getInstance().updateYoutubeDL(context, YoutubeDL.UpdateChannel.STABLE)
                Log.d(TAG, "yt-dlp binary updated successfully")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update yt-dlp binary", e)
                false
            }
        }
    }

    override fun cancelDownload() {
        isCancelled = true
    }

    private fun createDownloadRequest(url: String, format: String): YoutubeDLRequest {
        val request = YoutubeDLRequest(url)
        val outputDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "EasyShare")
            .apply { mkdirs() }
        
        val outputTemplate = "${outputDir.absolutePath}/%(title)s.%(ext)s"
        request.addOption("-o", outputTemplate)
        request.addOption("--restrict-filenames")
        request.addOption("-f", format)
        request.addOption("--extractor-args", "youtube:player_client=android,web")
        request.addOption("--no-check-certificates")
        request.addOption("--user-agent", "Mozilla/5.0 (Linux; Android 11; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36")
        request.addOption("--compat-options", "no-youtube-channel-redirect")
        request.addOption("--extractor-retries", "3")
        request.addOption("--socket-timeout", "30")
        request.addOption("--newline")
        request.addOption("--no-warnings")
        request.addOption("--ignore-errors")
        
        return request
    }

    private fun findDownloadedFile(): String {
        val outputDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "EasyShare")
        
        val downloadedFiles = outputDir.listFiles()
        if (downloadedFiles != null && downloadedFiles.isNotEmpty()) {
            val recentFile = downloadedFiles.maxByOrNull { it.lastModified() }
            if (recentFile != null) {
                Log.d(TAG, "Found file in EasyShare folder: ${recentFile.absolutePath}")
                return recentFile.absolutePath
            }
        }
        
        // Fallback: check Downloads root folder
        val downloadsRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val rootFiles = downloadsRoot.listFiles()?.filter { 
            it.isFile && it.lastModified() > System.currentTimeMillis() - 60000
        }
        
        if (rootFiles != null && rootFiles.isNotEmpty()) {
            val recentFile = rootFiles.maxByOrNull { it.lastModified() }
            if (recentFile != null) {
                val targetFile = File(outputDir, recentFile.name)
                try {
                    if (recentFile.renameTo(targetFile)) {
                        Log.d(TAG, "Moved file from root Downloads to EasyShare: ${targetFile.absolutePath}")
                        return targetFile.absolutePath
                    } else {
                        Log.w(TAG, "Could not move file, using original location: ${recentFile.absolutePath}")
                        return recentFile.absolutePath
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error moving file, using original location: ${recentFile.absolutePath}", e)
                    return recentFile.absolutePath
                }
            }
        }
        
        return "${outputDir.absolutePath}/downloaded_video"
    }

    private fun mapExceptionToUserMessage(e: Exception): String {
        return when {
            e.message?.contains("Failed to extract any player response") == true -> {
                "YouTube extraction failed. Try updating the app or the video might be restricted. Error: ${e.message}"
            }
            e.message?.contains("Video unavailable") == true -> {
                "Video is unavailable or has been removed."
            }
            e.message?.contains("Private video") == true -> {
                "Cannot download private videos."
            }
            e.message?.contains("Sign in to confirm your age") == true -> {
                "Age-restricted video. Cannot download without authentication."
            }
            e.message?.contains("This video is not available") == true -> {
                "Video is not available in your region or has been removed."
            }
            else -> "Download error: ${e.message}"
        }
    }
}