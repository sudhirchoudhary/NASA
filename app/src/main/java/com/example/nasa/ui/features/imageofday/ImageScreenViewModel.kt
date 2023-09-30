package com.example.nasa.ui.features.imageofday

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nasa.data.Repository
import com.example.nasa.data.Response
import com.example.nasa.data.models.ImageResponse
import com.example.nasa.util.isNetWorkAvailable
import com.example.nasa.util.logd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageScreenViewModel @Inject constructor(
    private val app: Application,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {
    private val imageDate: String = savedStateHandle.get<String>("imageDate") ?: ""

    private val _uiState = MutableStateFlow(ImageScreenUiState())
    val uiState: StateFlow<ImageScreenUiState>
        get() = _uiState.asStateFlow()

    private var count = 1

    private fun getImageOfDay() {
        viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            val userMessage = if(!app.applicationContext.isNetWorkAvailable())
                "No internet"
            else
                "Couldn't load image"

            _uiState.value = uiState.value.copy(
                isLoading = false,
                imageResponse = ImageResponse(),
                userMessage = userMessage,
                isError = true
            )
        }) {
            _uiState.value = uiState.value.copy(
                isLoading = true,
                userMessage = "",
                isError = false
            )
            repository.getImageOfDay(count.getDate()).collect {
                logd("got result")
                when (it) {
                    is Response.Success -> {
                        _uiState.value = uiState.value.copy(
                            isLoading = false,
                            imageResponse = it.data,
                            userMessage = "",
                            isError = false
                        )
                    }

                    else -> {
                        logd("error recevied from api is ${(it as Response.Error).error}")
                        _uiState.value = uiState.value.copy(
                            isLoading = false,
                            imageResponse = ImageResponse(),
                            userMessage = "Something went wrong",
                            isError = true
                        )
                    }
                }
            }
        }
    }

    private fun getImageFromDb() {
        _uiState.value = uiState.value.copy(
            isLoading = true,
            isShowingPastImages = true,
            userMessage = "",
            isError = false
        )
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            _uiState.value = uiState.value.copy(
                isLoading = false,
                userMessage = "Couldn't load image",
                isError = true
            )
        }) {
            repository.getImageFromDB(imageDate).collect {
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    imageResponse = it,
                    userMessage = "",
                    isError = false
                )
            }
        }
    }

    fun onEvent(imageScreenUiEvent: ImageScreenUiEvent) {
        when (imageScreenUiEvent) {
            ImageScreenUiEvent.GetImageOfDay -> {
                if(imageDate.isEmpty()) {
                    getImageOfDay()
                } else {
                    getImageFromDb()
                }
            }
        }
    }

    companion object {
        const val BASE_ROUTE = "image_screen"

        /**
         * returns the route for the current screen.
         */
        fun getRoute(date: String): String {
            return "$BASE_ROUTE/$date"
        }
    }
}

fun Int.getDate(): String {
    return if(this > 9)
        "2022-03-$this"
    else
        "2022-03-0$this"
}

/**
 * closed interface for handling ui events uniformly
 */
sealed interface ImageScreenUiEvent {
    data object GetImageOfDay : ImageScreenUiEvent
}

/**
 * represents the ui state at any point
 */
data class ImageScreenUiState(
    val isLoading: Boolean = false,
    val isShowingPastImages: Boolean = false,
    val imageResponse: ImageResponse = ImageResponse(),
    val userMessage: String = "",
    val isError: Boolean = false
)