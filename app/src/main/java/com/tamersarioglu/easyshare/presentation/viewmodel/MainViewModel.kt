package com.tamersarioglu.easyshare.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamersarioglu.easyshare.domain.model.DownloadState
import com.tamersarioglu.easyshare.domain.model.UpdateState
import com.tamersarioglu.easyshare.domain.usecase.CancelDownloadUseCase
import com.tamersarioglu.easyshare.domain.usecase.DownloadVideoUseCase
import com.tamersarioglu.easyshare.domain.usecase.UpdateYoutubeDLUseCase
import com.tamersarioglu.easyshare.core.constants.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val downloadVideoUseCase: DownloadVideoUseCase,
    private val updateYoutubeDLUseCase: UpdateYoutubeDLUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase
) : ViewModel() {

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url.asStateFlow()

    fun updateUrl(newUrl: String) {
        _url.value = newUrl
    }

    fun downloadVideo() {
        if (_url.value.isBlank() || _downloadState.value is DownloadState.Initializing) return
        
        viewModelScope.launch {
            downloadVideoUseCase(_url.value) { state ->
                _downloadState.value = state
            }
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
}