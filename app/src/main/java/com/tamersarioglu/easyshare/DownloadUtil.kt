package com.tamersarioglu.easyshare

import com.yausername.youtubedl_android.YoutubeDLRequest
import android.content.Context
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import android.os.Environment

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

        // Enhanced format options with more fallbacks
        val formatOptions = listOf(
            // Try simple best formats first
            "best[height<=720][ext=mp4]",
            "best[height<=480][ext=mp4]",
            "worst[ext=mp4]",
            // Fallback to any format
            "best[height<=720]",
            "best[height<=480]",
            "worst"
        )

        for ((index, format) in formatOptions.withIndex()) {
            try {
                Log.d(TAG, "Trying format: $format (attempt ${index + 1}/${formatOptions.size})")

                // 1. Create a request
                val request = YoutubeDLRequest(url)

                // 2. Set options - Save to public Downloads folder
                val outputDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "EasyShare")
                    .apply { mkdirs() }
                
                // Use absolute path for output template with proper escaping
                val outputTemplate = "${outputDir.absolutePath}/%(title)s.%(ext)s"
                request.addOption("-o", outputTemplate)
                
                // Add additional options to ensure file goes to correct location
                request.addOption("--restrict-filenames")  // Avoid special characters that might cause path issues

                // Set format
                request.addOption("-f", format)

                // Enhanced extraction options for YouTube
                request.addOption("--extractor-args", "youtube:player_client=android,web")
                request.addOption("--no-check-certificates")
                request.addOption("--user-agent", "Mozilla/5.0 (Linux; Android 11; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36")

                // Additional workaround options
                request.addOption("--compat-options", "no-youtube-channel-redirect")
                request.addOption("--extractor-retries", "3")
                request.addOption("--socket-timeout", "30")

                // Progress and output options
                request.addOption("--newline")
                request.addOption("--no-warnings")
                request.addOption("--ignore-errors")

                // 3. Execute the request on IO thread
                val response: YoutubeDLResponse = withContext(Dispatchers.IO) {
                    if (isCancelled) {
                        throw InterruptedException("Download cancelled by user")
                    }
                    YoutubeDL.getInstance().execute(request)
                }

                // 4. Handle the result
                if (response.exitCode == 0) {
                    // Success - try to find the downloaded file
                    Log.d(TAG, "Download successful with format: $format")
                    Log.d(TAG, "Expected output dir: ${outputDir.absolutePath}")
                    Log.d(TAG, "Output template: $outputTemplate")
                    
                    // First check if file is in our intended directory
                    var finalFilePath: String? = null
                    val downloadedFiles = outputDir.listFiles()
                    
                    if (downloadedFiles != null && downloadedFiles.isNotEmpty()) {
                        // File found in EasyShare folder
                        finalFilePath = downloadedFiles.maxByOrNull { it.lastModified() }?.absolutePath
                        Log.d(TAG, "Found file in EasyShare folder: $finalFilePath")
                    } else {
                        // File might be in the root Downloads folder, let's search there
                        val downloadsRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val rootFiles = downloadsRoot.listFiles()?.filter { 
                            it.isFile && it.lastModified() > System.currentTimeMillis() - 60000 // Files from last minute
                        }
                        
                        if (rootFiles != null && rootFiles.isNotEmpty()) {
                            val recentFile = rootFiles.maxByOrNull { it.lastModified() }
                            if (recentFile != null) {
                                // Move the file to EasyShare folder
                                val targetFile = File(outputDir, recentFile.name)
                                try {
                                    if (recentFile.renameTo(targetFile)) {
                                        finalFilePath = targetFile.absolutePath
                                        Log.d(TAG, "Moved file from root Downloads to EasyShare: $finalFilePath")
                                    } else {
                                        finalFilePath = recentFile.absolutePath
                                        Log.w(TAG, "Could not move file, using original location: $finalFilePath")
                                    }
                                } catch (e: Exception) {
                                    finalFilePath = recentFile.absolutePath
                                    Log.w(TAG, "Error moving file, using original location: $finalFilePath", e)
                                }
                            }
                        }
                    }
                    
                    finalFilePath = finalFilePath ?: outputTemplate
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
                    Log.w(TAG, "Format $format failed (exit code: ${response.exitCode}), trying next option...")
                }

            } catch (e: InterruptedException) {
                Log.i(TAG, "Download was cancelled")
                onProgress(DownloadResult.Cancelled)
                return
            } catch (e: Exception) {
                Log.w(TAG, "Format $format failed with exception: ${e.message}")

                // If this is the last format option, handle the error
                if (index == formatOptions.size - 1) {
                    Log.e(TAG, "Download error with all format options", e)

                    val errorMessage = when {
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

                    onProgress(DownloadResult.Error(errorMessage))
                    return
                }
                // Otherwise, continue to next format option
            }
        }
    }

    /**
     * Try to update the yt-dlp binary
     */
    suspend fun updateYoutubeDL(context: Context): Boolean {
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