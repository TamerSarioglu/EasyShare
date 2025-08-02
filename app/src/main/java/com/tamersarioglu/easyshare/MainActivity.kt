package com.tamersarioglu.easyshare

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.tamersarioglu.easyshare.presentation.ui.screen.MainScreen
import com.tamersarioglu.easyshare.presentation.viewmodel.MainViewModel
import com.tamersarioglu.easyshare.ui.theme.EasyShareTheme
import com.tamersarioglu.easyshare.core.constants.AppConstants
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
                                    AppConstants.STORAGE_PERMISSION_REQUIRED,
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
                MainScreen()
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
