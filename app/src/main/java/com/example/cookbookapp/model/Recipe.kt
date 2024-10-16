package com.example.cookbookapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity
data class Recipe(

    @ColumnInfo(name= "name")
    var name: String,

    @ColumnInfo(name="ingredient")
    var ingredient: String,

    @ColumnInfo(name="image")
    var image: ByteArray
)