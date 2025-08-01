package com.tamersarioglu.easyshare

import com.yausername.youtubedl_android.YoutubeDLRequest
import android.content.Context
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// Sealed class to represent different states of the download process
sealed class DownloadResult {
    data object Initializing : DownloadResult()
    data class Progress(val progress: Float, val eta: String?) : DownloadResult()
    data class Success(val filePath: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
    data object Cancelled : DownloadResult()
}

object DownloadUtil {

    private const val TAG = "DownloadUtil"
    private var isCancelled = false

    suspend fun downloadVideo(
        context: Context,
        url: String,
        onProgress: (DownloadResult) -> Unit
    ) {
        onProgress(DownloadResult.Initializing)
        isCancelled = false

        // Try different format options as fallbacks
        val formatOptions = listOf(
            "best[ext=mp4]", // Simple best mp4
            "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best", // Original format
            "worst[ext=mp4]", // Lower quality as last resort
            "best" // Any format
        )

        for ((index, format) in formatOptions.withIndex()) {
            try {
                // 1. Create a request
                val request = YoutubeDLRequest(url)

                // 2. Set options
                val outputDir = File(context.filesDir, "downloads").apply { mkdirs() }
                val outputTemplate = File(outputDir, "%(title)s.%(ext)s").absolutePath
                request.addOption("-o", outputTemplate)

                // Set format
                request.addOption("-f", format)

                // Add workaround options for YouTube extraction issues
                request.addOption("--extractor-args", "youtube:player_client=android")
                request.addOption("--no-check-certificates")
                request.addOption("--user-agent", "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36")
                
                // Add progress hooks for monitoring
                request.addOption("--newline")
                request.addOption("--no-warnings")

                // 3. Execute the request on IO thread
                val response: YoutubeDLResponse = withContext(Dispatchers.IO) {
                    if (isCancelled) {
                        throw InterruptedException("Download cancelled by user")
                    }
                    YoutubeDL.getInstance().execute(request)
                }

                // 4. Handle the result
                if (response.exitCode == 0) {
                    // Success - try to find the downloaded file based on template
                    val downloadedFiles = outputDir.listFiles()
                    val finalFilePath = downloadedFiles?.maxByOrNull { it.lastModified() }?.absolutePath 
                        ?: outputTemplate
                    onProgress(DownloadResult.Success(finalFilePath))
                    return // Success, exit the function
                } else {
                    // If this is the last format option, throw an error
                    if (index == formatOptions.size - 1) {
                        val errorMessage = "Download failed with all format options. Exit code: ${response.exitCode}. Output: ${response.out}"
                        Log.e(TAG, errorMessage)
                        onProgress(DownloadResult.Error(errorMessage))
                        return
                    }
                    // Otherwise, continue to next format option
                    Log.w(TAG, "Format $format failed, trying next option...")
                }

            } catch (e: InterruptedException) {
                Log.i(TAG, "Download was cancelled")
                onProgress(DownloadResult.Cancelled)
                return
            } catch (e: Exception) {
                // If this is the last format option, handle the error
                if (index == formatOptions.size - 1) {
                    Log.e(TAG, "Download error with all format options", e)
                    
                    val errorMessage = when {
                        e.message?.contains("Failed to extract any player response") == true -> {
                            "YouTube extraction failed. The video might be private, age-restricted, or the library needs updating."
                        }
                        e.message?.contains("Video unavailable") == true -> {
                            "Video is unavailable or has been removed."
                        }
                        e.message?.contains("Private video") == true -> {
                            "Cannot download private videos."
                        }
                        else -> "Download error: ${e.message}"
                    }
                    
                    onProgress(DownloadResult.Error(errorMessage))
                    return
                }
                // Otherwise, continue to next format option
                Log.w(TAG, "Format $format failed with exception: ${e.message}, trying next option...")
            }
        }
    }

    fun cancelDownload() {
        isCancelled = true
    }

    // Simple ETA formatting (e.g., converts seconds to mm:ss or hh:mm:ss)
    private fun formatETA(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }
}