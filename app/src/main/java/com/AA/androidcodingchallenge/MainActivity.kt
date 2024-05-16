package com.AA.androidcodingchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.AA.androidcodingchallenge.ui.theme.AndroidCodingChallengeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel by viewModels<ImageViewModel>()

            if(!viewModel.hasStarted) {
                LaunchedEffect(Unit) {
                    viewModel.getData("fruits")
                    viewModel.loading = false
                }

                viewModel.hasStarted = true;
            }

            AndroidCodingChallengeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    Navigation(viewModel = viewModel)
                }
            }
        }
    }
}


@Composable
fun MainScreen(
    navController: NavController,
    viewModel: ImageViewModel
) {
    if(viewModel.loading) {
        CircularProgressIndicator()
        return
    }

    Column {
        searchBar(viewModel = viewModel)
        itemsList(
            viewModel = viewModel,
            navController = navController
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun searchBar(
    viewModel: ImageViewModel
) {
    var active by remember {mutableStateOf(false)}

        SearchBar(
            modifier = Modifier
                .fillMaxWidth(),
            query = viewModel.searchText,
            onQueryChange = {
                viewModel.searchText = it
            },
            onSearch = {
                val searchQueryArray = it.split(' ')
                var searchQuery = ""

                searchQueryArray.forEach {
                    searchQuery += "+$it"
                }
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getData(searchQuery)
                    active = false
                }
            },
            active = active,
            onActiveChange = {
                active = it
            },
            placeholder = {
                Text(text = "Search an Image")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            }
        ) {
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun itemCard(
    modifier: Modifier = Modifier,
    item: ImageItem,
    image: Painter,
    username: String,
    onClick: () -> Unit
) {
    var aspectRatio: Float

    if(item.previewWidth > item.previewHeight) {
        aspectRatio = (item.previewWidth.toFloat()/item.previewHeight)
    } else {
        aspectRatio = (item.previewHeight.toFloat()/item.previewWidth)
    }

    Card(
        modifier = modifier
            .padding(top = 24.dp, bottom = 8.dp, start = 18.dp, end = 18.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                spotColor = colorScheme.onSurface,
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.background
        ),
        onClick = { onClick() },
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = image, contentDescription = "image",
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(aspectRatio),
                contentScale = ContentScale.Crop
            )
            Text(
                text = username,
                Modifier
                    .padding(top = 6.dp, start = 15.dp),
                color = colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.W800
            )
            tags(
                modifier = Modifier
                    .padding(start = 15.dp),
                item = item
            )
        }
    }
}
@Composable
fun itemsList(
    modifier: Modifier = Modifier,
    viewModel: ImageViewModel,
    navController: NavController
) {
    if(viewModel.imageList.isEmpty()) {
        Text(text = "There are no images matching your query")
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(viewModel.imageList) {
            itemCard(
                image = rememberAsyncImagePainter(it.imageUrl),
                username = it.username,
                item = it
            ) {
                navController.navigate(Screen.DetailsScreen.route + "/${it.id}")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun tags(
    modifier: Modifier = Modifier,
    item: ImageItem
) {
    val itemsArray = item.tags

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
    ) {
        itemsArray.forEach {
            AssistChip(
                modifier = Modifier
                    .padding(end = 4.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = {},
                label = {
                    Text(text = it)
                })
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidCodingChallengeTheme {
    }
}