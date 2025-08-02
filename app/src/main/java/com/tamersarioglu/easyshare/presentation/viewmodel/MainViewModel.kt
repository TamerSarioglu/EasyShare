package com.tamersarioglu.easyshare.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamersarioglu.easyshare.domain.model.DownloadState
import com.tamersarioglu.easyshare.domain.model.UpdateState
import com.tamersarioglu.easyshare.domain.usecase.CancelDownloadUseCase
import com.tamersarioglu.easyshare.domain.usecase.DownloadVideoUseCase
import com.tamersarioglu.easyshare.domain.usecase.UpdateYoutubeDLUseCase
import com.tamersarioglu.easyshare.domain.usecase.GetDownloadHistoryUseCase
import com.tamersarioglu.easyshare.domain.usecase.SaveDownloadHistoryUseCase
import com.tamersarioglu.easyshare.domain.usecase.DeleteDownloadHistoryUseCase
import com.tamersarioglu.easyshare.domain.model.DownloadHistory
import com.tamersarioglu.easyshare.core.constants.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val downloadVideoUseCase: DownloadVideoUseCase,
    private val updateYoutubeDLUseCase: UpdateYoutubeDLUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase,
    private val getDownloadHistoryUseCase: GetDownloadHistoryUseCase,
    private val saveDownloadHistoryUseCase: SaveDownloadHistoryUseCase,
    private val deleteDownloadHistoryUseCase: DeleteDownloadHistoryUseCase
) : ViewModel() {

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url.asStateFlow()

    val downloadHistory: StateFlow<List<DownloadHistory>> = getDownloadHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateUrl(newUrl: String) {
        _url.value = newUrl
    }

    fun downloadVideo() {
        if (_url.value.isBlank() || _downloadState.value is DownloadState.Initializing) return
        
        viewModelScope.launch {
            downloadVideoUseCase(_url.value) { state ->
                _downloadState.value = state
                
                // Save to history when download is successful
                if (state is DownloadState.Success) {
                    viewModelScope.launch {
                        saveDownloadToHistory(state.filePath)
                    }
                }
            }
        }
    }
    
    private suspend fun saveDownloadToHistory(filePath: String) {
        try {
            val videoTitle = extractVideoTitleFromPath(filePath)
            saveDownloadHistoryUseCase(
                youtubeUrl = _url.value,
                videoTitle = videoTitle,
                downloadPath = filePath,
                fileSize = getFileSize(filePath)
            )
        } catch (e: Exception) {
            // Log error but don't fail the download process
            android.util.Log.e("MainViewModel", "Failed to save download history", e)
        }
    }
    
    private fun extractVideoTitleFromPath(filePath: String): String {
        return try {
            val fileName = filePath.substringAfterLast("/")
            fileName.substringBeforeLast(".")
        } catch (e: Exception) {
            "Downloaded Video"
        }
    }
    
    private fun getFileSize(filePath: String): Long? {
        return try {
            java.io.File(filePath).length()
        } catch (e: Exception) {
            null
        }
    }

    fun cancelDownload() {
        cancelDownloadUseCase()
        _downloadState.value = DownloadState.Cancelled
    }

    fun updateYoutubeDL(context: Context) {
        if (_updateState.value is UpdateState.Updating) return
        
        viewModelScope.launch {
            _updateState.value = UpdateState.Updating
            
            val success = updateYoutubeDLUseCase(context)
            _updateState.value = if (success) {
                UpdateState.Success(AppConstants.YTDLP_UPDATED_SUCCESS)
            } else {
                UpdateState.Error(AppConstants.YTDLP_UPDATE_FAILED)
            }
            
            // Clear update message after 3 seconds
            delay(AppConstants.UPDATE_MESSAGE_DISPLAY_TIME_MS)
            _updateState.value = UpdateState.Idle
        }
    }

    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }
    
    fun deleteDownloadHistory(id: Long) {
        viewModelScope.launch {
            try {
                deleteDownloadHistoryUseCase(id)
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to delete download history", e)
            }
        }
    }
}