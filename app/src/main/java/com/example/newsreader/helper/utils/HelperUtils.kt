package com.example.newsreader.helper.utils

import android.content.Context
import android.widget.Toast

object HelperUtils {
    const val TAG = "MyTag"

    fun showToast(context: Context, data: Any) {
        Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show()
    }
}