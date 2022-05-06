package kg.iaau.diploma.primeclinicdoctor.ui.main.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.iaau.diploma.core.utils.formatForCurrentDate
import kg.iaau.diploma.core.vm.CoreVM
import kg.iaau.diploma.data.Slot
import kg.iaau.diploma.primeclinicdoctor.repository.AuthRepository
import kg.iaau.diploma.primeclinicdoctor.repository.ScheduleRepository
import javax.inject.Inject

@HiltViewModel
class ScheduleVM @Inject constructor(
    private val repository: ScheduleRepository,
    private val authRepository: AuthRepository
) : CoreVM() {

    val clientsLiveData: LiveData<List<Slot>>
        get() = _clientsLiveData
    private val _clientsLiveData = MutableLiveData<List<Slot>>()

    val choosingDateLiveData: LiveData<String>
        get() = _choosingDateLiveData
    private val _choosingDateLiveData = MutableLiveData<String>()

    fun getSchedule() {
        safeLaunch(
            action = {
                repository.insertSchedule(repository.getSchedule())
            }
        )
    }

    fun getScheduleFromDb(date: String) {
        safeLaunch(
            action = {
                _choosingDateLiveData.postValue(date)
                _clientsLiveData.postValue(
                    repository.getScheduleFromDb()
                        .filter { it.start?.formatForCurrentDate() == date }
                        .flatMap { interval ->
                            interval.reservation.filter { slot ->
                                slot.id != null && slot.paid == true
                            }
                        }
                )
            }
        )
    }

    fun logout() {
        authRepository.restorePinWithTokens()
    }

}