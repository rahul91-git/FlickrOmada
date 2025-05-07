package com.example.flickpics.api

import com.example.flickpics.models.PhotoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("services/rest/?method=flickr.photos.getRecent&format=json&nojsoncallback=1")
    suspend fun getRecentPhotos(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
    ): PhotoResponse

    @GET("services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1")
    suspend fun searchPhotos(
        @Query("api_key") apiKey: String,
        @Query("text") query: String,
        @Query("page") page: Int = 1,
    ): PhotoResponse
}
