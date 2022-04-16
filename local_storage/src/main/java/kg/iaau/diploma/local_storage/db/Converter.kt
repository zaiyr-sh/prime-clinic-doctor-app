package kg.iaau.diploma.local_storage.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import kg.iaau.diploma.data.Slot

class Converter {

    @TypeConverter
    fun reservationListToJson(value: List<Slot>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToReservationList(value: String) = Gson().fromJson(value, Array<Slot>::class.java).toList()
}