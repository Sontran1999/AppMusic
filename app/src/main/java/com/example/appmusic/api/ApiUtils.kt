package com.example.appmusic.api

class ApiUtils {

    val BASE_URL = "https://api.spotify.com/v1/"

    fun getAPIService(): APIService {
        return RetrofitClient.getClient(BASE_URL)!!.create(APIService::class.java)
    }
}