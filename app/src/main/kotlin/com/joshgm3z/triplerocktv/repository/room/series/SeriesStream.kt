package com.joshgm3z.triplerocktv.repository.room.series

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joshgm3z.triplerocktv.ui.login.UserInfo

@Entity(tableName = "series_stream")
data class SeriesStream(
    @PrimaryKey
    val seriesId: Int,
    val num: Int,
    val name: String,
    val cover: String?,
    val plot: String?,
    val cast: String?,
    val director: String?,
    val genre: String?,
    val releaseDate: String?,
    val lastModified: String?,
    val rating: String?,
    val categoryId: Int
){
    fun videoUrl(userInfo: UserInfo) = "${userInfo.webUrl}/series/${userInfo.username}/${userInfo.password}/$seriesId.mkv"
}