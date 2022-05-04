package kg.iaau.diploma.primeclinicdoctor.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kg.iaau.diploma.core.utils.convertToEmail
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

    fun createNewUserInFirebase(mAuth: FirebaseAuth) {
        val email = prefs.phone?.convertToEmail() ?: ""
        mAuth.createUserWithEmailAndPassword(email, prefs.phone ?: "").addOnCompleteListener {
            when (it.isSuccessful) {
                true -> setupOnlineUser()
                false -> {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, prefs.phone ?: "")
                        .addOnCompleteListener { t ->
                            if (t.isSuccessful) setupUser()
                        }
                }
            }
        }
    }

    private fun setupOnlineUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val map = mutableMapOf<String, Any>()
            map["userType"] = "USER"
            map["userPhone"] = prefs.phone ?: ""
            map["isOnline"] = true
            db.collection("users").document(prefs.userId.toString()).set(map, SetOptions.merge())
        }
    }

    private fun setupUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val map = mutableMapOf<String, String>()
            map["userType"] = "USER"
            map["userPhone"] = prefs.phone ?: ""
            db.collection("users").document(prefs.userId.toString()).set(map)
        }
    }

    fun signInFirebase() {
        val email = prefs.phone?.convertToEmail() ?: ""
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, prefs.phone ?: "")
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> setupOnlineUser()
                    else -> {
                        FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email, prefs.phone ?: "")
                            .addOnCompleteListener { t ->
                                if (t.isSuccessful) setupUser()
                            }
                    }
                }
            }
    }

}