package com.AA.androidcodingchallenge.Utils

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.AA.androidcodingchallenge.Models.ImageItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Composable for Image Tags
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun tags(
    modifier: Modifier = Modifier,
    viewModel: ImageViewModel = ImageViewModel(),
    item: ImageItem
) {
    val tagsArray = item.tags

    val coroutineScope = rememberCoroutineScope()

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
    ) {
        tagsArray.forEach {
            AssistChip(
                modifier = Modifier
                    .padding(end = 4.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    coroutineScope.launch {
                        val searchQueryArray = it.trim().split(' ')

                        val searchQuery = buildString {
                            searchQueryArray.forEach {
                                append("+$it")
                            }
                        }

                        if(viewModel.searchHistory.isEmpty() || viewModel.searchHistory.first() != searchQuery) {
                            viewModel.searchHistory.addFirst(searchQuery)
                            viewModel.parseJSON(searchQuery)
                        }

                        viewModel.scrollToTop()
                        viewModel.searchText = it
                    }
                },
                label = {
                    Text(text = it)
                })
        }
    }
}