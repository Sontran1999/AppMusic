package com.example.appmusic.api

import com.example.appmusic.model.Json4Kotlin_Base
import com.example.appmusic.model.Tracks
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @GET("albums?ids=382ObEPsp2rxGrnsizN5TX%2C1A2GTWGtFfWp7KSQTwWOyo%2C2noRn2Aes5aoNVsU6iWThc&market=ES")
    fun getAll(): Call<Json4Kotlin_Base>
}