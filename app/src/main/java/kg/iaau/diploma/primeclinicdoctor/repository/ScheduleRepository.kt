package kg.iaau.diploma.primeclinicdoctor.repository

import kg.iaau.diploma.data.Interval
import kg.iaau.diploma.local_storage.db.ScheduleDao
import kg.iaau.diploma.local_storage.prefs.StoragePreferences
import kg.iaau.diploma.network.api.ApiSchedule

class ScheduleRepository(
    private val prefs: StoragePreferences,
    private val apiSchedule: ApiSchedule,
    private val scheduleDao: ScheduleDao
) {

    var userId: Long? = prefs.userId

    suspend fun getSchedule() = apiSchedule.getScheduleByDoctorId(userId)

    suspend fun insertSchedule(schedule: List<Interval>) = scheduleDao.insertSchedule(schedule)

    suspend fun getScheduleFromDb() = scheduleDao.getScheduleFromDb()

}