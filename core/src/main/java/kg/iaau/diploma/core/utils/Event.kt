package kg.iaau.diploma.core.utils

import okhttp3.ResponseBody

open class Event
open class CoreEvent : Event() {
    class Success : CoreEvent()
    class Notification(val title: String? = null, val message: String? = null) : CoreEvent()
    class Error(val isNetworkError: Boolean,
                val errorCode: Int?,
                val errorBody: ResponseBody?,
                val message: String) : CoreEvent()
    class Loading(val isLoading: Boolean): CoreEvent()
}