package com.joshgm3z.triplerocktv.core.repository.room

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(indices = [Index(value = ["text"], unique = true)])
data class SearchHint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
)

@Dao
abstract class SearchHintDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(searchHint: SearchHint)

    @Query("SELECT * FROM SearchHint ORDER BY id DESC LIMIT :limit")
    abstract fun getSearchHints(limit: Int = 5): List<SearchHint>

    @Query("DELETE FROM SearchHint")
    abstract fun deleteAll()
}