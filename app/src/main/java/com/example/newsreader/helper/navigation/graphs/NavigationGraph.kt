package com.example.newsreader.helper.navigation.graphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.newsreader.helper.navigation.destinations.NavItems
import com.example.newsreader.helper.retrofit.models.articles.Result
import com.example.newsreader.helper.retrofit.viewModels.DataViewModel
import com.example.newsreader.helper.utils.HelperUtils.TAG
import com.example.newsreader.ui.screens.ScreenArticleDetail
import com.example.newsreader.ui.screens.ScreenDashboard
import com.google.gson.Gson

@Composable
fun NavigationGraph(navController: NavHostController, viewModel: DataViewModel) {
    NavHost(
        navController = navController,
        startDestination = NavItems.Dashboard.route
    ) {
        composable(NavItems.Dashboard.route) {
            Log.i(TAG, "navigating: ScreenDashboard")
            ScreenDashboard(navController, viewModel)
        }
        composable("${NavItems.DetailPage.route}/{encodedString}") { backStackEntry ->
            Log.i(TAG, "navigating: ScreenArticleDetail")
            val encodedString = backStackEntry.arguments?.getString("encodedString")
            val result = Gson().fromJson(encodedString, Result::class.java)
            ScreenArticleDetail(navController, viewModel, result)
        }
    }
}