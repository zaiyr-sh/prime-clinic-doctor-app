package kg.iaau.diploma.network.api

import kg.iaau.diploma.data.Interval
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiSchedule {

    @GET("worktime/relevant/{id}")
    suspend fun getScheduleByDoctorId(@Path("id") id: Long?): List<Interval>

}