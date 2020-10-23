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
                    .addHeader("Authorization","Bearer BQBrc6megpRhIXYkMZtTdTg6donMFvYi0IcKc8c5OONiCh4riZjqvllqqpkSznXt_OkpliVpjOrRnN6MppbdAyIcQwSbzU_bLxD4caxnnMnZlB2bjBNMPEh__LSUaK3V9J5kvUp7ShdP-SIl_sVnrnvAzRIXO6IbF1vFnmL0UZXd64hcZBfeyzLC76a59V8plbrHcLKAziVQI4qWgECP8Q3F_1r3vsVasvjuWnxNIjUQum5H-xFH9z1ZP4MSYvXRyXs6Jt9Tf8jRMi0H5AtJQeRZ6qnvB1KYpSyoPdg")
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
