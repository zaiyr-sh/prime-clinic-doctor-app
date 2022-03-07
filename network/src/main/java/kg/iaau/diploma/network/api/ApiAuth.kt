package kg.iaau.diploma.network.api

import kg.iaau.diploma.data.AccessToken
import kg.iaau.diploma.data.Authorization
import kg.iaau.diploma.data.Refresh
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiAuth {

    @POST("auth")
    suspend fun auth(@Body authorization: Authorization): AccessToken

    @POST("refresh")
    fun refresh(@Body refresh: Refresh): Call<AccessToken>

}