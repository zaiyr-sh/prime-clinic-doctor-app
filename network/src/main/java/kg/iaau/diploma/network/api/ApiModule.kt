package kg.iaau.diploma.network.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kg.iaau.diploma.local_storage.prefs.StoragePreferences
import kg.iaau.diploma.network.BuildConfig
import kg.iaau.diploma.network.interceptors.HeaderInterceptor
import kg.iaau.diploma.network.interceptors.RefreshTokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    const val BASE_URL = "http://192.168.15.104:8080/api/v1/"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, prefs: StoragePreferences): OkHttpClient {
        val builder = OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(HeaderInterceptor(prefs))
            .addInterceptor(RefreshTokenInterceptor(prefs))
        if (BuildConfig.DEBUG)
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiAuth(retrofit: Retrofit): ApiAuth = retrofit.create(ApiAuth::class.java)

}