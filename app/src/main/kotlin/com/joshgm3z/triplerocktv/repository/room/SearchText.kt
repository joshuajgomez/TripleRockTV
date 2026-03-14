package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class SearchText(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String
)

@Dao
abstract class SearchTextDao {
    @Insert
    abstract fun insert(searchText: SearchText)

    @Query("SELECT * FROM SearchText LIMIT 5")
    abstract fun getAll(): List<SearchText>
}