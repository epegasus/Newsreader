package com.example.newsreader.helper.navigation.destinations

sealed class NavItems(val route: String) {
    data object Dashboard : NavItems(route = "dashboard")
    data object DetailPage : NavItems(route = "screen_detail")
}