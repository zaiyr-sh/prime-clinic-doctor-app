package kg.iaau.diploma.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth

abstract class BaseActivity<VB: ViewBinding> : AppCompatActivity() {

    private var _vb: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val vb: VB
        get() = _vb as VB

    protected lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _vb = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_vb).root)
        setupActivityView()
    }

    abstract fun setupActivityView()

    open fun showLoader() {
        LoadingScreen.showLoading(this)
    }

    open fun goneLoader() {
        LoadingScreen.hideLoading()
    }

    open fun initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        _vb = null
    }

}