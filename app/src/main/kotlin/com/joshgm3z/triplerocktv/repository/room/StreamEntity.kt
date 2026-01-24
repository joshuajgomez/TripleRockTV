package com.joshgm3z.triplerocktv.repository.room

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
            streamId = 1,
            num = 1,
            name = "The Walking Dead",
            streamType = "TV Show",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/cQvc9N6JiMVKqol3wcYrGshsIdZ.jpg",
            categoryId = 1,
            added = "2023"
        )
    }
}
