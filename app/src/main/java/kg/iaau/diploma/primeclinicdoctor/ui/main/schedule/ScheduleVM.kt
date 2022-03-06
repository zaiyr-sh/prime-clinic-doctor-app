package kg.iaau.diploma.primeclinicdoctor.ui.main.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.iaau.diploma.core.vm.CoreVM
import kg.iaau.diploma.data.Interval
import kg.iaau.diploma.primeclinicdoctor.repository.ScheduleRepository
import javax.inject.Inject

@HiltViewModel
class ScheduleVM @Inject constructor(private val repository: ScheduleRepository) : CoreVM() {

    val doctorScheduleLiveData: LiveData<List<Interval>?>
        get() = _doctorScheduleLiveData
    private val _doctorScheduleLiveData = MutableLiveData<List<Interval>?>()

    fun getSchedule() {
        safeLaunch(
            action = {
                repository.insertSchedule(repository.getSchedule())
            }
        )
    }

    fun getScheduleFromDb() {
        safeLaunch(
            action = {
                _doctorScheduleLiveData.postValue(repository.getScheduleFromDb())
            }
        )
    }

}