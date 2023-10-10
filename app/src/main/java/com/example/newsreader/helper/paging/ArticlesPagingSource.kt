package com.example.newsreader.helper.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsreader.helper.retrofit.interfaces.ClientApi
import com.example.newsreader.helper.retrofit.models.articles.Result
import com.example.newsreader.helper.utils.HelperUtils.TAG

class ArticlesPagingSource(private val api: ClientApi) : PagingSource<Int, Result>() {

    private var nextId: Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
        try {
            val nextPage = params.key ?: 1
            val response = nextId?.let {
                api.getPaginatedArticles(it)
            } ?: kotlin.run {
                api.getArticles()
            }
            val productList = response.body()?.Results ?: emptyList()
            nextId = response.body()?.NextId

            return LoadResult.Page(
                data = productList,
                prevKey = if (nextPage == 1) null else nextPage.minus(1),
                nextKey = if (productList.isEmpty()) null else nextPage.plus(1),
            )
        } catch (ex: Exception) {
            Log.e(TAG, "ArticlesPagingSource: load: ", ex)
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