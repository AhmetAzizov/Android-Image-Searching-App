package com.AA.androidcodingchallenge

data class ImageItem (
    val id: String,
    val imageUrl: String,
    val username: String,
    val tags: List<String>,
    val previewWidth: Int,
    val previewHeight: Int
)