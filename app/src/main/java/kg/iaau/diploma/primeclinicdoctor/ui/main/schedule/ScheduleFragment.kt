package kg.iaau.diploma.primeclinicdoctor.ui.main.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentScheduleBinding

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private lateinit var vb: FragmentScheduleBinding
    private val vm: ScheduleVM by navGraphViewModels(R.id.main_navigation) { defaultViewModelProviderFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentScheduleBinding.inflate(inflater, container, false)
        vm.getSchedule()
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentView()
    }

    private fun setupFragmentView() {
        vb.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            findNavController().navigate(R.id.nav_client_reserved)
        }
    }

}