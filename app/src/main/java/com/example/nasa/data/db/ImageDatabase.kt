package com.example.nasa.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nasa.data.models.ImageResponse

@Database(entities = [ImageResponse::class], version = 1)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}