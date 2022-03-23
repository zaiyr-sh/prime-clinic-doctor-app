package kg.iaau.diploma.primeclinicdoctor.repository.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kg.iaau.diploma.core.constants.DEFAULT_PAGE_INDEX
import kg.iaau.diploma.core.constants.NETWORK_ERROR
import kg.iaau.diploma.core.utils.CoreEvent
import kg.iaau.diploma.core.utils.Event
import kg.iaau.diploma.data.Client
import kg.iaau.diploma.network.api.ApiMedCard
import retrofit2.HttpException
import java.io.IOException

class MedCardsDS(
    private var event: MutableLiveData<Event>,
    private val apiMedCard: ApiMedCard,
    private val query: String
) : PagingSource<Int, Client>() {

    override fun getRefreshKey(state: PagingState<Int, Client>): Int? = null

    /**
     * calls api if there is any error getting results then return the [LoadResult.Error]
     * for successful response return the results using [LoadResult.Page] for some reason if the results
     * are empty from service like in case of no more data from api then we can pass [null] to
     * send signal that source has reached the end of list
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Client> {
        val page = params.key ?: DEFAULT_PAGE_INDEX
        return try {
            event.postValue(CoreEvent.Loading(true))
            val response = apiMedCard.getMedCards(page, query).content
            event.postValue(CoreEvent.Success())
            LoadResult.Page(
                response,
                prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            event.postValue(CoreEvent.Error(true, null, null, NETWORK_ERROR))
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            event.postValue(
                CoreEvent.Error(
                    false,
                    exception.code(),
                    exception.response()?.errorBody(),
                    exception.message.toString()
                )
            )
            return LoadResult.Error(exception)
        }
    }
}