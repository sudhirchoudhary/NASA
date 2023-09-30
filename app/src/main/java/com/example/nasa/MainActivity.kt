package com.example.nasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nasa.ui.features.history.HistoryScreen
import com.example.nasa.ui.features.history.HistoryViewModel
import com.example.nasa.ui.features.imageofday.ImageScreen
import com.example.nasa.ui.features.imageofday.ImageScreenViewModel
import com.example.nasa.ui.theme.NASATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NASATheme(isStatusBarTransparent = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // We are using Navigation component to handle the screen navigation
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = ImageScreenViewModel.getRoute("0")) {
                        composable(
                            route = "${ImageScreenViewModel.BASE_ROUTE}/{imageDate}",
                            arguments = listOf(
                                navArgument("imageDate") {
                                    this.type = NavType.StringType
                                    this.defaultValue = ""
                                }
                            )
                        ) {
                            val imageScreenViewModel: ImageScreenViewModel = hiltViewModel()
                            val uiState = imageScreenViewModel.uiState.collectAsState()
                            ImageScreen(navController, uiState.value, onEvent = imageScreenViewModel::onEvent)
                        }

                        composable(route = HistoryViewModel.getRoute()) {
                            val historyViewModel: HistoryViewModel = hiltViewModel()
                            val uiState = historyViewModel.uiState.collectAsState()
                            HistoryScreen(uiState = uiState.value, navController = navController, onEvent = historyViewModel::onEvent)
                        }
                    }
                }
            }
        }
    }
}