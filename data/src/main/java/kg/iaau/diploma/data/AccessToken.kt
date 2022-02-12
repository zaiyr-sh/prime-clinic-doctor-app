package kg.iaau.diploma.data

data class AccessToken(
    var id: Long? = null,
    var userId: Long? = null,
    var accessToken: String? = null,
    var tokenExpirationTime: String? = null,
    var refreshToken: String? = null,
    var refreshExpirationTime: String? = null
)