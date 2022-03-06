package kg.iaau.diploma.primeclinicdoctor.ui.main.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentChatBinding

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var vb: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentChatBinding.inflate(inflater, container, false)
        return vb.root
    }
}