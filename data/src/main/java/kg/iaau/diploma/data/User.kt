package kg.iaau.diploma.data

class User(
    val authorities: List<String> = listOf("USER"),
    var birthDate: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var password: String? = null,
    var patronymic: String? = null,
    var username: String? = null
)