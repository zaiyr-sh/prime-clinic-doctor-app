package kg.iaau.diploma.data

data class Authorization(
    val username: String,
    val password: String,
    val role: Type = Type.CUSTOMER
)

enum class Type {
    USER, CUSTOMER, ADMIN
}