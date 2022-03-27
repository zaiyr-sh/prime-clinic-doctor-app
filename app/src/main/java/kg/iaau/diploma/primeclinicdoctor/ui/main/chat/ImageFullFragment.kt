package kg.iaau.diploma.primeclinicdoctor.ui.main.chat

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.gone
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentImageFullBinding

@AndroidEntryPoint
class ImageFullFragment : Fragment() {

    private lateinit var vb: FragmentImageFullBinding
    private val args: ImageFullFragmentArgs by navArgs()
    private val image by lazy { args.image }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentImageFullBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentView()
    }

    private fun setupFragmentView() {
        vb.run {
            toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
            Glide.with(requireActivity()).load(image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.gone()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.gone()
                        return false
                    }

                })
                .error(R.drawable.ic_error)
                .into(ivAvatar)
        }
    }
}