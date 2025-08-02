package com.tamersarioglu.easyshare

import android.app.Application
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                YoutubeDL.getInstance().init(applicationContext)
                Log.d(TAG, "youtubedl-android initialized successfully")

                try {
                    YoutubeDL.getInstance().updateYoutubeDL(applicationContext, YoutubeDL.UpdateChannel.STABLE)
                    Log.d(TAG, "yt-dlp binary updated successfully")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to update yt-dlp binary, using existing version", e)
                }

            } catch (e: YoutubeDLException) {
                Log.e(TAG, "Failed to initialize youtubedl-android", e)
            }
        }
    }
}