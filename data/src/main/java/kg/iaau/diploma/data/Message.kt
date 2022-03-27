package kg.iaau.diploma.data

import com.google.firebase.Timestamp

data class Message(
    var sender: String? = null,
    var receiver: String? = null,
    var message: String? = null,
    var time: Timestamp? = null,
    var type: String? = null,
    var image: String? = null
)