package kg.iaau.diploma.core.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kg.iaau.diploma.core.R
import kg.iaau.diploma.core.utils.CoreEvent.*
import kg.iaau.diploma.core.utils.Event
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

abstract class CoreVM : ViewModel() {

    var event: MutableLiveData<Event> = MutableLiveData()

    fun safeLaunch(dispatcher: CoroutineDispatcher = Dispatchers.IO, action: suspend () -> Unit, fail: (() -> Unit)? = null, success: (() -> Unit)? = null) {
        viewModelScope.launch(dispatcher) {
            try {
                event.postValue(Loading(true))
                action.invoke()
                event.postValue(Success())
                success?.invoke()
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        var message = R.string.network_error
                        when(throwable.code()) {

                        }
                        event.postValue(Error(false, throwable.code(), throwable.response()?.errorBody(), message))
                        fail?.invoke()
                    }
                    else -> {
                        event.postValue(Error(true, null, null, R.string.network_error))
                    }
                }
            }
        }
    }

}