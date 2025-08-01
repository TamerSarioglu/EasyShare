package com.tamersarioglu.easyshare.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tamersarioglu.easyshare.core.utils.FileUtils
import com.tamersarioglu.easyshare.domain.model.DownloadState
import com.tamersarioglu.easyshare.presentation.ui.components.ActionButtons
import com.tamersarioglu.easyshare.presentation.ui.components.DownloadSection
import com.tamersarioglu.easyshare.presentation.ui.components.DownloadHistorySection
import com.tamersarioglu.easyshare.presentation.viewmodel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val downloadState by viewModel.downloadState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val url by viewModel.url.collectAsState()
    val downloadHistory by viewModel.downloadHistory.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            DownloadSection(
                url = url,
                onUrlChange = viewModel::updateUrl,
                downloadState = downloadState,
                updateState = updateState,
                onDownloadClick = viewModel::downloadVideo,
                onCancelClick = viewModel::cancelDownload,
                onUpdateClick = { viewModel.updateYoutubeDL(context) }
            )

            val currentDownloadState = downloadState
            if (currentDownloadState is DownloadState.Success) {
                ActionButtons(filePath = currentDownloadState.filePath)
            }

            // Show download history
            Spacer(modifier = Modifier.height(24.dp))
            
            DownloadHistorySection(
                downloadHistory = downloadHistory,
                onDeleteDownload = viewModel::deleteDownloadHistory,
                onShareDownload = { filePath ->
                    FileUtils.shareVideo(context, filePath)
                },
                onOpenDownload = { filePath ->
                    FileUtils.openFolder(context, filePath)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}