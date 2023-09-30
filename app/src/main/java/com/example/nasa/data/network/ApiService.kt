package com.example.nasa.data.network

import com.example.nasa.data.models.ImageResponse
import com.example.nasa.util.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("planetary/apod")
    suspend fun getImageOfDay(
        @Query("date") date: String = "",
        @Query("api_key") apiKey: String = AppConstants.API_KEY
    ): Response<ImageResponse>
}