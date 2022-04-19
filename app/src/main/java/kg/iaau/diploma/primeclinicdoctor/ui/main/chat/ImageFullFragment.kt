package kg.iaau.diploma.primeclinicdoctor.ui.main.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.BaseFragment
import kg.iaau.diploma.core.utils.loadWithFresco
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentImageFullBinding

@AndroidEntryPoint
class ImageFullFragment : BaseFragment<FragmentImageFullBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentImageFullBinding
        get() = FragmentImageFullBinding::inflate

    private val args: ImageFullFragmentArgs by navArgs()
    private val image by lazy { args.image }

    override fun setupFragmentView() {
        vb.run {
            toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
            ivAvatar.loadWithFresco(
                image,
                onSuccess = { showLoader() },
                onFail = {
                    ivAvatar.setImageResource(kg.iaau.diploma.core.R.drawable.ic_error)
                    goneLoader()
                }
            )
        }
    }

}