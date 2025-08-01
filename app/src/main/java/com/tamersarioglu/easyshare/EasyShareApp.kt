package com.tamersarioglu.easyshare

import android.app.Application
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoInfo
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class EasyShareApp: Application() {
    companion object {
        private const val TAG = "EasyShareApp"
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize youtubedl-android library in a background coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Use applicationContext, which is the recommended context for libraries
                YoutubeDL.getInstance().init(applicationContext)
                Log.d(TAG, "youtubedl-android initialized successfully")

                // Try to update the yt-dlp binary to the latest version
                try {
                    YoutubeDL.getInstance().updateYoutubeDL(applicationContext, YoutubeDL.UpdateChannel.STABLE)
                    Log.d(TAG, "yt-dlp binary updated successfully")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to update yt-dlp binary, using existing version", e)
                    // Don't fail the app if update fails, just log and continue
                }

            } catch (e: YoutubeDLException) {
                Log.e(TAG, "Failed to initialize youtubedl-android", e)
                // Handle initialization failure
                // Post error status to LiveData/StateFlow/event bus if UI notification needed
            }
        }
    }
}