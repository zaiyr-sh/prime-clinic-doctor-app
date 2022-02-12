package kg.iaau.diploma.network.api

import kg.iaau.diploma.data.AccessToken
import kg.iaau.diploma.data.Authorization
import kg.iaau.diploma.data.Refresh
import kg.iaau.diploma.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiAuth {

    @POST("auth")
    suspend fun auth(@Body authorization: Authorization): AccessToken

    @POST("refresh")
    fun refresh(@Body refresh: Refresh): Call<AccessToken>

    @POST("register")
    suspend fun register(@Body user: User)

    @POST("verify/{code}")
    suspend fun verify(@Path("code") code: String)

}