package kg.iaau.diploma.primeclinicdoctor.ui.main.schedule.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.CoreFragment
import kg.iaau.diploma.core.utils.hide
import kg.iaau.diploma.core.utils.show
import kg.iaau.diploma.data.Slot
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentClientsReservedBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.schedule.ScheduleVM
import kg.iaau.diploma.primeclinicdoctor.ui.main.schedule.reservation.adapter.ClientAdapter

@AndroidEntryPoint
class ClientsReservedFragment : CoreFragment<FragmentClientsReservedBinding, ScheduleVM>(ScheduleVM::class.java) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentClientsReservedBinding
        get() = FragmentClientsReservedBinding::inflate

    private val adapter = ClientAdapter()
    private val args: ClientsReservedFragmentArgs by navArgs()
    private val date by lazy { args.date }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getScheduleFromDb(date)
    }

    override fun setupFragmentView() {
        vb.run {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            rvClients.adapter = adapter
        }
    }

    override fun observeLiveData() {
        vm.choosingDateLiveData.observe(viewLifecycleOwner) { date ->
            vb.toolbar.title = getString(R.string.client_reservation_for_date, date)
        }
        vm.clientsLiveData.observe(viewLifecycleOwner) { clients ->
            setupClients(clients)
        }
    }

    private fun setupClients(clients: List<Slot>) {
        vb.run {
            when(clients.isNullOrEmpty()) {
                true -> {
                    rvClients.hide()
                    ivEmpty.show()
                }
                else -> {
                    adapter.submitList(clients)
                    rvClients.show()
                    ivEmpty.hide()
                }
            }
        }
    }

}