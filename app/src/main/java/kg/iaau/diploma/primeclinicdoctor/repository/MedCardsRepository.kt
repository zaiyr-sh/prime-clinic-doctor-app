package kg.iaau.diploma.primeclinicdoctor.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import kg.iaau.diploma.core.constants.DEFAULT_PAGE_SIZE
import kg.iaau.diploma.core.utils.Event
import kg.iaau.diploma.data.Client
import kg.iaau.diploma.network.api.ApiMedCard
import kg.iaau.diploma.primeclinicdoctor.repository.paging.MedCardsDS

class MedCardsRepository(
    private val apiMedCard: ApiMedCard
) {

    fun getMedCards(event: MutableLiveData<Event>, pagingConfig: PagingConfig = getDefaultPageConfig(), query: String = ""): LiveData<PagingData<Client>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { MedCardsDS(event, apiMedCard, query) }
        ).liveData
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = true)
    }

}