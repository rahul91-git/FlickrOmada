package com.example.flickrapp

import android.app.Application
import com.example.flickpics.api.FlickrApi
import com.example.flickpics.api.FlickrService
import com.example.flickpics.ui.viewmodel.PhotoViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class App : Application() {
    lateinit var photoViewModel: PhotoViewModel

    @OptIn(ExperimentalSerializationApi::class)
    override fun onCreate() {
        super.onCreate()
        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
        val api = retrofit.create(FlickrApi::class.java)
        val repository = FlickrService(api)
        photoViewModel = PhotoViewModel(repository)
    }
}
