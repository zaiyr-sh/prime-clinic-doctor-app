package kg.iaau.diploma.primeclinicdoctor.ui.main.medcards

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.*
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentMedCardsBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.medcards.adapter.MedCardAdapter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedCardsFragment : Fragment() {

    private lateinit var vb: FragmentMedCardsBinding
    private val vm: MedCardsVM by navGraphViewModels(R.id.main_navigation) { defaultViewModelProviderFactory }
    private val adapter = MedCardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentMedCardsBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    vb.rvMedCards.scrollToPosition(0)
                    vm.searchPhotos(query)
                    searchView.clearFocus()
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) getMedCards()
                return true
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(vb.toolbar)
        setupFragmentView()
        observeLiveData()
    }

    private fun setupFragmentView() {
        vb.rvMedCards.adapter = adapter
    }

    private fun observeLiveData() {
        vm.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is CoreEvent.Loading -> showLoader()
                is CoreEvent.Success -> goneLoader()
                is CoreEvent.Error -> errorAction(event)
            }
        }
        getMedCards()
        vm.searchedMedCards.observe(viewLifecycleOwner) { searchedMedCards ->
            lifecycleScope.launch {
                adapter.submitData(viewLifecycleOwner.lifecycle, searchedMedCards)
            }
        }
    }

    private fun getMedCards() {
        vm.getMedCards().observe(viewLifecycleOwner) { medCards ->
            lifecycleScope.launch {
                adapter.submitData(viewLifecycleOwner.lifecycle, medCards)
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