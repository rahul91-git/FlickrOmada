package com.example.flickpics.api

import com.example.flickpics.models.PhotoUiModel
import com.example.flickpics.models.toUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service class to interact with the Flickr API.
 *
 * @param api The [FlickrApi] instance for making network requests.
 * @param apiKey The API key for authenticating requests to the Flickr API.
 */
class FlickrService(
    private val api: FlickrApi,
    private val apiKey: String
) {
    suspend fun getPhotos(query: String?, page: Int): List<PhotoUiModel> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = if (query.isNullOrBlank()) {
                    api.getRecentPhotos(apiKey, page)
                } else {
                    api.searchPhotos(apiKey, query)
                }
                response.photos.photo.map { it.toUiModel() }
            } catch (ex: Exception) {
                // Handle the exception and log it if necessary
                throw ex
            }
        }
}