package kg.iaau.diploma.data

data class MedCards(
    var content: List<Client> = arrayListOf(),
    var totalElements: Int? = null,
    var last: Boolean? = null,
    var totalPages: Int? = null,
    var first: Boolean? = null,
    var size: Int? = null,
    var number: Int? = null,
    var numberOfElements: Int? = null,
    var empty: Boolean? = null

)

data class Client(
    var id: Int? = null,
    var username: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var patronymic: String? = null,
    var birthDate: String? = null,
    var image: String? = null
)