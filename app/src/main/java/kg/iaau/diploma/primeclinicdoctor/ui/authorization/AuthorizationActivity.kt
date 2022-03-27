package kg.iaau.diploma.primeclinicdoctor.ui.authorization

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.constants.AUTH_ERROR
import kg.iaau.diploma.core.utils.*
import kg.iaau.diploma.core.utils.CoreEvent.*
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityAuthorizationBinding
import kg.iaau.diploma.primeclinicdoctor.ui.pin.PinActivity

@AndroidEntryPoint
class AuthorizationActivity : AppCompatActivity() {

    private lateinit var vb: ActivityAuthorizationBinding
    private val vm: AuthorizationVM by viewModels()
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(vb.root)
        setupActivityView()
        observeLiveData()
    }

    private fun setupActivityView() {
        vb.apply {
            ccp.registerCarrierNumberEditText(etPhone)
            btnEnter.setEnable(false)
            etPhone.addTextChangedListener { checkEditTextFilling() }
            etPassword.addTextChangedListener { checkEditTextFilling() }
            btnEnter.setOnClickListener { auth() }
        }
    }

    private fun checkEditTextFilling() {
        val (login, password) = editTextHandler()
        vb.btnEnter.setEnable(login.isNotEmpty() && password.isNotEmpty())
    }

    private fun auth() {
        val (login, password) = editTextHandler()
        vm.auth(login.convertPhoneNumberTo(vb.ccp.selectedCountryCode), password)
    }

    private fun editTextHandler(): Array<String> {
        vb.apply {
            val login = etPhone.text.toString().filterNot { it.isWhitespace() }
            val password = etPassword.text.toString().filterNot { it.isWhitespace() }
            return arrayOf(login, password)
        }
    }

    private fun initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if (user == null) vm.createNewUserInFirebase(mAuth)
    }

    private fun observeLiveData() {
        vm.event.observe(this) { event ->
            when (event) {
                is Loading -> showLoader()
                is Success -> successAction()
                is Error -> errorAction(event)
            }
        }
    }

    private fun successAction() {
        initFirebaseAuth()
        goneLoader()
        PinActivity.startActivity(this)
        finish()
    }

    private fun errorAction(event: Error) {
        when (event.isNetworkError) {
            true -> toast(event.message)
            false -> toast(AUTH_ERROR)
        }
        goneLoader()
    }

    private fun showLoader() {
        vb.run {
            progressBar.show()
            clContainer.setAnimateAlpha(0.5f)
            btnEnter.setEnable(false)
        }
    }

    private fun goneLoader() {
        vb.run {
            progressBar.gone()
            clContainer.setAnimateAlpha(1f)
            btnEnter.setEnable(true)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity<AuthorizationActivity>()
        }
    }

}