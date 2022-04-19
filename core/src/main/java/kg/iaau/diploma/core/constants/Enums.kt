package kg.iaau.diploma.core.constants

enum class UserType {
    ADMIN, PATIENT
}

enum class MessageType(val type: String) {
    TEXT("text"), IMAGE("image")
}