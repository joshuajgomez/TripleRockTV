package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.ui.login.UserInfo

/**
 * Minimum video playback duration for the app to consider the video "started".
 */
const val MIN_PLAYBACK_DURATION = 3 * 60 * 1000L // 180,000 ms

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
    val epgChannelId: String? = null,

    val streamType: StreamType,
    val inMyList: Boolean = false,
    val timeAddedToList: Long = 0,
    val subtitleUrl: String? = null,
    val subtitleTitle: String? = null,
    val subtitleLanguage: String? = null,
    val lastPlayed: Long = 0,
    val playedDuration: Long = 0,

    @Embedded
    val movieMetadata: MovieMetadata? = null
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
        (movieMetadata?.totalDurationMs ?: 0L) == 0L -> 0
        else -> ((playedDuration.toDouble() / (movieMetadata?.totalDurationMs ?: 0L)) * 100).toInt()
    }

    val startedWatching: Boolean
        get() = lastPlayed > MIN_PLAYBACK_DURATION
}

fun Long.toTextTime(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0) append("${minutes}m")
    }.trim()
}

data class MovieMetadata(
    val description: String?,
    val backPoster: String? = null,
    val totalDurationMs: Long = 0L,
    val cast: String? = null,
    val director: String? = null,
    val actors: String? = null,
    val genre: String? = null,
) {
    val backPosterUrl: String = "https://image.tmdb.org/t/p/w1280/$backPoster"
}