package kg.iaau.diploma.primeclinicdoctor.ui.main.medcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.*
import kg.iaau.diploma.data.Client
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentMedCardsBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.medcards.adapter.MedCardAdapter

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
        vm.getMedCards()
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        vm.medCardsLiveData.observe(viewLifecycleOwner) { medCards ->
            setupMedCards(medCards)
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

    private fun setupMedCards(medCards: List<Client>) {
        vb.run {
            if(medCards.isNullOrEmpty()) {
                rvMedCards.hide()
                ivEmpty.show()
            } else {
                rvMedCards.show()
                ivEmpty.hide()
                adapter.submitList(medCards)
            }
        }
    }

}