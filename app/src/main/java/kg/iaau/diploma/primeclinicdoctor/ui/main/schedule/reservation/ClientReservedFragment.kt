package kg.iaau.diploma.primeclinicdoctor.ui.main.schedule.reservation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.primeclinicdoctor.R

@AndroidEntryPoint
class ClientReservedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_client_reserved, container, false)
    }

}