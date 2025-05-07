package com.example.flickpics

import app.cash.turbine.test
import com.example.flickpics.api.FlickrService
import com.example.flickpics.models.PhotoUiModel
import com.example.flickpics.ui.viewmodel.PhotoViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoViewModelTest {

    private lateinit var viewModel: PhotoViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `fetchPhotos should update uiState with photos on success`() = runTest {
        val mockPhotos = listOf(
            PhotoUiModel("Title1", "Url1"),
            PhotoUiModel("Title2", "Url2")
        )
        val repository = mockk<FlickrService> {
            coEvery { getPhotos(null, 1) } returns mockPhotos
        }

        viewModel = PhotoViewModel(repository)
        viewModel.fetchPhotos(reset = true)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)

            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)

            val successState = awaitItem()
            assertEquals(mockPhotos, successState.photos)
            assertEquals(false, successState.isLoading)
            assertEquals(2, successState.page)
        }
    }

    @Test
    fun `fetchPhotos should update uiState with error on failure`() = runTest {
        val errorMessage = "Network error"
        val repository = mockk<FlickrService> {
            coEvery { getPhotos(null, 1) } throws  RuntimeException(errorMessage)
        }

        viewModel = PhotoViewModel(repository)
        viewModel.fetchPhotos(reset = true)

        viewModel.uiState.test {
            skipItems(1) // Skip initial state

            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)

            val errorState = awaitItem()
            assertEquals(false, errorState.isLoading)
            assertEquals(errorMessage, errorState.error)
        }
    }
}