package com.tamersarioglu.easyshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
                text = "Basic YouTube Downloader",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Enter Video URL") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

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
                enabled = url.text.isNotBlank() || isDownloading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isDownloading) "Cancel" else "Download")
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
                    Text("Saved to: ${state.filePath}")
                    // Optionally, you could add a button here to open the file
                }
                is DownloadResult.Error -> {
                    Text("Download Failed!", color = MaterialTheme.colorScheme.error)
                    Text(state.message)
                }
                is DownloadResult.Cancelled -> {
                    Text("Download Cancelled", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}