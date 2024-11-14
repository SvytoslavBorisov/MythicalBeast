package ru.openbiz64.mythicalbeast.retrofit

import retrofit2.Response
import retrofit2.http.GET

interface RetrofitAPI {
    @GET("/myth/articals_web.json")
    suspend fun getArticleList(): Response<ArticleList>
}