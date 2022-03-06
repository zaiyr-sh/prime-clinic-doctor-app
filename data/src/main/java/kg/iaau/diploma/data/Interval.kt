package kg.iaau.diploma.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Interval(
    @PrimaryKey
    var id: Int? = null,
    var start: String? = null,
    var end: String? = null,
    var reservation: List<Slot>? = null
)

@Entity
data class Slot(
    var id: Int? = null,
    var start: String? = null,
    var end: String? = null,
    var phoneNumber: String? = null,
    var comment: String? = null,
    var paid: Boolean? = false
)