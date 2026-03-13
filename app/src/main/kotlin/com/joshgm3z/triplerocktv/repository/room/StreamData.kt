package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.ui.login.UserInfo

/**
 * Minimum video playback duration for the app to consider the video "started".
 */
const val MIN_PLAYBACK_DURATION = 5000L

@Entity(tableName = "stream_data")
data class StreamData(
    @PrimaryKey val streamId: Int,
    val num: Int,
    val name: String,
    val streamTypeText: String,
    val streamIcon: String?,
    val categoryId: Int,
    val added: String,
    val rating: Float,
    val extension: String,

    val streamType: StreamType,
    val inMyList: Boolean = false,
    val timeAddedToList: Long = 0,
    val subtitleUrl: String? = null,
    val subtitleTitle: String? = null,
    val subtitleLanguage: String? = null,
    val lastPlayed: Long = 0,
    val totalDuration: Long = 0,
    val playedDuration: Long = 0,
    val description: String? = null,
    val backPosterUrl: String? = null,
) {
    companion object {
        fun sample(): StreamData = StreamData(
            streamId = 20642,
            num = 1,
            name = "Wonder Woman 1984 (2020)(4K)",
            streamTypeText = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/lNVHB85FUDZqLzvug3k6FA07RIr.jpg",
            categoryId = 122,
            added = "1609012046",
            lastPlayed = System.currentTimeMillis(),
            streamType = StreamType.VideoOnDemand,
            rating = 1.5f,
            extension = "mp4"
        )
    }

    fun videoUrl(userInfo: UserInfo) =
        "${userInfo.webUrl}/$streamTypeText/${userInfo.username}/${userInfo.password}/$streamId.$extension"

    fun progressPercent(): Int = when {
        totalDuration == 0L -> 0
        else -> ((playedDuration.toDouble() / totalDuration) * 100).toInt()
    }

    val startedWatching: Boolean
        get() = lastPlayed > MIN_PLAYBACK_DURATION
}

fun Long.toTextTime(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return buildString {
        if (hours > 0) append("$hours hour ")
        if (minutes > 0 || hours > 0) append("$minutes minutes")
    }.trim()
}
