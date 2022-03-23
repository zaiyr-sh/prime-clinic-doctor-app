package kg.iaau.diploma.primeclinicdoctor.ui.main.medcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.iaau.diploma.core.vm.CoreVM
import kg.iaau.diploma.data.Client
import kg.iaau.diploma.primeclinicdoctor.repository.MedCardsRepository
import javax.inject.Inject

@HiltViewModel
class MedCardsVM @Inject constructor(private val repository: MedCardsRepository) : CoreVM() {

    private val currentQuery = MutableLiveData("")

    fun getMedCards(): LiveData<PagingData<Client>> {
        return repository.getMedCards(event)
    }

    val searchedMedCards = currentQuery.switchMap { queryString ->
        repository.getMedCards(event, query = queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query.replace("+", "")
    }

}