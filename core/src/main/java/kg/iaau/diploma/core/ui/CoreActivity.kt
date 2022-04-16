package kg.iaau.diploma.core.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import kg.iaau.diploma.core.utils.CoreEvent
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.core.vm.CoreVM

abstract class CoreActivity<VB: ViewBinding, VM: CoreVM>(
    private val mViewModelClass: Class<VM>
) : BaseActivity<VB>() {

    protected val vm by lazy {
        ViewModelProvider(this)[mViewModelClass]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLiveData()
    }

    open fun observeLiveData() {
        vm.event.observe(this) { event ->
            when (event) {
                is CoreEvent.Loading -> showLoader()
                is CoreEvent.Success -> successAction()
                is CoreEvent.Error -> errorAction(event)
                is CoreEvent.Notification -> notificationAction()
            }
        }
    }

    open fun successAction() {
        goneLoader()
    }

    open fun notificationAction() {}

    open fun errorAction(event: CoreEvent.Error) {
        if (event.isNetworkError) toast(getString(event.message))
        goneLoader()
    }

}