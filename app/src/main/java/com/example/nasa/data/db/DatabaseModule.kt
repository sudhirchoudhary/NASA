package com.example.nasa.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideImageDatabase(
        @ApplicationContext context: Context
    ): ImageDatabase = Room.databaseBuilder(
        context,
        ImageDatabase::class.java,
        "image_database"
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun imageDao(imageDatabase: ImageDatabase): ImageDao = imageDatabase.imageDao()
}