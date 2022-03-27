package kg.iaau.diploma.primeclinicdoctor.ui.authorization

import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.iaau.diploma.core.vm.CoreVM
import kg.iaau.diploma.primeclinicdoctor.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthorizationVM @Inject constructor(private val repository: AuthRepository) : CoreVM() {

    var pin: StringBuilder = StringBuilder()
    var dotPosition: Int = 0

    fun isUserSignIn() = !repository.token.isNullOrEmpty()

    fun auth(phone: String, password: String) {
        safeLaunch(
            action = {
                repository.auth(phone, password)
                savePhone(phone)
            }
        )
    }

    fun isFirstTimePinCreating() = repository.getPin().isNullOrEmpty()

    fun fillPin(digit: Int) {
        dotPosition++
        pin.append(digit)
    }

    fun deleteLastDigit() {
        dotPosition--
        pin.deleteCharAt(dotPosition)
    }

    fun clearPin() {
        dotPosition = 0
        pin.clear()
    }

    fun savePin() {
        repository.savePin(pin.toString())
    }

    fun restorePinWithTokens() {
        repository.restorePinWithTokens()
    }

    fun isPinVerified() = repository.getPin() == pin.toString()

    private fun savePhone(phone: String?) {
        repository.phone = phone
    }

    fun createNewUserInFirebase(mAuth: FirebaseAuth) {
        repository.createNewUserInFirebase(mAuth)
    }

    fun signInFirebase() {
        repository.signInFirebase()
    }

}