package com.example.newsreader.helper.retrofit.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.newsreader.helper.paging.ArticlesPagingSource
import com.example.newsreader.helper.paging.FavoritesPagingSource
import com.example.newsreader.helper.retrofit.RetrofitInstance
import com.example.newsreader.helper.retrofit.models.user.ResponseRegister
import com.example.newsreader.helper.retrofit.models.user.User
import com.example.newsreader.helper.utils.HelperUtils.TAG
import com.example.newsreader.helper.utils.SharedPrefUtils
import kotlinx.coroutines.launch

class DataViewModel(application: Application) : AndroidViewModel(application) {

    private var sharedPrefUtils = SharedPrefUtils(application)

    val pagingDataArticles = Pager(config = PagingConfig(pageSize = 20), pagingSourceFactory = { ArticlesPagingSource(RetrofitInstance.clientApi) }).flow.cachedIn(viewModelScope)
    val pagingDataFavorites = Pager(config = PagingConfig(pageSize = 20), pagingSourceFactory = { FavoritesPagingSource(RetrofitInstance.clientApi, sharedPrefUtils) }).flow.cachedIn(viewModelScope)
    val refreshDataArticles = MutableLiveData(false)
    val refreshDataFavorites = MutableLiveData(false)

    private val _registerLiveData = MutableLiveData<ResponseRegister?>()
    val registerLiveData: LiveData<ResponseRegister?> get() = _registerLiveData

    private val _loginLiveData = MutableLiveData<Boolean?>()
    val loginLiveData: LiveData<Boolean?> get() = _loginLiveData

    /* -------------------------------- BottomNavigation -------------------------------- */

    val showBottomBar = MutableLiveData<Boolean>()

    /* -------------------------------- Registration -------------------------------- */

    fun register(username: String, password: String) {
        val user = User(username, password)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.clientApi.register(user)
                if (response.isSuccessful) {
                    Log.d(TAG, "register: ${response.body()?.Message}")
                    response.body()?.let {
                        _registerLiveData.value = it
                    }
                } else {
                    Log.e(TAG, "register: Failure: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "register: Exception: ", e)
            }
        }
    }

    fun login(sharedPrefUtils: SharedPrefUtils, username: String, password: String) {
        val user = User(username, password)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.clientApi.login(user)
                Log.d(TAG, "login: ${response.body()?.AuthToken}")
                if (response.isSuccessful) {
                    _loginLiveData.value = response.body()?.AuthToken != null
                    response.body()?.let {
                        sharedPrefUtils.token = it.AuthToken
                        sharedPrefUtils.userName = username
                    }
                } else {
                    _loginLiveData.value = false
                    Log.e(TAG, "login: Failure: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                _loginLiveData.value = false
                Log.e(TAG, "login: Exception: ", e)
            }
        }
    }

    fun resetRegister() {
        _registerLiveData.value = null
    }

    fun resetLogin() {
        _loginLiveData.value = null
    }

    /* -------------------------------- Favourites -------------------------------- */

    fun setFavorite(articleId: Int, callback: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                if (sharedPrefUtils.token == null) {
                    callback.invoke(false, "Login required")
                    return@launch
                }
                RetrofitInstance.clientApi.setFavourite(sharedPrefUtils.token!!, articleId)
                callback.invoke(true, "Saved")
            } catch (e: Exception) {
                callback.invoke(false, e.message.toString())
                Log.e(TAG, "setFavorite: Exception: ", e)
            }
        }
    }

    fun deleteFavorite(articleId: Int, callback: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                if (sharedPrefUtils.token == null) {
                    callback.invoke(false, "Login required")
                    return@launch
                }
                RetrofitInstance.clientApi.deleteFavourite(sharedPrefUtils.token!!, articleId)
                callback.invoke(true, "Removed")
            } catch (e: Exception) {
                callback.invoke(false, e.message.toString())
                Log.e(TAG, "deleteFavorite: Exception: ", e)
            }
        }
    }

}