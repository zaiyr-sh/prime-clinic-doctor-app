package kg.iaau.diploma.primeclinicdoctor.repository

import kg.iaau.diploma.data.Interval
import kg.iaau.diploma.local_storage.db.ScheduleDao
import kg.iaau.diploma.local_storage.prefs.StoragePreferences
import kg.iaau.diploma.network.api.ApiSchedule

class ScheduleRepository(
    prefs: StoragePreferences,
    private val apiSchedule: ApiSchedule,
    private val scheduleDao: ScheduleDao
) {

    var id: Long? = prefs.id

    suspend fun getSchedule() = apiSchedule.getScheduleByDoctorId(id)

    suspend fun insertSchedule(schedule: List<Interval>) = scheduleDao.insertSchedule(schedule)

    suspend fun getScheduleFromDb() = scheduleDao.getScheduleFromDb()

}