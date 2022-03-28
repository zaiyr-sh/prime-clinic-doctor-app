package kg.iaau.diploma.data

import com.google.firebase.Timestamp

data class Chat(
    var clientId: String? = null,
    var adminId: String? = null,
    var adminPhone: String? = null,
    var lastMessage: String? = null,
    var lastMessageSenderId: String? = null,
    var chatStarted: Boolean? = null,
    var lastMessageTime: Timestamp? = null,
    var userPhone: String? = null,
)