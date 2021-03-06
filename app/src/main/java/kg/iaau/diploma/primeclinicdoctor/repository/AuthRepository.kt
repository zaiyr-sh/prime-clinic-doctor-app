package kg.iaau.diploma.primeclinicdoctor.repository

import kg.iaau.diploma.data.AccessToken
import kg.iaau.diploma.data.Authorization
import kg.iaau.diploma.local_storage.prefs.StoragePreferences
import kg.iaau.diploma.network.api.ApiAuth

class AuthRepository(
    private val prefs: StoragePreferences,
    private val apiAuth: ApiAuth
) {
    var token: String? = prefs.token
    var phone: String? = prefs.phone

    suspend fun auth(phone: String, password: String): AccessToken {
        val authorization = Authorization(phone, password)
        val response = apiAuth.auth(authorization)
        saveUserIds(response.id, response.userId)
        saveToken(response.accessToken)
        saveRefreshToken(response.refreshToken)
        savePhone(phone)
        return response
    }

    fun getPin() = prefs.pin

    fun savePin(pin: String) {
        prefs.pin = pin
    }

    fun restorePinWithTokens() {
        prefs.token = ""
        prefs.refreshToken = ""
        prefs.pin = ""
    }

    private fun saveUserIds(id: Long?, userId: Long?) {
        prefs.id = id
        prefs.userId = userId
    }

    private fun saveToken(accessToken: String?) {
        prefs.token = accessToken
    }

    private fun saveRefreshToken(refreshToken: String?) {
        prefs.refreshToken = refreshToken
    }

    private fun savePhone(phone: String) {
        prefs.phone = phone
    }

}