package com.joshgm3z.triplerocktv.repository.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.joshgm3z.triplerocktv.repository.room.series.Season

class SeasonConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromSeasonList(seasons: List<Season>?): String? {
        return gson.toJson(seasons)
    }

    @TypeConverter
    fun toSeasonList(seasonsString: String?): List<Season>? {
        if (seasonsString == null) return null
        val type = object : TypeToken<List<Season>>() {}.type
        return gson.fromJson(seasonsString, type)
    }
}