package kg.iaau.diploma.primeclinicdoctor.ui.main.medcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.iaau.diploma.core.vm.CoreVM
import kg.iaau.diploma.data.Client
import kg.iaau.diploma.primeclinicdoctor.repository.MedCardsRepository
import javax.inject.Inject

@HiltViewModel
class MedCardsVM @Inject constructor(private val repository: MedCardsRepository) : CoreVM() {

    val medCardsLiveData: LiveData<List<Client>>
        get() = _medCardsLiveData
    private val _medCardsLiveData = MutableLiveData<List<Client>>()

    fun getMedCards() {
        safeLaunch(
            action = {
                _medCardsLiveData.postValue(repository.getMedCards().content)
            }
        )
    }

}