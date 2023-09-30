package com.example.nasa.ui.features.history

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasa.data.Repository
import com.example.nasa.data.models.ImageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {
    private val _uiState = MutableStateFlow(HistoryScreenUiState())
    val uiState: StateFlow<HistoryScreenUiState>
        get() = _uiState.asStateFlow()

    private fun getAllImagesFromDb() {
        _uiState.value = uiState.value.copy(
            isLoading = true,
            userMessage = ""
        )

        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            _uiState.value = uiState.value.copy(
                isLoading = false,
                userMessage = "Something went wrong"
            )
        }) {
            repository.getAllImagesFromDb().collect {
                _uiState.value  = uiState.value.copy(
                    isLoading = false,
                    userMessage = "",
                    images = it.toMutableStateList()
                )
            }
        }
    }

    fun onEvent(uiEvent: HistoryScreenUiEvent) {
        when(uiEvent) {
            HistoryScreenUiEvent.GetAllImagesFromDb -> {
                getAllImagesFromDb()
            }
        }
    }

    companion object {
        private const val BASE_ROUTE = "history_screen"

        fun getRoute() = BASE_ROUTE
    }
}

sealed interface HistoryScreenUiEvent{
    data object GetAllImagesFromDb: HistoryScreenUiEvent
}

data class HistoryScreenUiState(
    val isLoading: Boolean = false,
    val images: SnapshotStateList<ImageResponse> = mutableStateListOf(),
    val userMessage: String = ""
)