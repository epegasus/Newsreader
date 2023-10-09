package com.example.newsreader.helper.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsreader.helper.retrofit.interfaces.ClientApi
import com.example.newsreader.helper.retrofit.models.articles.Result
import com.example.newsreader.helper.utils.HelperUtils.TAG
import com.example.newsreader.helper.utils.SharedPrefUtils

class FavoritesPagingSource(private val api: ClientApi, private val sharedPrefUtils: SharedPrefUtils) : PagingSource<Int, Result>() {

    private var nextId: Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
        try {
            if (sharedPrefUtils.token == null) {
                Log.e(TAG, "FavouritesPagingSource: load: ", NullPointerException("Token is null"))
            }
            val nextPage = params.key ?: 1
            val response = nextId?.let {
                api.getPaginatedFavourites(sharedPrefUtils.token!!, it)
            } ?: kotlin.run {
                api.getFavourites(sharedPrefUtils.token!!)
            }
            val productList = response.body()?.Results ?: emptyList()
            nextId = response.body()?.NextId

            Log.d(TAG, "load: Count: $nextId, NextPage: $nextPage, ListSize: ${productList.size}")

            return LoadResult.Page(
                data = productList,
                prevKey = if (nextPage == 1) null else nextPage.minus(1),
                nextKey = if (productList.isEmpty()) null else nextPage.plus(1),
            )
        } catch (ex: Exception) {
            Log.e(TAG, "FavouritesPagingSource: load: ", ex)
            return LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Result>): Int? {
        val temp = state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
        return temp
    }
}