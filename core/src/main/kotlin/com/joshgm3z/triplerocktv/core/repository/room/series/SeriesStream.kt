package com.joshgm3z.triplerocktv.core.repository.room.series

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.joshgm3z.triplerocktv.core.repository.data.Episode

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

    val seasons: List<Season>? = null,
) {
    @Ignore
    var lastPlayedEpisodeId: Int? = null

    @Ignore
    var inMyList: Boolean = false
}

data class Season(
    val episodes: List<Episode>,
    val number: Int,
    val name: String,
    val coverImageUrl: String,
    val overview: String,
    val voteAverage: Float,
)
