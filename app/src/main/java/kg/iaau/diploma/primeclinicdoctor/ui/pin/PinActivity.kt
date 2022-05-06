package kg.iaau.diploma.primeclinicdoctor.ui.pin

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.core.view.get
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.CoreActivity
import kg.iaau.diploma.core.utils.hide
import kg.iaau.diploma.core.utils.show
import kg.iaau.diploma.core.utils.startActivity
import kg.iaau.diploma.primeclinicdoctor.MainActivity
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityPinBinding
import kg.iaau.diploma.primeclinicdoctor.ui.authorization.AuthorizationActivity
import kg.iaau.diploma.primeclinicdoctor.ui.authorization.AuthorizationVM

@AndroidEntryPoint
class PinActivity : CoreActivity<ActivityPinBinding, AuthorizationVM>(AuthorizationVM::class.java) {

    override val bindingInflater: (LayoutInflater) -> ActivityPinBinding
        get() = ActivityPinBinding::inflate

    override fun setupActivityView() {
        if (vm.isFirstTimePinCreating())
            setupTvPin(R.string.pin_creation, false)
        else if (vm.isUserSignIn())
            setupTvPin(R.string.pin_enter, true)
        vb.apply {
            btnZero.setOnClickListener { onEnterPin(0) }
            btnOne.setOnClickListener { onEnterPin(1) }
            btnTwo.setOnClickListener { onEnterPin(2) }
            btnThree.setOnClickListener { onEnterPin(3) }
            btnFour.setOnClickListener { onEnterPin(4) }
            btnFive.setOnClickListener { onEnterPin(5) }
            btnSix.setOnClickListener { onEnterPin(6) }
            btnSeven.setOnClickListener { onEnterPin(7) }
            btnEight.setOnClickListener { onEnterPin(8) }
            btnNine.setOnClickListener { onEnterPin(9) }
            ibDeleteNumber.setOnClickListener { deleteLastDigit() }
            tvRestorePin.setOnClickListener { restorePinWithTokens() }
        }
    }

    private fun setupTvPin(@StringRes stringRes: Int, isRestorePinVisible: Boolean) {
        vb.apply {
            tvPin.text = getString(stringRes)
            when(isRestorePinVisible) {
                true -> tvRestorePin.show()
                else -> tvRestorePin.hide()
            }
        }
    }

    private fun onEnterPin(digit: Int) {
        vb.apply {
            llIndicatorDots[vm.dotPosition].setBackgroundResource(R.drawable.shape_filled_dot)
            vm.fillPin(digit)
            ibDeleteNumber.show()
            tvWrongPin.hide()
            checkIsPinCompletelyFilled()
        }
    }

    private fun checkIsPinCompletelyFilled() {
        vb.apply {
            if(vm.dotPosition == 4) {
                llIndicatorDots.hide()
                when {
                    vm.isFirstTimePinCreating() -> startCurrentActivity()
                    vm.isPinVerified() -> startMainActivity()
                    else -> wrongPin()
                }
            }
        }
    }

    private fun startCurrentActivity() {
        vm.savePin()
        startActivity(intent)
        finish()
        return
    }

    private fun deleteLastDigit() {
        vm.deleteLastDigit()
        vb.apply {
            llIndicatorDots[vm.dotPosition].setBackgroundResource(R.drawable.shape_empty_dot)
            checkIsPinEmpty()
        }
    }

    private fun checkIsPinEmpty() {
        if(vm.dotPosition == 0) vb.ibDeleteNumber.hide()
    }

    private fun wrongPin() {
        vm.clearPin()
        vb.apply {
            tvWrongPin.show()
            tlKeypad.show()
            ibDeleteNumber.hide()
            llIndicatorDots.show()
            setDotsBackgroundRes(R.drawable.shape_empty_dot)
        }
    }

    private fun setDotsBackgroundRes(res_id: Int) {
        for (position in 0..3) {
            vb.llIndicatorDots[position].setBackgroundResource(res_id)
        }
    }

    private fun startMainActivity() {
        vm.signInFirebase()
        MainActivity.startActivity(this)
        finish()
    }

    private fun restorePinWithTokens() {
        vm.restorePinWithTokens()
        AuthorizationActivity.startActivity(this)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity<PinActivity>()
        }
    }

}