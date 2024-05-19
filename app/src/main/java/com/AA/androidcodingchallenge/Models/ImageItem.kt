package com.AA.androidcodingchallenge.Models

data class ImageItem (
    val id: String,
    val previewUrl: String,
    val username: String,
    val tags: List<String>,
    val likes: Int,
    val downloads: Int,
    val comments: Int,
    val previewWidth: Int,
    val previewHeight: Int,
    val largeImageUrl: String,
    val largeImageWidth: Int,
    val largeImageHeight: Int
)