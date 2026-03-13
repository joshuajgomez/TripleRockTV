package com.joshgm3z.triplerocktv.repository.retrofit

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbService {

    @GET("search/movie")
    suspend fun searchMovieMetadata(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = Secrets.TMDB_API_KEY,
    ): Response<TmdbSearchResult>
}

data class TmdbSearchResult(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<TmdbMovie>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int
)

data class TmdbMovie(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("original_title")
    val originalTitle: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("genre_ids")
    val genreIds: List<Int>,

    @SerializedName("popularity")
    val popularity: Double,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("vote_count")
    val voteCount: Int,

    @SerializedName("video")
    val hasVideo: Boolean,

    @SerializedName("adult")
    val isAdult: Boolean,

    @SerializedName("original_language")
    val originalLanguage: String
)