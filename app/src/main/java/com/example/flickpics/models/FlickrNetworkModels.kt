package com.example.flickpics.models

import kotlinx.serialization.Serializable

@Serializable
data class PhotoResponse(
    val photos: Photos,
    val stat: String
)

@Serializable
data class Photos(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<Photo>
)

@Serializable
data class Photo(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val title: String,
    val ispublic: Int,
    val isfriend: Int,
    val isfamily: Int
) {
    fun getImageUrl(): String =
        "https://live.staticflickr.com/${server}/${id}_${secret}.jpg"
}

fun Photo.toUiModel(): PhotoUiModel {
    return PhotoUiModel(
        title = title,
        imageUrl = getImageUrl()
    )
}
