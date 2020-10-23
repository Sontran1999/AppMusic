package com.example.app_retrofit.data.remote


import com.example.appmusic.FileJson
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @GET("episodes?ids=77o6BIVlYM3msb4MMIL1jH%2C0Q86acNRm6V9GYx55SXKwf&market=ES")
    fun getAll(): Call<FileJson>
}