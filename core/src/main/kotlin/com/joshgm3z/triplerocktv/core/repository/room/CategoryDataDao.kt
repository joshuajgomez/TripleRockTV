package com.joshgm3z.triplerocktv.core.repository.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.joshgm3z.triplerocktv.core.repository.StreamType

@Dao
interface CategoryDataDao {
    @Query("SELECT * FROM category_data WHERE streamType = :streamType")
    fun getAllOfType(streamType: StreamType): List<CategoryData>

    @Query("SELECT * FROM category_data WHERE streamType = :streamType AND categoryName LIKE '%' || :titleKey ||'%'")
    fun getAllOfTypeWithTitleKey(streamType: StreamType, titleKey: String): List<CategoryData>

    @Query("SELECT * FROM category_data")
    fun getAll(): List<CategoryData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryData: CategoryData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categoryDataList: List<CategoryData>)

    @Transaction
    suspend fun replaceData(streamType: StreamType, categoryDataList: List<CategoryData>) {
        deleteAllOfType(streamType)
        insertAll(categoryDataList)
    }

    @Query("DELETE FROM category_data")
    suspend fun deleteAll()

    @Query("DELETE FROM category_data WHERE streamType = :streamType")
    suspend fun deleteAllOfType(streamType: StreamType)
}