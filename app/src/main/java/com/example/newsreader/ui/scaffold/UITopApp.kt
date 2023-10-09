package com.example.newsreader.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.newsreader.R
import com.example.newsreader.helper.navigation.destinations.NavItemsDashboard
import com.example.newsreader.helper.retrofit.viewModels.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UITopApp(navController: NavHostController, viewModel: DataViewModel, showSheet: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.toolbar_title)) },
        actions = {
            IconButton(onClick = {
                if (navController.currentDestination?.route == NavItemsDashboard.Dashboard.route) {
                    viewModel.refreshDataArticles.value = true
                } else {
                    viewModel.refreshDataFavorites.value = true
                }
            }) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null
                )
            }
            IconButton(onClick = { showSheet.invoke() }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null
                )
            }
        }
    )
}