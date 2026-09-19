package com.joshgm3z.triplerocktv.core

import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaSyncService : LifecycleService() {

    @Inject
    lateinit var onlineRepository: MediaOnlineRepository

    @Inject
    lateinit var localRepository: MediaLocalRepository

    @Inject
    lateinit var localDatastore: LocalDatastore

    val syncState = MutableStateFlow("")

    private var syncProgressMap = HashMap<StreamType, Int>()

    inner class LocalBinder : android.os.Binder() {
        fun getService(): MediaSyncService = this@MediaSyncService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        Logger.entry
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Logger.entry
        lifecycleScope.launch {
            if (alreadyUpdatedToday()) {
                Logger.debug("Already updated today, skipping sync")
                return@launch
            }
            Logger.debug("Syncing")
            listOf(
                StreamType.VideoOnDemand,
                StreamType.LiveTV,
                StreamType.Series,
            ).forEach { streamType ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val categories = localRepository.fetchCategories(streamType)
                    val size = categories.size
                    categories.forEachIndexed { index, category ->
                        syncProgressMap[streamType] = (index + 1) * 100 / size
                        updateState()
                        onlineRepository.fetchStreams(streamType, category.categoryId)
                    }
                }
            }
        }
    }

    private suspend fun alreadyUpdatedToday(): Boolean {
        val syncInterval24h = 24 * 60 * 60 * 1000L
        val lastContentUpdate = localDatastore.getLastContentUpdate()
        val duration = System.currentTimeMillis() - lastContentUpdate
        val alreadyUpdatedToday = duration < syncInterval24h
        return alreadyUpdatedToday && lastContentUpdate != 0L
    }

    private fun updateState() {
        var combinedProgress = 0
        syncProgressMap.values.forEach { progress ->
            combinedProgress += progress
        }
        syncState.value = "Syncing ${combinedProgress / 3}%"

        if (syncProgressMap.values.all { it == 100 }) {
            syncState.value = "Sync done"
            lifecycleScope.launch {
                localDatastore.notifyLastContentUpdate(System.currentTimeMillis())
                delay(1000)
                syncState.value = ""
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.entry
    }

    companion object {
        fun restart(context: Context) {
            Logger.debug("Restarting MediaSyncService")
            val serviceIntent = Intent(context, MediaSyncService::class.java)
            context.stopService(serviceIntent)
            context.startService(serviceIntent)
        }

        fun stop(context: Context) {
            Logger.debug("Stopping MediaSyncService")
            val serviceIntent = Intent(context, MediaSyncService::class.java)
            context.stopService(serviceIntent)
        }
    }
}
