package com.example.app_retrofit.data.remote

import com.example.appmusic.api.RetrofitClient

class ApiUtils {

    val BASE_URL = "https://api.spotify.com/v1/"

    fun getAPIService(): APIService {
        return RetrofitClient.getClient(BASE_URL)!!.create(APIService::class.java)
    }
}