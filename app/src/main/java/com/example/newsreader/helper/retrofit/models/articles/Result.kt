package com.example.newsreader.helper.retrofit.models.articles

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Result(
    val Categories: List<Category>,
    val Feed: Int,
    val Id: Int,
    val Image: String,
    val IsLiked: Boolean,
    val PublishDate: String,
    val Related: List<String>,
    val Summary: String,
    val Title: String,
    val Url: String
) : Parcelable