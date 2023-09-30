package com.example.nasa.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.example.nasa.data.models.ImageResponse
import kotlinx.datetime.toLocalDate


fun Any.logd(msg: String) {
    Log.d("RequestX", "${this.javaClass.simpleName}: $msg")
}

fun String.getHumanReadableTime(): String {
    val localDate = toLocalDate()
    return "${localDate.dayOfMonth}, ${localDate.month.name.lowercase().take(3)} ${localDate.year}"
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFBBB9B9),
                Color(0xFF8D8787),
                Color(0xFFBBB9B9)
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

/**
 * start an implicit intent to show video or the image.
 */
fun Context.handleUrl(imageResponse: ImageResponse) {
    if(imageResponse.url.isEmpty())
        return
    val playVideo = Intent(Intent.ACTION_VIEW)
    playVideo.setDataAndType(Uri.parse(imageResponse.url), "${imageResponse.mediaType}/*")
    startActivity(playVideo)
}

/**
 * Check whether or not the internet is available.
 */
fun Context.isNetWorkAvailable(): Boolean {
    val result: Boolean
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
        actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return result
}