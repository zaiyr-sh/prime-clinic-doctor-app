package kg.iaau.diploma.core.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kg.iaau.diploma.core.constants.NETWORK_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kg.iaau.diploma.core.utils.CoreEvent.Loading
import kg.iaau.diploma.core.utils.CoreEvent.Error
import kg.iaau.diploma.core.utils.CoreEvent.Success
import kg.iaau.diploma.core.utils.Event
import retrofit2.HttpException

abstract class CoreVM : ViewModel() {

    var event: MutableLiveData<Event> = MutableLiveData()

    fun safeLaunch(dispatcher: CoroutineDispatcher = Dispatchers.IO, action: suspend () -> Unit, fail: (() -> Unit)? = null) {
        viewModelScope.launch(dispatcher) {
            try {
                event.postValue(Loading(true))
                action.invoke()
                event.postValue(Success())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        var message = ""
                        when(throwable.code()) {

                        }
                        event.postValue(Error(false, throwable.code(), throwable.response()?.errorBody(), message))
                        fail?.invoke()
                    }
                    else -> {
                        event.postValue(Error(true, null, null, NETWORK_ERROR))
                    }
                }
            }
        }
    }
}