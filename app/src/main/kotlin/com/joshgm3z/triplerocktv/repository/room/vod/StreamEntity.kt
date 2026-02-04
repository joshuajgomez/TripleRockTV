package com.joshgm3z.triplerocktv.repository.room.vod

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streams")
data class StreamEntity(
    @PrimaryKey val streamId: Int,
    val num: Int,
    val name: String,
    val streamType: String,
    val streamIcon: String?,
    val categoryId: Int,
    val added: String
) {
    companion object {
        fun sample(): StreamEntity = StreamEntity(
            streamId = 20642,
            num = 1,
            name = "Wonder Woman 1984 (2020)(4K)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/lNVHB85FUDZqLzvug3k6FA07RIr.jpg",
            categoryId = 122,
            added = "1609012046"
        )
    }
}
