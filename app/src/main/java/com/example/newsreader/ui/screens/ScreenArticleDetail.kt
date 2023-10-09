package com.example.newsreader.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.newsreader.R
import com.example.newsreader.helper.retrofit.models.articles.Result
import com.example.newsreader.helper.retrofit.viewModels.DataViewModel
import com.example.newsreader.helper.utils.HelperUtils.showToast
import com.example.newsreader.helper.utils.SharedPrefUtils

@Composable
fun ScreenArticleDetail(navController: NavHostController, viewModel: DataViewModel, result: Result) {

    val context = LocalContext.current
    val sharedPrefUtils = SharedPrefUtils(context)
    var isLiked by remember { mutableStateOf(result.IsLiked) }
    val image = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, 32.dp, 0.dp, 0.dp)
                .height(300.dp) // You can adjust the height as needed
        ) {
            AsyncImage(
                model = result.Image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .clickable {
                        navController.popBackStack()
                    }
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(
                        color = Color(0xFFDCDCDC),
                        shape = RoundedCornerShape(50)
                    )
            ) {
                Image(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(8.dp)
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = result.PublishDate,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )

                TextButton(
                    modifier = Modifier.weight(1F),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(result.Url)
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = context.getString(R.string.open_in_browser))
                }
            }
            Text(
                text = result.Title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = result.Summary,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1F)
                        .wrapContentWidth(Alignment.Start)
                ) {
                    result.Categories.forEach {
                        SuggestionChip(label = { Text(text = it.Name) }, onClick = {})
                        Spacer(modifier = Modifier.padding(2.dp))
                    }
                }

                AnimatedVisibility(visible = !sharedPrefUtils.token.isNullOrEmpty()) {
                    Image(
                        imageVector = image,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                if (!isLiked) {
                                    viewModel.setFavorite(result.Id) { success, message ->
                                        showToast(context, message)
                                        if (success) {
                                            isLiked = true
                                        }
                                    }
                                } else {
                                    viewModel.deleteFavorite(result.Id) { success, message ->
                                        showToast(context, message)
                                        if (success) {
                                            isLiked = false
                                        }
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}