package com.example.cookbookapp.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cookbookapp.model.Recipe

@Database(entities = [Recipe::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): RecipeDAO
}