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
                    .addHeader("Authorization","Bearer BQD_BI_W9TjCwBC5YLWdpmmPBLGb2weVCvC9VaUNARc0xPC0zd8UHxAlWTP_zV6kLIKnzxsfFii1HFM4QgYag7ssriKZSi4DQgOQpwoN2UIm-zYT7ZViws765wXwDb1bUTNLrxJ9gXyceeYZvuziHsRGRYH0eViqBOX-ZT3IEFT3YCaC75b3jgKkXPxjahHtgDOZK3ym8vTfc4DC4R_qXOGI49SOf7PpAc3B88Cxi1lXCZZz51lSjyXzp8qRvq-2adHF9jqqeC-qDkY0aQoGZkZTihkQ4OLTFgA0f3M")
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
