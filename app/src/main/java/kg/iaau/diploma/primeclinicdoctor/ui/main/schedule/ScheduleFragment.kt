package kg.iaau.diploma.primeclinicdoctor.ui.main.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.*
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
        observeLiveData()
    }

    private fun setupFragmentView() {
        vb.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            vm.getScheduleFromDb(convertToDateFormat(dayOfMonth, month, year))
            findNavController().navigate(R.id.nav_client_reserved)
        }
    }

    private fun observeLiveData() {
        vm.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is CoreEvent.Loading -> showLoader()
                is CoreEvent.Success -> goneLoader()
                is CoreEvent.Error -> errorAction(event)
            }
        }
    }

    private fun errorAction(event: CoreEvent.Error) {
        when (event.isNetworkError) {
            true -> requireActivity().toast(event.message)
            else -> requireActivity().toast(getString(R.string.unexpected_error))
        }
        goneLoader()
    }

    private fun showLoader() {
        vb.run {
            progressBar.show()
            clContainer.setAnimateAlpha(0.5f)
        }
    }

    private fun goneLoader() {
        vb.run {
            progressBar.gone()
            clContainer.setAnimateAlpha(1f)
        }
    }

}