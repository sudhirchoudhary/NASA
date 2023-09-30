package com.example.nasa.data

import com.example.nasa.data.db.ImageDao
import com.example.nasa.data.models.ImageResponse
import com.example.nasa.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * central place to handle the handle the data sources and expose the required functions.
 */
class Repository @Inject constructor(
    private val apiService: ApiService,
    private val imageDao: ImageDao
){
    /**
     * fetch image data from api and store it in db.
     */
    //2014-11-03
    suspend fun getImageOfDay(date: String = "") = apiService
        .getImageOfDay(date)
        .mapToResponse().onEach {
            if(it is Response.Success)
                insertImageInDb(it.data)
        }

    fun getImageFromDB(imageDate: String) = imageDao.getImage(imageDate).flowOn(Dispatchers.IO)

    private suspend fun insertImageInDb(imageResponse: ImageResponse) {
        withContext(Dispatchers.IO) {
            imageDao.insertImage(imageResponse)
        }
    }

    fun getAllImagesFromDb() = imageDao.getAllPastImages().flowOn(Dispatchers.IO)
}