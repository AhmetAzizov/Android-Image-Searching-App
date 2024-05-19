package com.AA.androidcodingchallenge.Utils

suspend fun handleSearchHistory(query: String, viewModel: ImageViewModel) {
    val searchQueryArray = query.trim().split(' ')

    val searchQuery = buildString {
        searchQueryArray.forEach {
            append("+$it")
        }
    }

    val array = viewModel.searchHistory

    if (array.isEmpty()) {
        viewModel.searchHistory.addFirst(searchQuery)
        return
    } else if(array.first() == searchQuery.trim()) {
        return
    }

    for (index in array.indices) {
        if(searchQuery == array[index]) {
            array.removeAt(index)
            break
        }
    }

    array.addFirst(searchQuery)
    viewModel.parseJSON(searchQuery)
    viewModel.searchHistory = array
}