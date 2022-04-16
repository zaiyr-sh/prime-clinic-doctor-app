package kg.iaau.diploma.primeclinicdoctor.ui.main.medcards

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.CoreFragment
import kg.iaau.diploma.core.utils.CoreEvent
import kg.iaau.diploma.core.utils.setAnimateAlpha
import kg.iaau.diploma.core.utils.setEnable
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentMedCardsBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.medcards.adapter.MedCardAdapter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedCardsFragment : CoreFragment<FragmentMedCardsBinding, MedCardsVM>(MedCardsVM::class.java) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMedCardsBinding
        get() = FragmentMedCardsBinding::inflate

    private val adapter = MedCardAdapter()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(vb.toolbar)
        return vb.root
    }

    override fun setupFragmentView() {
        vb.rvMedCards.adapter = adapter
    }

    override fun observeLiveData() {
        super.observeLiveData()
        getMedCards()
        vm.searchedMedCards.observe(viewLifecycleOwner) { searchedMedCards ->
            lifecycleScope.launch {
                adapter.submitData(viewLifecycleOwner.lifecycle, searchedMedCards)
            }
        }
    }

    fun getMedCards() {
        vm.getMedCards().observe(viewLifecycleOwner) { medCards ->
            lifecycleScope.launch {
                adapter.submitData(viewLifecycleOwner.lifecycle, medCards)
            }
        }
    }

    override fun errorAction(event: CoreEvent.Error) {
        super.errorAction(event)
        if (!event.isNetworkError) requireActivity().toast(getString(R.string.unexpected_error))
    }

    override fun showLoader() {
        super.showLoader()
        vb.clContainer.run {
            setAnimateAlpha(0.5f)
            setEnable(false)
        }
    }

    override fun goneLoader() {
        super.goneLoader()
        vb.clContainer.run {
            setAnimateAlpha(1f)
            setEnable(true)
        }
    }

}