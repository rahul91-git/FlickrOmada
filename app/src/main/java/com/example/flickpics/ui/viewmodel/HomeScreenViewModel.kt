package com.example.flickpics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpics.api.FlickrService
import com.example.flickpics.models.PhotoUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the state of the Flickr photo search screen.
 *
 * @param repository The [FlickrService] instance for fetching photos.
 */
class PhotoViewModel(private val repository: FlickrService) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> get() = _uiState

    fun fetchPhotos(reset: Boolean = false) {
        viewModelScope.launch {
            val current = _uiState.value
            if (current.isLoading) return@launch

            _uiState.value = current.copy(isLoading = true, error = null)

            val nextPage = if (reset) 1 else current.page
            try {
                val photos = repository.getPhotos(current.query, nextPage)
                val updatedList = if (reset) photos else current.photos + photos
                _uiState.value = current.copy(
                    photos = updatedList,
                    isLoading = false,
                    page = nextPage + 1
                )
            } catch (e: Exception) {
                _uiState.value = current.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun searchPhotoByQuery(query: String?) {
        _uiState.value = UiState(query = query)
        fetchPhotos(reset = true)
    }
}

data class UiState(
    val photos: List<PhotoUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val query: String? = null
)