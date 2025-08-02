package com.tamersarioglu.easyshare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.tamersarioglu.easyshare.ui.theme.EasyShareTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (!allGranted) {
            Toast.makeText(
                this,
                "Storage permissions are required to download videos to your device",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        requestStoragePermissions()
        
        setContent {
            EasyShareTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DownloadScreen()
                }
            }
        }
    }
    
    private fun requestStoragePermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        
        val permissionsToRequest = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            storagePermissionLauncher.launch(permissionsToRequest.toTypedArray())
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
                                DownloadUtil.downloadVideo(url = url.text) { state ->
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

            when (val state = downloadState) {
                is DownloadResult.Initializing -> {
                    CircularProgressIndicator()
                    Text("Initializing download...")
                }

                is DownloadResult.Progress -> {
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

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val file = File(state.filePath)
                                    if (file.exists()) {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(
                                                "content://com.android.externalstorage.documents/document/primary:Download/EasyShare".toUri(),
                                                "vnd.android.document/directory"
                                            )
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }

                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            try {
                                                val downloadsIntent = Intent(Intent.ACTION_VIEW).apply {
                                                    setDataAndType(
                                                        "content://com.android.externalstorage.documents/document/primary:Download".toUri(),
                                                        "vnd.android.document/directory"
                                                    )
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                }
                                                context.startActivity(downloadsIntent)
                                            } catch (e2: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "File saved to: Downloads/EasyShare/${file.name}",
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
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Open Folder")
                        }
                        
                        Button(
                            onClick = {
                                try {
                                    val file = File(state.filePath)
                                    if (file.exists()) {
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            file
                                        )
                                        
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "video/*"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Video file not found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error sharing: ${e.message}", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Share")
                        }
                    }
                }

                is DownloadResult.Error -> {
                    Text("Download Failed!", color = MaterialTheme.colorScheme.error)
                    Text(
                        state.message,
                        style = MaterialTheme.typography.bodySmall
                    )

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