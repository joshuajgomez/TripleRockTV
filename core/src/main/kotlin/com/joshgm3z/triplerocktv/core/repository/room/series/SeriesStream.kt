package com.joshgm3z.triplerocktv.core.repository.room.series

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.joshgm3z.triplerocktv.core.repository.data.Episode

@Entity(tableName = "series_stream")
data class SeriesStream(
    @PrimaryKey
    val seriesId: Int = 0,
    val num: Int = 0,
    val name: String = "",
    val coverImageUrl: String? = null,
    val backdropUrl: String? = null,
    val plot: String? = null,
    val cast: String? = null,
    val director: String? = null,
    val genre: String? = null,
    val releaseDate: String? = null,
    val lastModified: Long = 0L,
    val rating: String? = null,
    val categoryId: Int = 0,

    val seasons: List<Season>? = null,
) {
    @Ignore
    var lastPlayedEpisodeId: Int? = null

    @Ignore
    var favorite: Boolean = false

    override fun equals(other: Any?): Boolean {
        val new = other as SeriesStream
        return seriesId == new.seriesId
                && seasons == new.seasons
                && favorite == new.favorite
                && lastPlayedEpisodeId == new.lastPlayedEpisodeId
    }
}

data class Season(
    val episodes: List<Episode>,
    val number: Int,
    val name: String,
    val coverImageUrl: String,
    val overview: String,
    val voteAverage: Float,
) {
    override fun equals(other: Any?): Boolean {
        val new = other as Season
        return number == new.number
                && episodes == new.episodes
    }
}
