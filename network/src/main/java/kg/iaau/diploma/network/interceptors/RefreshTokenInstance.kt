package kg.iaau.diploma.network.interceptors

import kg.iaau.diploma.network.BuildConfig
import kg.iaau.diploma.network.api.ApiModule.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RefreshTokenInstance {

    private val client = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG)
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> serviceBuilder(service: Class<T>): T {
        return retrofit.create(service)
    }
}