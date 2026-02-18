package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.ui.login.UserInfo

@Entity(tableName = "stream_data")
data class StreamData(
    @PrimaryKey val streamId: Int,
    val num: Int,
    val name: String,
    val streamTypeText: String,
    val streamIcon: String?,
    val categoryId: Int,
    val added: String,

    val streamType: StreamType,
    val extension: String = "mkv",
    val lastPlayed: Long = 0,
    val totalDuration: Long = 0,
    val playedDuration: Long = 0,
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
        )
    }

    fun videoUrl(userInfo: UserInfo) =
        "${userInfo.webUrl}/$streamType/${userInfo.username}/${userInfo.password}/$streamId.$extension"


}
