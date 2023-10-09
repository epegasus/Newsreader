package com.example.newsreader.helper.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.example.newsreader.helper.retrofit.models.articles.Result
import com.google.gson.Gson

class NavParamResult : NavType<Result>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Result? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Result {
        return Gson().fromJson(value, Result::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Result) {
        bundle.putParcelable(key, value)
    }
}