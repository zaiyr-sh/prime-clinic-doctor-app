package kg.iaau.diploma.primeclinicdoctor.ui.main.chat

import dagger.hilt.android.lifecycle.HiltViewModel
import kg.iaau.diploma.core.vm.CoreVM
import kg.iaau.diploma.local_storage.prefs.StoragePreferences
import javax.inject.Inject

@HiltViewModel
class ChatVM @Inject constructor(
    private val prefs: StoragePreferences,
) : CoreVM() {

    val phone: String? = prefs.phone
    val userId: Long? = prefs.userId
    val id: Long? = prefs.id


}