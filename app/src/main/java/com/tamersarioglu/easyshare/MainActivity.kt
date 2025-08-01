package com.tamersarioglu.easyshare

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tamersarioglu.easyshare.ui.theme.EasyShareTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyShareTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DownloadScreen()
                }
            }
        }
    }
}

@Composable
fun DownloadScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var url by remember { mutableStateOf(TextFieldValue("")) }
    var downloadState by remember { mutableStateOf<DownloadResult>(DownloadResult.Initializing) }
    var isDownloading by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }
    var updateMessage by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "EasyShare - YouTube Downloader",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
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
                        if (url.text.isNotBlank() && !isDownloading) {
                            isDownloading = true
                            downloadState = DownloadResult.Initializing
                            coroutineScope.launch {
                                DownloadUtil.downloadVideo(context, url.text) { state ->
                                    downloadState = state
                                    if (state is DownloadResult.Success ||
                                        state is DownloadResult.Error ||
                                        state is DownloadResult.Cancelled
                                    ) {
                                        isDownloading = false
                                    }
                                }
                            }
                        } else if (isDownloading) {
                            DownloadUtil.cancelDownload()
                        }
                    },
                    enabled = (url.text.isNotBlank() || isDownloading) && !isUpdating,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isDownloading) "Cancel" else "Download")
                }

                OutlinedButton(
                    onClick = {
                        isUpdating = true
                        updateMessage = "Updating yt-dlp..."
                        coroutineScope.launch {
                            val success = DownloadUtil.updateYoutubeDL(context)
                            updateMessage = if (success) {
                                "yt-dlp updated successfully!"
                            } else {
                                "Failed to update yt-dlp"
                            }
                            isUpdating = false
                            // Clear message after 3 seconds
                            kotlinx.coroutines.delay(3000)
                            updateMessage = ""
                        }
                    },
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

            // Show update message
            if (updateMessage.isNotEmpty()) {
                Text(
                    text = updateMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (updateMessage.contains("successfully")) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display download state
            when (val state = downloadState) {
                is DownloadResult.Initializing -> {
                    CircularProgressIndicator()
                    Text("Initializing download...")
                }

                is DownloadResult.Progress -> {
                    // Linear Progress Indicator for percentage
                    LinearProgressIndicator(
                        progress = { state.progress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Downloading... ${"%.1f".format(state.progress)}% ETA: ${state.eta ?: "Calculating..."}")
                }

                is DownloadResult.Success -> {
                    Text("Download Successful!", color = MaterialTheme.colorScheme.primary)
                    Text(
                        "Saved to: ${state.filePath}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            try {
                                val file = File(state.filePath)
                                if (file.exists()) {
                                    // Create intent to open file manager at the folder location
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(
                                            Uri.fromFile(file.parentFile),
                                            "resource/folder"
                                        )
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }

                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Fallback: try different approaches
                                        try {
                                            // Try with file manager intent
                                            val fileManagerIntent =
                                                Intent(Intent.ACTION_GET_CONTENT).apply {
                                                    type = "*/*"
                                                    addCategory(Intent.CATEGORY_OPENABLE)
                                                    putExtra(
                                                        "org.openintents.extra.ABSOLUTE_PATH",
                                                        file.parent
                                                    )
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                }
                                            context.startActivity(fileManagerIntent)
                                        } catch (e2: Exception) {
                                            // Final fallback: show the path and copy to clipboard
                                            val clipboard =
                                                context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText(
                                                "File Path",
                                                file.absolutePath
                                            )
                                            clipboard.setPrimaryClip(clip)

                                            Toast.makeText(
                                                context,
                                                "File path copied to clipboard: ${file.absolutePath}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Video file not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Show in Files")
                    }
                }

                is DownloadResult.Error -> {
                    Text("Download Failed!", color = MaterialTheme.colorScheme.error)
                    Text(
                        state.message,
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Add a hint about updating if it's an extraction error
                    if (state.message.contains("extraction failed", ignoreCase = true) ||
                        state.message.contains("player response", ignoreCase = true)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "💡 Try clicking 'Update' to get the latest yt-dlp version",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                is DownloadResult.Cancelled -> {
                    Text("Download Cancelled", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}