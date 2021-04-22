package com.example.appmusic.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitClient {

    companion object {
        var interceptor = fun(chain: Interceptor.Chain): Response = chain.run {
            proceed(
                request()
                    .newBuilder()
                    .addHeader("Accept","application/json")
                    .addHeader("Content-type","application/json")
                    .addHeader("Authorization","Bearer BQDfQHlYVQzZquu3NOm2W4gY_HMUhUX5eodjFH_VYOtJMwHEXwcYhPl83_QKAPmR7fvGnrbo8PfyDFd9KKG5twn7MmFcyXE1l95ha_xYNco33t1yYHfRGBuSZpSNaNhOzCuowHav1Za5-P_m3oq3R9txzgkMuwNmJewaoTeIzvPGXH_3DOHoZ6JKSDd2nn-jvAya4S71qr7dbDRbV62kf4bCpb0oLorgfzjkKT0PSWx4wbz7PvUlIMHR0CXcJ_hYUpL39XjLmenJ2yrmRH2pFDfnuCeKKSF7Q-WWVQ9O25wJ")
                    .build()
            )
        }

        private val builder = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .writeTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(interceptor)
            .build()
        private var retrofit: Retrofit? = null
        fun getClient(baseUrl: String?): Retrofit? {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .client(builder)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

    }

}
