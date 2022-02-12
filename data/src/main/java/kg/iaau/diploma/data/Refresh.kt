package kg.iaau.diploma.data

data class Refresh(
    var accessToken: String? =  null,
    var refreshToken: String? = null,
    var username: String? = null
)
