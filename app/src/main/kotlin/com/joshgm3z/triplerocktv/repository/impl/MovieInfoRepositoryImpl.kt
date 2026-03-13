package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.MovieData
import com.joshgm3z.triplerocktv.repository.MovieInfoRepository
import com.joshgm3z.triplerocktv.repository.retrofit.TmdbService
import com.joshgm3z.triplerocktv.util.Logger
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class MovieInfoRepositoryImpl
@Inject
constructor() : MovieInfoRepository {

    private val tmdbService: TmdbService by lazy {
        Retrofit.Builder()
            .baseUrl(" https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbService::class.java)
    }

    override suspend fun searchMovieData(query: String): MovieData? {
        Logger.debug("query = [${query}]")
        return try {
            val response = tmdbService.searchMovieMetadata(query)
            response.body()?.results?.firstOrNull()?.let { searchResult ->
                MovieData(
                    description = searchResult.overview,
                    backPoster = searchResult.backdropPath
                ).apply {
                    Logger.debug("query=[query], movieData=[$this]")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
