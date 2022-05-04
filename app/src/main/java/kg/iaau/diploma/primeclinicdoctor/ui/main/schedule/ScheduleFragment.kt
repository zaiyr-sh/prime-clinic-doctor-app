package kg.iaau.diploma.primeclinicdoctor.ui.main.schedule

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.CoreFragment
import kg.iaau.diploma.core.utils.*
import kg.iaau.diploma.primeclinicdoctor.MainActivity
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentScheduleBinding
import kg.iaau.diploma.primeclinicdoctor.ui.authorization.AuthorizationActivity

@AndroidEntryPoint
class ScheduleFragment : CoreFragment<FragmentScheduleBinding, ScheduleVM>(ScheduleVM::class.java) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentScheduleBinding
        get() = FragmentScheduleBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getSchedule()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        (requireActivity() as? MainActivity)?.setSupportActionBar(vb.toolbar)
        return vb.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.exit -> {
                context?.showDialog(R.string.exit_confirmation, {
                    vm.logout()
                    activity?.finishAffinity()
                    AuthorizationActivity.startActivity(requireContext())
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setupFragmentView() {
        vb.run {
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                findNavController().navigate(
                    R.id.nav_client_reserved,
                    Bundle().apply {
                        putString("date", convertToDateFormat(dayOfMonth, month, year))
                    }
                )
            }
            swipeToRefresh.setOnRefreshListener {  vm.getSchedule() }
        }
    }

    override fun errorAction(event: CoreEvent.Error) {
        super.errorAction(event)
        if (!event.isNetworkError) requireActivity().toast(getString(R.string.unexpected_error))
    }

    override fun showLoader() {
        if(!vb.swipeToRefresh.isRefreshing)
            super.showLoader()
        vb.clContainer.run {
            setAnimateAlpha(0.5f)
            setEnable(false)
        }
    }

    override fun goneLoader() {
        super.goneLoader()
        with(vb) {
            clContainer.run {
                setAnimateAlpha(1f)
                setEnable(true)
            }
            swipeToRefresh.isRefreshing = false
        }
    }

}