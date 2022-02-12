package kg.iaau.diploma.primeclinicdoctor.ui.authorization

import dagger.hilt.android.lifecycle.HiltViewModel
import kg.iaau.diploma.core.vm.CoreVM
import kg.iaau.diploma.primeclinicdoctor.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthorizationVM @Inject constructor(private val repository: AuthRepository) : CoreVM() {

    fun isUserSignIn() = !repository.token.isNullOrEmpty()

    fun auth(phone: String, password: String) {
        safeLaunch(
            action = {
                repository.auth(phone, password)
            }
        )
    }

    fun savePhoneWithDeviceId(phone: String?, deviceId: String?) {
        repository.phone = phone
        repository.deviceId = deviceId
    }

}