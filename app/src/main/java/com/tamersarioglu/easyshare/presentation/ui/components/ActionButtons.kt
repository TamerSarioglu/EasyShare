package com.tamersarioglu.easyshare.presentation.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.tamersarioglu.easyshare.core.constants.AppConstants
import java.io.File

@Composable
fun ActionButtons(
    filePath: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { openFolder(context, filePath) },
            modifier = Modifier.weight(1f)
        ) {
            Text(AppConstants.OPEN_FOLDER)
        }
        
        Button(
            onClick = { shareVideo(context, filePath) },
            modifier = Modifier.weight(1f)
        ) {
            Text(AppConstants.SHARE)
        }
    }
}

private fun openFolder(context: Context, filePath: String) {
    try {
        val file = File(filePath)
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(
                    AppConstants.DOWNLOADS_EASYSHARE_URI.toUri(),
                    AppConstants.DIRECTORY_MIME_TYPE
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val downloadsIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(
                            AppConstants.DOWNLOADS_URI.toUri(),
                            AppConstants.DIRECTORY_MIME_TYPE
                        )
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(downloadsIntent)
                } catch (e2: Exception) {
                    Toast.makeText(
                        context,
                        AppConstants.FILE_SAVED_TO.format(file.name),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                context,
                AppConstants.VIDEO_FILE_NOT_FOUND,
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, AppConstants.ERROR_PREFIX.format(e.message), Toast.LENGTH_SHORT).show()
    }
}

private fun shareVideo(context: Context, filePath: String) {
    try {
        val file = File(filePath)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = AppConstants.VIDEO_MIME_TYPE
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, AppConstants.SHARE_VIDEO))
        } else {
            Toast.makeText(
                context,
                AppConstants.VIDEO_FILE_NOT_FOUND,
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, AppConstants.ERROR_SHARING_PREFIX.format(e.message), Toast.LENGTH_SHORT).show()
    }
}