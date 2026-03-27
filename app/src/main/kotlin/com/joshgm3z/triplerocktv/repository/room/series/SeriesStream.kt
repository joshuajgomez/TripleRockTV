package com.joshgm3z.triplerocktv.repository.room.series

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joshgm3z.triplerocktv.repository.data.Episode

@Entity(tableName = "series_stream")
data class SeriesStream(
    @PrimaryKey
    val seriesId: Int,
    val num: Int,
    val name: String,
    val coverImageUrl: String?,
    val backdropUrl: String?,
    val plot: String?,
    val cast: String?,
    val director: String?,
    val genre: String?,
    val releaseDate: String?,
    val lastModified: String?,
    val rating: String?,
    val categoryId: Int,
    val lastPlayed: Long = 0,
    val lastPlayedEpisodeId: Int = 0,

    val seasons: List<Season>? = null,
    val inMyList: Boolean = false,
    val timeAddedToList: Long = 0L,
)

data class Season(
    val episodes: List<Episode>,
    val number: Int,
    val name: String,
    val coverImageUrl: String,
    val overview: String,
    val voteAverage: Float,
)
