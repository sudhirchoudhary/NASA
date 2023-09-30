package com.example.nasa.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "images_table")
data class ImageResponse(
    @PrimaryKey(autoGenerate = false)
    var date: String = "",
    var copyright: String = "",
    var explanation: String = "",
    @Json(name = "hdurl")
    var hdUrl: String = "",
    @Json(name = "media_type")
    var mediaType: String = "",
    @Json(name = "service_version")
    var serviceVersion: String = "",
    var title: String = "",
    var url: String = "",
    @Json(name = "thumbnail_url")
    var thumbnail: String = ""
)