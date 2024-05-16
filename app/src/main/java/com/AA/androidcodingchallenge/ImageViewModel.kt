package com.AA.androidcodingchallenge

import com.AA.androidcodingchallenge.BuildConfig
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ImageViewModel() : ViewModel() {

    private val TAG = "ImageViewModel"
    var hasStarted by mutableStateOf(false)
    var loading by mutableStateOf(true)
    var imageList by mutableStateOf(listOf<ImageItem>())
    var searchText by mutableStateOf("")

    suspend fun getData(query: String) {
        val images = mutableListOf<ImageItem>()

        val url = "https://pixabay.com/api/?key=${BuildConfig.PIXABAY_API_KEY}&q=$query&image_type=photo"
        val response = fetchXMLData(url)
        val jsonArray = JSONObject(response).getJSONArray("hits")

        for (i in 0..< jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val id = jsonObject.getString("id")
            val imageUrl = jsonObject.getString("previewURL")
            val username = jsonObject.getString("user")
            val previewWidth = jsonObject.getString("previewWidth")
            val previewHeight = jsonObject.getString("previewHeight")
            val tags = jsonObject.getString("tags")

            val tagsList = tags.split(",").map { it.trim() }
            val tagsArray = mutableListOf<String>()

            tagsList.forEach {
                tagsArray.add(it)
            }

            val item = ImageItem(id, imageUrl, username, tagsArray, previewWidth.toInt(), previewHeight.toInt())

            images.add(item)
        }

        if(imageList != images) {
            imageList = images
        }
    }

    private suspend fun fetchXMLData(urlString: String): String = withContext(Dispatchers.IO) {
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

        stringBuilder.toString()
    }
}