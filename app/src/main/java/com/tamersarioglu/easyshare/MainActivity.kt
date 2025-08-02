package com.tamersarioglu.easyshare

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.tamersarioglu.easyshare.presentation.ui.screen.MainScreen
import com.tamersarioglu.easyshare.presentation.viewmodel.MainViewModel
import com.tamersarioglu.easyshare.ui.theme.EasyShareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val storagePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    permissions ->
                val allGranted = permissions.all { it.value }
                if (!allGranted) {
                    Toast.makeText(
                                    this,
                                    "Storage permissions are required to download videos to your device",
                                    Toast.LENGTH_LONG
                            )
                            .show()
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestStoragePermissions()

        setContent {
            EasyShareTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> MainScreen() }
            }
        }
    }

    private fun requestStoragePermissions() {
        val permissionsToRequest = viewModel.getPermissionsToRequest()

        if (permissionsToRequest.isNotEmpty()) {
            storagePermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
