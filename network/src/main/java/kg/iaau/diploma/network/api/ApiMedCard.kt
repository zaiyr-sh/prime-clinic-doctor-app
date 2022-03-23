package kg.iaau.diploma.network.api

import kg.iaau.diploma.data.MedCards
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiMedCard {

    @GET("client/card")
    suspend fun getMedCards(@Query("page") page: Int, @Query("search") query: String): MedCards

}