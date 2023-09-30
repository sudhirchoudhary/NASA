package com.example.nasa.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nasa.data.models.ImageResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM images_table")
    fun getAllPastImages(): Flow<List<ImageResponse>>

    @Query("SELECT * FROM images_table WHERE :imageDate = date")
    fun getImage(imageDate: String) : Flow<ImageResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImage(imageResponse: ImageResponse)
}