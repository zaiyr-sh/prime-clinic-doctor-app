package kg.iaau.diploma.primeclinicdoctor.ui.authorization

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.constants.AUTH_ERROR
import kg.iaau.diploma.core.utils.*
import kg.iaau.diploma.core.utils.CoreEvent.*
import kg.iaau.diploma.primeclinicdoctor.MainActivity
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityAuthorizationBinding

@AndroidEntryPoint
class AuthorizationActivity : AppCompatActivity() {

    private lateinit var vb: ActivityAuthorizationBinding
    private val vm: AuthorizationVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(vb.root)
        setupActivityView()
        observeLiveData()
    }

    private fun setupActivityView() {
        vb.apply {
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
            val login = etPhone.text.trim().toString()
            val password = etPassword.text.trim().toString()
            return arrayOf(login, password)
        }
    }

    private fun observeLiveData() {
        vm.event.observe(this, { event ->
            when(event) {
                is Loading -> showLoader()
                is Success -> successAction()
                is Error -> errorAction(event)
            }
        })
    }

    private fun successAction() {
        goneLoader()
        MainActivity.startActivity(this)
    }

    private fun errorAction(event: Error) {
        when (event.isNetworkError) {
            true -> toast(event.message)
            false -> toast(AUTH_ERROR)
        }
        goneLoader()
    }

    private fun showLoader() {
        vb.progressBar.show()
    }

    private fun goneLoader() {
        vb.progressBar.gone()
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity<AuthorizationActivity>()
        }
    }

}