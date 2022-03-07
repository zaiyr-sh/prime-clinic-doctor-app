package kg.iaau.diploma.primeclinicdoctor.ui.main.schedule.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.hide
import kg.iaau.diploma.core.utils.show
import kg.iaau.diploma.data.Slot
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentClientsReservedBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.schedule.ScheduleVM
import kg.iaau.diploma.primeclinicdoctor.ui.main.schedule.reservation.adapter.ClientAdapter

@AndroidEntryPoint
class ClientsReservedFragment : Fragment() {

    private lateinit var vb: FragmentClientsReservedBinding
    private val vm: ScheduleVM by navGraphViewModels(R.id.main_navigation) { defaultViewModelProviderFactory }
    private val adapter = ClientAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentClientsReservedBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentView()
        observeLiveData()
    }

    private fun setupFragmentView() {
        vb.run {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            rvClients.adapter = adapter
        }
    }

    private fun observeLiveData() {
        vm.choosingDateLiveData.observe(viewLifecycleOwner) { date ->
            vb.toolbar.title = getString(R.string.client_reservation_for_date, date)
        }
        vm.clientsLiveData.observe(viewLifecycleOwner) { clients ->
            setupClients(clients)
        }
    }

    private fun setupClients(clients: List<Slot>) {
        vb.run {
            if(clients.isNullOrEmpty()) {
                rvClients.hide()
                ivEmpty.show()
            } else {
                rvClients.show()
                ivEmpty.hide()
                adapter.submitList(clients)
            }
        }
    }

}