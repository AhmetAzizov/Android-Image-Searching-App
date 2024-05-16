package com.AA.androidcodingchallenge

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DetailsScreen(
    id: String?
) {
    if (id == null || id == "0") {
        Text(text = "ID not found")
        return
    }

    Text(text = id)
}