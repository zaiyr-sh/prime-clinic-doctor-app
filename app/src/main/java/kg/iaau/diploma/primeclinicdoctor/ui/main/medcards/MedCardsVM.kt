package kg.iaau.diploma.primeclinicdoctor.ui.main.medcards

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.iaau.diploma.core.vm.CoreVM
import kg.iaau.diploma.data.Client
import kg.iaau.diploma.primeclinicdoctor.repository.MedCardsRepository
import javax.inject.Inject

@HiltViewModel
class MedCardsVM @Inject constructor(private val repository: MedCardsRepository) : CoreVM() {

    fun getMedCards(): LiveData<PagingData<Client>> {
        return repository.getMedCards(event)
    }

}