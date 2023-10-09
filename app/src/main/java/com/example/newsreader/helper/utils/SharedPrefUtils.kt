package com.example.newsreader.helper.utils

import android.content.Context

class SharedPrefUtils(context: Context) {

    private val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val editor by lazy { sharedPref.edit() }

    var token: String?
        get() = sharedPref.getString("token", null)
        set(value) {
            editor.putString("token", value)
            editor.apply()
        }

    var userName: String?
        get() = sharedPref.getString("username", null)
        set(value) {
            editor.putString("username", value)
            editor.apply()
        }
}