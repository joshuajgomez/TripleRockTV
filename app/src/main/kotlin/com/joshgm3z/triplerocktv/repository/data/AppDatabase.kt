package com.joshgm3z.triplerocktv.repository.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CategoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}
