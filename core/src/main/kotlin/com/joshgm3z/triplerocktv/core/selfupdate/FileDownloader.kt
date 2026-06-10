package com.joshgm3z.triplerocktv.core.selfupdate

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

enum class DownloadState {
    Waiting,
    Downloading,
    Completed,
    Error
}

class FileDownloader
@Inject constructor(
    @param:ApplicationContext
    private val context: Context
) {

    private val downloadManager = context.getSystemService(
        Context.DOWNLOAD_SERVICE
    ) as DownloadManager

    fun startDownload(
        fileUrl: String,
        onUpdate: (DownloadState) -> Unit,
        onDownloadComplete: (File) -> Unit
    ) {
        Logger.debug("fileUrl = [${fileUrl}]")
        try {
            onUpdate(DownloadState.Waiting)

            val fileName = "3rock-tv-app.apk"
            val uri = Uri.parse(fileUrl)
            val request = DownloadManager.Request(uri).apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setTitle("Downloading update...")

                // Delete existing file if it exists
                val downloadFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val existingFile = File(downloadFolder, fileName)
                if (existingFile.exists()) {
                    val deleted = existingFile.delete()
                    Logger.debug("Existing file found and deleted: $deleted")
                }

                setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
                setMimeType("application/vnd.android.package-archive")
            }

            val downloadId = downloadManager.enqueue(request)
            onUpdate(DownloadState.Downloading)

            // Register receiver to listen for completion
            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                    if (id == downloadId) {
                        onUpdate(DownloadState.Completed)

                        val downloadFolder =
                            context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        val file = File(downloadFolder, fileName)
                        onDownloadComplete(file)

                        context?.unregisterReceiver(this)
                    }
                }
            }

            context.registerReceiver(
                onComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED // Required for Android 14+
            )

        } catch (e: Exception) {
            Logger.error("Error starting download: ${e.message}")
            e.printStackTrace()
            onUpdate(DownloadState.Error)
        }
    }

    suspend fun getLatestApkReleaseName(apkUrl: String): String? {
        val url = URL(apkUrl)
        val connection = url.openConnection() as HttpURLConnection

        if (connection.responseCode == 200) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            Logger.debug("response = [$response]")
            val jsonObject = JSONObject(response)

            // 1. Get the Release Name (e.g., "Dev Build v2024.05.20-dev-run42")
            val releaseName = jsonObject.getString("name")

            // 2. Extract the number after "-run" using Regex
            return releaseName
        }
        return null
    }
}