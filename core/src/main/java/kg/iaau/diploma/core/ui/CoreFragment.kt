package kg.iaau.diploma.core.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import kg.iaau.diploma.core.utils.CoreEvent
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.core.vm.CoreVM

abstract class CoreFragment<VB: ViewBinding, VM: CoreVM>(
    private val mViewModelClass: Class<VM>
) : BaseFragment<VB>() {

    protected val vm by lazy {
        ViewModelProvider(this)[mViewModelClass]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
    }

    open fun observeLiveData() {
        vm.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is CoreEvent.Loading -> showLoader()
                is CoreEvent.Success -> successAction()
                is CoreEvent.Error -> errorAction(event)
                is CoreEvent.Notification -> notificationAction(event)
            }
        }
    }

    open fun successAction() {
        goneLoader()
    }

    open fun notificationAction(event: CoreEvent.Notification) {}

    open fun errorAction(event: CoreEvent.Error) {
        if (event.isNetworkError) requireActivity().toast(getString(event.message))
        goneLoader()
    }

}