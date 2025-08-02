package com.tamersarioglu.easyshare.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamersarioglu.easyshare.core.constants.AppConstants
import com.tamersarioglu.easyshare.domain.model.DownloadState
import com.tamersarioglu.easyshare.domain.model.UpdateState

@Composable
fun DownloadSection(
    url: String,
    onUrlChange: (String) -> Unit,
    downloadState: DownloadState,
    updateState: UpdateState,
    onDownloadClick: () -> Unit,
    onCancelClick: () -> Unit,
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDownloading = downloadState is DownloadState.Initializing || downloadState is DownloadState.Progress
    val isUpdating = updateState is UpdateState.Updating

    Column(modifier = modifier) {
        Text(
            text = AppConstants.APP_TITLE,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text(AppConstants.ENTER_VIDEO_URL) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !isDownloading && !isUpdating
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (isDownloading) {
                        onCancelClick()
                    } else {
                        onDownloadClick()
                    }
                },
                enabled = (url.isNotBlank() || isDownloading) && !isUpdating,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isDownloading) AppConstants.CANCEL else AppConstants.DOWNLOAD)
            }

            OutlinedButton(
                onClick = onUpdateClick,
                enabled = !isDownloading && !isUpdating,
                modifier = Modifier.weight(0.7f)
            ) {
                if (isUpdating) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                        )
                    }
                } else {
                    Text(AppConstants.UPDATE)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        UpdateMessage(updateState = updateState)

        Spacer(modifier = Modifier.height(16.dp))

        DownloadStatus(downloadState = downloadState)
    }
}

@Composable
private fun UpdateMessage(updateState: UpdateState) {
    when (updateState) {
        is UpdateState.Success -> {
            Text(
                text = updateState.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        is UpdateState.Error -> {
            Text(
                text = updateState.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        else -> { /* No message to show */ }
    }
}

@Composable
private fun DownloadStatus(downloadState: DownloadState) {
    when (downloadState) {
        is DownloadState.Initializing -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(AppConstants.INITIALIZING_DOWNLOAD)
            }
        }

        is DownloadState.Progress -> {
            Column {
                LinearProgressIndicator(
                    progress = { downloadState.progress / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(AppConstants.DOWNLOADING_PROGRESS.format(downloadState.progress, downloadState.eta ?: AppConstants.CALCULATING_ETA))
            }
        }

        is DownloadState.Success -> {
            Column {
                Text(AppConstants.DOWNLOAD_SUCCESSFUL, color = MaterialTheme.colorScheme.primary)
                Text(
                    AppConstants.SAVED_TO.format(downloadState.filePath),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        is DownloadState.Error -> {
            Column {
                Text(AppConstants.DOWNLOAD_FAILED, color = MaterialTheme.colorScheme.error)
                Text(
                    downloadState.message,
                    style = MaterialTheme.typography.bodySmall
                )

                if (downloadState.message.contains(AppConstants.EXTRACTION_FAILED_KEYWORD, ignoreCase = true) ||
                    downloadState.message.contains(AppConstants.PLAYER_RESPONSE_KEYWORD, ignoreCase = true)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        AppConstants.UPDATE_SUGGESTION,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        is DownloadState.Cancelled -> {
            Text(AppConstants.DOWNLOAD_CANCELLED, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        is DownloadState.Idle -> {
            // No status to show
        }
    }
}