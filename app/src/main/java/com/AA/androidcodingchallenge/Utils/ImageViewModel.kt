package com.AA.androidcodingchallenge.Utils

import androidx.compose.foundation.lazy.LazyListState
import com.AA.androidcodingchallenge.BuildConfig
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AA.androidcodingchallenge.Models.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

class ImageViewModel: ViewModel() {

    val listState = LazyListState()
    var loading by mutableStateOf(true)
    var imageList by mutableStateOf(listOf<ImageItem>())
    var searchText by mutableStateOf("")
    var selectedItemId by mutableStateOf("0")
    var noConnection by mutableStateOf(false)
    var searchHistory by mutableStateOf(ArrayDeque<String>())


    init {
        viewModelScope.launch {
            parseJSON("fruits")
            loading = false
        }
    }

    suspend fun scrollToTop() {
        listState.scrollToItem(0)
    }

    suspend fun parseJSON(query: String) {
        val images = mutableListOf<ImageItem>()

        val url = "https://pixabay.com/api/?key=${BuildConfig.PIXABAY_API_KEY}&q=$query&image_type=photo"
        val response = fetchData(url)
        val jsonArray = JSONObject(response).getJSONArray("hits")

        loading = true

        for (i in 0..< jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val id = jsonObject.getString("id")
            val imageUrl = jsonObject.getString("previewURL")
            val username = jsonObject.getString("user")
            val tags = jsonObject.getString("tags")
            val likes = jsonObject.getString("likes")
            val downloads = jsonObject.getString("downloads")
            val comments = jsonObject.getString("comments")
            val previewWidth = jsonObject.getString("previewWidth")
            val previewHeight = jsonObject.getString("previewHeight")
            val largeImageUrl = jsonObject.getString("largeImageURL")
            val largeImageWidth = jsonObject.getString("imageWidth")
            val largeImageHeight = jsonObject.getString("imageHeight")

            val tagsList = tags.split(",").map { it.trim() }
            val tagsArray = mutableListOf<String>()

            tagsList.forEach {
                tagsArray.add(it)
            }

            val item = ImageItem(
                id = id,
                previewUrl = imageUrl,
                username = username,
                tags = tagsArray,
                likes = likes.toInt(),
                downloads = downloads.toInt(),
                comments = comments.toInt(),
                previewWidth = previewWidth.toInt(),
                previewHeight = previewHeight.toInt(),
                largeImageUrl = largeImageUrl,
                largeImageWidth = largeImageWidth.toInt(),
                largeImageHeight = largeImageHeight.toInt()
            )

            images.add(item)
        }

        imageList = images

        loading = false
    }

    private suspend fun fetchData(urlString: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val stringBuilder = StringBuilder()

            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }

            reader.close()
            connection.disconnect()

            if(noConnection) noConnection = false

            stringBuilder.toString()
        } catch (e: UnknownHostException) {
            noConnection = true
            "{hits:[]}"
        }
    }
}