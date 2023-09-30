package com.example.nasa.ui.features.imageofday

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nasa.R
import com.example.nasa.data.models.ImageResponse
import com.example.nasa.ui.features.history.HistoryViewModel
import com.example.nasa.util.getHumanReadableTime
import com.example.nasa.util.handleUrl
import com.example.nasa.util.logd
import com.example.nasa.util.shimmerEffect

@Composable
fun ImageScreen(
    navController: NavController,
    imageScreenUiState: ImageScreenUiState,
    modifier: Modifier = Modifier,
    onEvent: (ImageScreenUiEvent) -> Unit
) {
    LaunchedEffect(key1 = true) {
        onEvent(ImageScreenUiEvent.GetImageOfDay)
    }
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (imageScreenUiState.imageResponse.isValidImage()) {
            var shouldShowShimmer by remember {
                mutableStateOf(false)
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Card(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = imageScreenUiState.imageResponse.getFinalImageUrl(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (!shouldShowShimmer) {
                                            context.handleUrl(imageScreenUiState.imageResponse)
                                        }
                                    },
                                contentScale = ContentScale.FillWidth,
                                error = painterResource(id = R.drawable.nasa),
                                onSuccess = { shouldShowShimmer = false },
                                onError = { shouldShowShimmer = false },
                                onLoading = { shouldShowShimmer = true }
                            )

                            if (imageScreenUiState.imageResponse.mediaType == "video") {
                                PlayButton()
                            }
                            this@Card.AnimatedVisibility(visible = shouldShowShimmer) {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .shimmerEffect()
                                ) {}
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = imageScreenUiState.imageResponse.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        text = imageScreenUiState.imageResponse.date.getHumanReadableTime(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = imageScreenUiState.imageResponse.explanation,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

            }

            AnimatedVisibility(visible = !imageScreenUiState.isShowingPastImages) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp), contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = {
                            logd("navigating to next screen")
                            navController.navigate(HistoryViewModel.getRoute())
                        }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.baseline_history),
                            contentDescription = null
                        )
                    }
                }
            }

        }

        AnimatedVisibility(visible = !imageScreenUiState.imageResponse.isValidImage() && imageScreenUiState.isError) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = imageScreenUiState.userMessage,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onEvent(ImageScreenUiEvent.GetImageOfDay) }) {
                    Text(text = "Retry", modifier = Modifier.padding(horizontal = 8.dp))
                }
            }


        }

        AnimatedVisibility(visible = imageScreenUiState.isLoading) {
            CircularProgressIndicator()
        }
    }
}

fun ImageResponse.getFinalImageUrl(): String {
    return if (this.mediaType == "image")
        url
    else
        thumbnail
}

@Composable
fun PlayButton(
    modifier: Modifier = Modifier
) {
    Card(shape = CircleShape, modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null,
            Modifier
                .padding(3.dp)
                .size(35.dp))
    }
}

private fun ImageResponse.isValidImage(): Boolean {
    return !(url.isEmpty() || hdUrl.isEmpty() || title.isEmpty())
}
