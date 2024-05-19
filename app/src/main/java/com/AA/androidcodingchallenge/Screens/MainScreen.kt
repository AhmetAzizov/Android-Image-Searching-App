package com.AA.androidcodingchallenge.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.AA.androidcodingchallenge.Models.ImageItem
import com.AA.androidcodingchallenge.Utils.ImageViewModel
import com.AA.androidcodingchallenge.Utils.Screen
import com.AA.androidcodingchallenge.Utils.tags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: ImageViewModel
) {
    if(viewModel.selectedItemId != "0") {
        AlertDialog(
            icon = {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info Icon")
            },
            title = {
                Text(text = "Would like to proceed to the Details Screen?")
            },
            onDismissRequest = {
                viewModel.selectedItemId = "0"
            },
            properties = DialogProperties(dismissOnBackPress = true),
            confirmButton = {
                Button(onClick = {
                    navController.navigate(route = Screen.DetailsScreen.route + "/${viewModel.selectedItemId}")
                    viewModel.selectedItemId = "0"
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.selectedItemId = "0"
                }) {
                    Text(text = "Back")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            searchBar(
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            )
        }
    ) {
        if(viewModel.noConnection) {
            noInternet()
        } else {
            if(viewModel.loading) {
                loadingScreen(
                    modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                )
            } else if(viewModel.imageList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(text = "There are no images matching your query")
                    }
                }
            else {
                itemsList(
                    viewModel = viewModel,
                    modifier = Modifier
                        .padding(it)
                )
            }
        }
    }
}

@Composable
fun loadingScreen(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        CircularProgressIndicator()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun searchBar(
    viewModel: ImageViewModel,
    modifier: Modifier = Modifier
) {
    var active by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    
    SearchBar(
        modifier = modifier
            .background(Color.Transparent),
        query = viewModel.searchText,
        onQueryChange = {
            viewModel.searchText = it
        },
        onSearch = { query ->
            enabled = false
            val searchQueryArray = query.trim().split(' ')

            val searchQuery = buildString {
                searchQueryArray.forEach {
                    append("+$it")
                }
            }

            CoroutineScope(Dispatchers.IO).launch {

                if(viewModel.searchHistory.isEmpty() || viewModel.searchHistory.first() != searchQuery.trim()) {
                    viewModel.searchHistory.addFirst(searchQuery)
                    viewModel.parseJSON(searchQuery)
                }

                viewModel.scrollToTop()
                active = false
                enabled = true
            }
        },
        active = active,
        onActiveChange = {
            active = it
        },
        enabled = enabled,
        placeholder = {
            Text(text = "Search an Image")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        trailingIcon = {
            if(!enabled) {
                loadingScreen(
                    contentAlignment = Alignment.CenterEnd
                )
            } else if(active) {
                Icon(
                    modifier = Modifier.clickable {
                        if(viewModel.searchText.isNotEmpty()) {
                            viewModel.searchText = ""
                        } else {
                            active = false
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Search Icon"
                )
            }
        }
    ) {
        LazyColumn {
            items(viewModel.searchHistory) {
                val parsedHistoryItem = it.replace('+', ' ').trim()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .clickable {
                            enabled = false
                            viewModel.searchText = parsedHistoryItem
                            CoroutineScope(Dispatchers.IO).launch {
                                if (viewModel.searchHistory.first() != it) {
                                    viewModel.parseJSON(it)
                                }
                                viewModel.scrollToTop()
                                enabled = true
                                active = false
                            }
                        },
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 10.dp),
                        imageVector = Icons.Default.History,
                        contentDescription = "History"
                    )
                    Text(text = parsedHistoryItem)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun itemCard(
    modifier: Modifier = Modifier,
    viewModel: ImageViewModel,
    item: ImageItem,
    username: String,
    onClick: () -> Unit
) {
    var aspectRatio = (item.previewWidth.toFloat()/item.previewHeight)

    if(aspectRatio < .8F) aspectRatio = .8F

    Card(
        modifier = modifier
            .padding(top = 24.dp, bottom = 8.dp, start = 18.dp, end = 18.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                spotColor = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(24.dp)
            ),
        enabled = !viewModel.loading,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        onClick = { onClick() },
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.previewUrl)
                    .crossfade(400).
                    build(),
                contentDescription = "image",
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(aspectRatio),
                contentScale = ContentScale.Crop
            )

            Text(
                text = username,
                Modifier
                    .padding(top = 6.dp, start = 15.dp),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.W800
            )
            tags(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp),
                viewModel = viewModel,
                item = item
            )
        }
    }
}

@Composable
fun itemsList(
    modifier: Modifier = Modifier,
    viewModel: ImageViewModel
) {
    LazyColumn(
        state = viewModel.listState,
        modifier = modifier
            .fillMaxSize()
    ) {
        items(
            viewModel.imageList,
            key = {
                it.id
            }
        ) {
            itemCard(
                username = it.username,
                viewModel = viewModel,
                item = it
            ) {
                viewModel.selectedItemId = it.id
            }
        }
    }
}

@Composable
fun noInternet() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(32.dp),
                imageVector = Icons.Default.SignalWifiOff,
                contentDescription = "No Internet"
            )

            Text(text = "You have no Internet Connection")
        }
    }
}
