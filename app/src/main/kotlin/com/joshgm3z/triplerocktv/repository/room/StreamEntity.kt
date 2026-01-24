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
            streamIcon = "https://www.themoviedb.org/t/p/w6",
            categoryId = 1,
            added = "2023"
        )
    }
}
