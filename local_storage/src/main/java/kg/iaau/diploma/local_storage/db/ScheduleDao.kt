package kg.iaau.diploma.local_storage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kg.iaau.diploma.data.Interval

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(aboutList: List<Interval>)

    @Query("select * from Interval")
    suspend fun getScheduleFromDb(): List<Interval>

}