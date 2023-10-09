package com.example.newsreader.helper.retrofit.interfaces

import com.example.newsreader.helper.retrofit.models.articles.ResultArticles
import com.example.newsreader.helper.retrofit.models.user.ResponseLogin
import com.example.newsreader.helper.retrofit.models.user.ResponseRegister
import com.example.newsreader.helper.retrofit.models.user.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClientApi {

    @GET("articles/")
    suspend fun getArticles(): Response<ResultArticles>

    @GET("articles/{id}?count=20")
    suspend fun getPaginatedArticles(@Path("id") nextId: Int): Response<ResultArticles>

    @POST("Users/register")
    suspend fun register(@Body user: User): Response<ResponseRegister>

    @POST("Users/login")
    suspend fun login(@Body user: User): Response<ResponseLogin>

    @GET("articles/liked")
    suspend fun getFavourites(@Header("x-authtoken") token: String): Response<ResultArticles>

    @GET("articles/{id}/liked?count=20")
    suspend fun getPaginatedFavourites(@Header("x-authtoken") token: String, @Path("id") nextId: Int): Response<ResultArticles>

    @PUT("articles/{id}/like")
    suspend fun setFavourite(@Header("x-authtoken") token: String, @Path("id") articleId: Int)

    @DELETE("articles/{id}/like")
    suspend fun deleteFavourite(@Header("x-authtoken") token: String, @Path("id") articleId: Int)
}