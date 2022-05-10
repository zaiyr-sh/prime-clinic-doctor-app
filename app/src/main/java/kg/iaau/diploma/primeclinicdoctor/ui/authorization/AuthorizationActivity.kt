package kg.iaau.diploma.primeclinicdoctor.ui.authorization

import android.content.Context
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.CoreActivity
import kg.iaau.diploma.core.utils.*
import kg.iaau.diploma.core.utils.CoreEvent.Error
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityAuthorizationBinding
import kg.iaau.diploma.primeclinicdoctor.ui.pin.PinActivity

@AndroidEntryPoint
class AuthorizationActivity :
    CoreActivity<ActivityAuthorizationBinding, AuthorizationVM>(AuthorizationVM::class.java) {

    override val bindingInflater: (LayoutInflater) -> ActivityAuthorizationBinding
        get() = ActivityAuthorizationBinding::inflate

    override fun setupActivityView() {
        vb.apply {
            ccp.registerCarrierNumberEditText(etPhone)
            btnEnter.setEnable(false)
            etPhone.addTextChangedListener { validateFields() }
            etPassword.addTextChangedListener { validateFields() }
            btnEnter.setOnClickListener { auth() }
        }
    }

    private fun validateFields() {
        val (login, password) = filterFields()
        vb.btnEnter.setEnable(login.isNotEmpty() && password.isNotEmpty())
    }

    private fun auth() {
        val (login, password) = filterFields()
        vm.auth(login.convertPhoneNumberTo(vb.ccp.selectedCountryCode), password)
    }

    private fun filterFields(): Array<String> {
        vb.apply {
            val login = etPhone.text.toString().filterNot { it.isWhitespace() }
            val password = etPassword.text.toString().filterNot { it.isWhitespace() }
            return arrayOf(login, password)
        }
    }

    override fun successAction() {
        super.successAction()
        PinActivity.startActivity(this)
        finish()
    }

    override fun errorAction(event: Error) {
        super.errorAction(event)
        if (!event.isNetworkError) toast(getString(R.string.auth_error))
    }

    override fun showLoader() {
        super.showLoader()
        vb.clContainer.run {
            setAnimateAlpha(0.5f)
            setEnable(false)
        }
    }

    override fun goneLoader() {
        super.goneLoader()
        vb.clContainer.run {
            setAnimateAlpha(1f)
            setEnable(true)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity<AuthorizationActivity>()
        }
    }

}