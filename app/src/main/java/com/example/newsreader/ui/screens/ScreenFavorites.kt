package com.example.newsreader.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.newsreader.R
import com.example.newsreader.helper.navigation.destinations.NavItems
import com.example.newsreader.helper.retrofit.models.articles.Result
import com.example.newsreader.helper.retrofit.viewModels.DataViewModel
import com.example.newsreader.helper.utils.navigateScreen
import com.google.gson.Gson
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun ScreenFavorites(parentNavController: NavHostController, viewModel: DataViewModel) {

    val pagingDataState = viewModel.pagingDataFavorites.collectAsLazyPagingItems()

    when (pagingDataState.loadState.refresh) {
        is LoadState.Loading -> PlaceHolderFavorites()
        is LoadState.Error -> ErrorStateViewFavorites(modifier = Modifier.fillMaxSize()) { pagingDataState.retry() }
        is LoadState.NotLoading -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                content = {
                    items(pagingDataState.itemCount, key = {
                        pagingDataState[it]?.Id ?: it
                    }) {
                        val item = pagingDataState.itemSnapshotList.items[it]
                        CardItemFavorites(result = item, parentNavController = parentNavController)
                    }
                    if (pagingDataState.loadState.append is LoadState.Loading) {
                        item { LoadingUI() }
                    }
                    if (pagingDataState.loadState.append is LoadState.Error) {
                        item { ErrorStateView(modifier = Modifier.fillMaxWidth()) { pagingDataState.retry() } }
                    }
                    if (pagingDataState.itemCount == 0) {
                        item { EmptyViewFavorites() }
                    }
                })
        }
    }

    viewModel.refreshDataFavorites.observe(LocalLifecycleOwner.current) {
        if (it) {
            pagingDataState.refresh()
            viewModel.refreshDataFavorites.value = false
        }
    }
}

@Composable
fun ErrorStateViewFavorites(modifier: Modifier, retryAction: () -> Unit) {
    val resources = LocalContext.current.resources
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = resources.getString(R.string.something_went_wrong), fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { retryAction() },
        ) {
            Text(text = resources.getString(R.string.retry), color = Color.White)
        }
    }
}

@Composable
fun PlaceHolderFavorites() {
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        repeat(8) {
            PlaceHolderCardItemFavorites()
        }
    }
}

@Composable
fun PlaceHolderCardItemFavorites() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row {
            Column(Modifier.weight(1F)) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                        .shimmer(shimmerInstance)
                        .background(Color.Gray),
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                    .weight(3F)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .shimmer(shimmerInstance)
                        .background(Color.Gray)
                )
            }
        }
    }
}


@Composable
fun LoadingUIFavorites() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyViewFavorites() {
    val resources = LocalContext.current.resources
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = resources.getString(R.string.no_favorite_found), fontSize = 18.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardItemFavorites(result: Result, parentNavController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        onClick = {
            val json = Gson().toJson(result, Result::class.java)
            navigateScreen(parentNavController, "${NavItems.DetailPage.route}/${Uri.encode(json)}")
        }
    ) {
        Row {
            Column(Modifier.weight(1F)) {
                AsyncImage(
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp),
                    model = result.Image,
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                    .weight(3F)
            ) {
                Column {
                    Text(
                        text = result.Title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(4.dp)
                ) {
                    AnimatedVisibility(visible = result.IsLiked) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_fav),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}