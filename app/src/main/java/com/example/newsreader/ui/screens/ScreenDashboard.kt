package com.example.newsreader.ui.screens

import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.newsreader.helper.navigation.graphs.NavigationGraphHome
import com.example.newsreader.helper.retrofit.viewModels.DataViewModel
import com.example.newsreader.helper.utils.HelperUtils
import com.example.newsreader.helper.utils.SharedPrefUtils
import com.example.newsreader.ui.scaffold.UIBottomApp
import com.example.newsreader.ui.scaffold.UITopApp
import com.example.newsreader.ui.sheets.BottomSheetProfile

@Composable
fun ScreenDashboard(parentNavController: NavHostController, viewModel: DataViewModel) {

    val sharedPrefUtils = SharedPrefUtils(LocalContext.current)

    val navController = rememberNavController()
    var showSheet by remember { mutableStateOf(false) }
    var showBottomApp by remember { mutableStateOf(!sharedPrefUtils.token.isNullOrEmpty()) }

    viewModel.showBottomBar.observe(LocalLifecycleOwner.current) {
        showBottomApp = it
    }

    if (showSheet) {
        BottomSheetProfile(viewModel) {
            showSheet = false
        }
    }

    Scaffold(
        topBar = { UITopApp(navController, viewModel) { showSheet = true } },
        bottomBar = {
            if (showBottomApp) UIBottomApp(navController)
        }
    )
    { innerPadding ->
        Log.d(HelperUtils.TAG, "onCreate: $innerPadding")
        NavigationGraphHome(parentNavController, navController = navController, innerPadding = innerPadding, viewModel = viewModel)
    }
}