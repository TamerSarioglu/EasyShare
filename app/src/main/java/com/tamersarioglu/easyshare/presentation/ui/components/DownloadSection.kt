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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            text = "EasyShare - YouTube Downloader",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("Enter Video URL") },
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
                Text(if (isDownloading) "Cancel" else "Download")
            }

            OutlinedButton(
                onClick = onUpdateClick,
                enabled = !isDownloading && !isUpdating,
                modifier = Modifier.weight(0.7f)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(16.dp)
                            .height(16.dp)
                    )
                } else {
                    Text("Update")
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
                CircularProgressIndicator()
                Text("Initializing download...")
            }
        }

        is DownloadState.Progress -> {
            Column {
                LinearProgressIndicator(
                    progress = { downloadState.progress / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Downloading... ${"%.1f".format(downloadState.progress)}% ETA: ${downloadState.eta ?: "Calculating..."}")
            }
        }

        is DownloadState.Success -> {
            Column {
                Text("Download Successful!", color = MaterialTheme.colorScheme.primary)
                Text(
                    "Saved to: ${downloadState.filePath}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        is DownloadState.Error -> {
            Column {
                Text("Download Failed!", color = MaterialTheme.colorScheme.error)
                Text(
                    downloadState.message,
                    style = MaterialTheme.typography.bodySmall
                )

                if (downloadState.message.contains("extraction failed", ignoreCase = true) ||
                    downloadState.message.contains("player response", ignoreCase = true)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "💡 Try clicking 'Update' to get the latest yt-dlp version",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        is DownloadState.Cancelled -> {
            Text("Download Cancelled", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        is DownloadState.Idle -> {
            // No status to show
        }
    }
}