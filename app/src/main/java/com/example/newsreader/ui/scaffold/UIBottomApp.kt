package com.example.newsreader.ui.scaffold

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.newsreader.helper.navigation.destinations.NavItemsDashboard
import com.example.newsreader.helper.utils.HelperUtils.TAG
import com.example.newsreader.helper.utils.navigateScreen

@Composable
fun UIBottomApp(navController: NavHostController) {
    val items = listOf(
        NavItemsDashboard.Dashboard,
        NavItemsDashboard.Favorite,
    )
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                label = {
                    Text(text = item.title)
                },
                icon = {
                    Icon(
                        imageVector = when (currentRoute == item.route) {
                            true -> item.selectedIcon
                            false -> item.unSelectedIcon
                        },
                        contentDescription = item.title
                    )
                },
                onClick = {
                    navigateScreen(navController, item.route)
                },
            )
        }
    }
}
