package com.example.newsreader.helper.retrofit.models.articles

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val Id: Int,
    val Name: String
) : Parcelable