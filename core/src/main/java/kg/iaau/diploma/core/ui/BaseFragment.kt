package kg.iaau.diploma.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth

abstract class BaseFragment<VB: ViewBinding> : Fragment() {

    private var _vb: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val vb: VB
        get() = _vb as VB

    protected lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vb = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_vb).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentView()
    }

    abstract fun setupFragmentView()

    open fun showLoader() {
        LoadingScreen.showLoading(requireActivity())
    }

    open fun goneLoader() {
        LoadingScreen.hideLoading()
    }

    open fun initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _vb = null
    }

}