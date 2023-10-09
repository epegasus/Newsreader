package com.example.newsreader.helper.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.newsreader.helper.navigation.destinations.NavItemsDashboard
import com.example.newsreader.helper.retrofit.viewModels.DataViewModel
import com.example.newsreader.ui.screens.ScreenFavorites
import com.example.newsreader.ui.screens.ScreenHome

@Composable
fun NavigationGraphHome(parentNavController: NavHostController, navController: NavHostController, innerPadding: PaddingValues, viewModel: DataViewModel) {
    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = NavItemsDashboard.Dashboard.route
    ) {
        // Bottom Navigation
        composable(NavItemsDashboard.Dashboard.route) {
            ScreenHome(parentNavController, viewModel)
        }
        composable(NavItemsDashboard.Favorite.route) {
            ScreenFavorites(parentNavController, viewModel)
        }
    }
}