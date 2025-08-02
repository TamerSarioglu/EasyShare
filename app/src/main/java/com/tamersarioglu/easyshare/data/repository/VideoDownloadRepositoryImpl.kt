package com.tamersarioglu.easyshare.data.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import com.tamersarioglu.easyshare.domain.model.DownloadState
import com.tamersarioglu.easyshare.domain.repository.VideoDownloadRepository
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.YoutubeDLResponse
import com.tamersarioglu.easyshare.core.constants.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoDownloadRepositoryImpl @Inject constructor() : VideoDownloadRepository {

    private companion object {
        const val TAG = AppConstants.TAG_VIDEO_DOWNLOAD_REPOSITORY
    }

    private var isCancelled = false

    override suspend fun downloadVideo(url: String, onProgress: (DownloadState) -> Unit) {
        onProgress(DownloadState.Initializing)
        isCancelled = false

        val formatOptions = AppConstants.VIDEO_FORMAT_OPTIONS

        for ((index, format) in formatOptions.withIndex()) {
            try {
                Log.d(TAG, AppConstants.TRYING_FORMAT.format(format, index + 1, formatOptions.size))

                val request = createDownloadRequest(url, format)
                val response: YoutubeDLResponse = withContext(Dispatchers.IO) {
                    if (isCancelled) {
                        throw InterruptedException(AppConstants.DOWNLOAD_CANCELLED_BY_USER)
                    }
                    YoutubeDL.getInstance().execute(request)
                }

                if (response.exitCode == 0) {
                    Log.d(TAG, AppConstants.DOWNLOAD_SUCCESSFUL_FORMAT.format(format))
                    val filePath = findDownloadedFile()
                    onProgress(DownloadState.Success(filePath))
                    return
                } else {
                    if (index == formatOptions.size - 1) {
                        val errorMessage = AppConstants.DOWNLOAD_FAILED_ALL_FORMATS.format(response.exitCode, response.out)
                        Log.e(TAG, errorMessage)
                        onProgress(DownloadState.Error(errorMessage))
                        return
                    }
                    Log.w(TAG, AppConstants.FORMAT_FAILED_EXIT_CODE.format(format, response.exitCode))
                }

            } catch (e: InterruptedException) {
                Log.i(TAG, AppConstants.DOWNLOAD_CANCELLED_BY_USER)
                onProgress(DownloadState.Cancelled)
                return
            } catch (e: Exception) {
                Log.w(TAG, AppConstants.FORMAT_FAILED_EXCEPTION.format(format, e.message))

                if (index == formatOptions.size - 1) {
                    Log.e(TAG, AppConstants.DOWNLOAD_ERROR_ALL_FORMATS, e)
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
                Log.d(TAG, AppConstants.YTDLP_UPDATED_LOG)
                true
            } catch (e: Exception) {
                Log.e(TAG, AppConstants.YTDLP_UPDATE_FAILED_LOG, e)
                false
            }
        }
    }

    override fun cancelDownload() {
        isCancelled = true
    }

    private fun createDownloadRequest(url: String, format: String): YoutubeDLRequest {
        val request = YoutubeDLRequest(url)
        val outputDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), AppConstants.FOLDER_NAME)
            .apply { mkdirs() }
        
        val outputTemplate = AppConstants.OUTPUT_TEMPLATE.format(outputDir.absolutePath)
        request.addOption(AppConstants.OUTPUT_OPTION, outputTemplate)
        request.addOption(AppConstants.RESTRICT_FILENAMES)
        request.addOption(AppConstants.FORMAT_OPTION, format)
        request.addOption(AppConstants.EXTRACTOR_ARGS, AppConstants.EXTRACTOR_ARGS_VALUE)
        request.addOption(AppConstants.NO_CHECK_CERTIFICATES)
        request.addOption(AppConstants.USER_AGENT, AppConstants.USER_AGENT_VALUE)
        request.addOption(AppConstants.COMPAT_OPTIONS, AppConstants.COMPAT_OPTIONS_VALUE)
        request.addOption(AppConstants.EXTRACTOR_RETRIES, AppConstants.EXTRACTOR_RETRIES_VALUE)
        request.addOption(AppConstants.SOCKET_TIMEOUT, AppConstants.SOCKET_TIMEOUT_VALUE)
        request.addOption(AppConstants.NEWLINE)
        request.addOption(AppConstants.NO_WARNINGS)
        request.addOption(AppConstants.IGNORE_ERRORS)
        
        return request
    }

    private fun findDownloadedFile(): String {
        val outputDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), AppConstants.FOLDER_NAME)
        
        val downloadedFiles = outputDir.listFiles()
        if (downloadedFiles != null && downloadedFiles.isNotEmpty()) {
            val recentFile = downloadedFiles.maxByOrNull { it.lastModified() }
            if (recentFile != null) {
                Log.d(TAG, AppConstants.FOUND_FILE_EASYSHARE.format(recentFile.absolutePath))
                return recentFile.absolutePath
            }
        }
        
        // Fallback: check Downloads root folder
        val downloadsRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val rootFiles = downloadsRoot.listFiles()?.filter { 
            it.isFile && it.lastModified() > System.currentTimeMillis() - AppConstants.FILE_SEARCH_TIME_WINDOW_MS
        }
        
        if (rootFiles != null && rootFiles.isNotEmpty()) {
            val recentFile = rootFiles.maxByOrNull { it.lastModified() }
            if (recentFile != null) {
                val targetFile = File(outputDir, recentFile.name)
                try {
                    if (recentFile.renameTo(targetFile)) {
                        Log.d(TAG, AppConstants.MOVED_FILE_TO_EASYSHARE.format(targetFile.absolutePath))
                        return targetFile.absolutePath
                    } else {
                        Log.w(TAG, AppConstants.COULD_NOT_MOVE_FILE.format(recentFile.absolutePath))
                        return recentFile.absolutePath
                    }
                } catch (e: Exception) {
                    Log.w(TAG, AppConstants.ERROR_MOVING_FILE.format(recentFile.absolutePath), e)
                    return recentFile.absolutePath
                }
            }
        }
        
        return "${outputDir.absolutePath}/downloaded_video"
    }

    private fun mapExceptionToUserMessage(e: Exception): String {
        return when {
            e.message?.contains(AppConstants.FAILED_TO_EXTRACT_PLAYER_RESPONSE) == true -> {
                AppConstants.YOUTUBE_EXTRACTION_FAILED.format(e.message)
            }
            e.message?.contains(AppConstants.VIDEO_UNAVAILABLE_KEYWORD) == true -> {
                AppConstants.VIDEO_UNAVAILABLE
            }
            e.message?.contains(AppConstants.PRIVATE_VIDEO_KEYWORD) == true -> {
                AppConstants.PRIVATE_VIDEO
            }
            e.message?.contains(AppConstants.SIGN_IN_CONFIRM_AGE) == true -> {
                AppConstants.AGE_RESTRICTED_VIDEO
            }
            e.message?.contains(AppConstants.VIDEO_NOT_AVAILABLE_KEYWORD) == true -> {
                AppConstants.VIDEO_NOT_AVAILABLE_REGION
            }
            else -> AppConstants.DOWNLOAD_ERROR_GENERIC.format(e.message)
        }
    }
}