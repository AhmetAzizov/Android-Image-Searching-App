package com.AA.androidcodingchallenge.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.AA.androidcodingchallenge.Models.ImageItem
import com.AA.androidcodingchallenge.Utils.ImageViewModel
import com.AA.androidcodingchallenge.Utils.tags


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun detailsScreen(
    id: String?,
    navController: NavController,
    viewModel: ImageViewModel
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Details")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }

    ) {
        content(
            modifier = Modifier
                .padding(it),
            id = id,
            viewModel = viewModel
        )
    }
}

@Composable
fun content(
    id: String?,
    viewModel: ImageViewModel,
    modifier: Modifier = Modifier
) {
    if (id == null || id == "0") {
        Text(text = "ID not found")
        return
    }

    val item: ImageItem = viewModel.imageList.find { it.id == id } ?: return Text(text = "ID not found")

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        largeImage(item = item)
        
        tags(
            item = item,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        Divider()

        detail(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            icon = Icons.Default.Person,
            text = item.username)

        Divider()

        detail(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            icon = Icons.Default.ThumbUp,
            text = item.likes.toString())

        Divider()

        detail(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            icon = Icons.Default.Download,
            text = item.likes.toString())

        Divider()

        detail(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            icon = Icons.Default.Comment,
            text = item.comments.toString())
    }
}

@Composable
fun detail(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(20.dp),
            imageVector = icon,
            contentDescription = text
        )

        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            fontSize = 20.sp,
            text = text
        )
    }
}


@Composable
fun largeImage(
    modifier: Modifier = Modifier,
    item: ImageItem
) {
    var aspectRatio = (item.previewWidth.toFloat()/item.previewHeight)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 16.dp)
            .aspectRatio(aspectRatio),
        shape = RoundedCornerShape(30.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.largeImageUrl)
                .crossfade(400).
                build(),
            contentDescription = "image",
            contentScale = ContentScale.Crop
        )
    }
}