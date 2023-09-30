package com.example.nasa.ui.features.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nasa.R
import com.example.nasa.data.models.ImageResponse
import com.example.nasa.ui.features.imageofday.ImageScreenViewModel
import com.example.nasa.ui.theme.Black10
import com.example.nasa.util.getHumanReadableTime

@Composable
fun HistoryScreen(
    navController: NavController,
    uiState: HistoryScreenUiState,
    modifier: Modifier = Modifier,
    onEvent: (HistoryScreenUiEvent) -> Unit
) {
    LaunchedEffect(key1 = true) {
        onEvent(HistoryScreenUiEvent.GetAllImagesFromDb)
    }
    Box(modifier = modifier.fillMaxSize(), Alignment.Center) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppBar {
                navController.navigateUp()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                Alignment.Center
            ) {
                if(uiState.images.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 4.dp,
                            top = 16.dp,
                            end = 12.dp,
                            bottom = 4.dp
                        )
                    ) {
                        items(uiState.images) {
                            ImageCard(imageResponse = it) {
                                navController.navigate(ImageScreenViewModel.getRoute(it.date))
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = uiState.images.isEmpty()) {
            Text(text = "No images saved yet.", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCard(
    imageResponse: ImageResponse,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(5.dp)
            .aspectRatio(1f),
        onClick = { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            var heightOfBox by remember {
                mutableFloatStateOf(0f)
            }

            var shouldShowShimmer by remember {
                mutableStateOf(true)
            }

            AsyncImage(
                model = imageResponse.url,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.nasa),
                error = painterResource(id = R.drawable.nasa),
                modifier = modifier.aspectRatio(1f),
                contentScale = ContentScale.Crop,
                onLoading = { shouldShowShimmer = true },
                onError = { shouldShowShimmer = false },
                onSuccess = { shouldShowShimmer = false }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Black10
                            ),
                            startY = heightOfBox * 0.7f
                        )
                    )
                    .onSizeChanged {
                        heightOfBox = it.height.toFloat()
                    },
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = imageResponse.date.getHumanReadableTime(),
                    modifier = Modifier.padding(start = 5.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onClick() }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Past Images", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}