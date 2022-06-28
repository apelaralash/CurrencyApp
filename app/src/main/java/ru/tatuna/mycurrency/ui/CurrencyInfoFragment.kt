package ru.tatuna.mycurrency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.tatuna.mycurrency.databinding.FragmentCurrencyInfoBinding
import ru.tatuna.mycurrency.repositories.CurrencyHistoryRepository
import ru.tatuna.mycurrency.service.CurrencyService
import ru.tatuna.mycurrency.viewmodels.CurrencyInfoViewModel
import ru.tatuna.mycurrency.viewmodels.CurrencyInfoViewModelFactory
import kotlin.properties.Delegates

class CurrencyInfoFragment : Fragment() {
    private var _binding: FragmentCurrencyInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var name: String
    private var currencyValue by Delegates.notNull<Float>()

    private lateinit var viewModel: CurrencyInfoViewModel
    private lateinit var viewModelFactory: CurrencyInfoViewModelFactory
//    private val historyList = listOf(
//        HistoryValue(date = LocalDate.parse("2022-06-08"), name = "AED", value = 1F / 0.061741F),
//        HistoryValue(date = LocalDate.parse("2022-06-09"), name = "AED", value = 1F / 0.062741F),
//        HistoryValue(date = LocalDate.parse("2022-06-10"), name = "AED", value = 1F / 0.063741F),
//        HistoryValue(date = LocalDate.parse("2022-06-11"), name = "AED", value = 1F / 0.063742F),
//        HistoryValue(date = LocalDate.parse("2022-06-12"), name = "AED", value = 1F / 0.033017F),
//        HistoryValue(date = LocalDate.parse("2022-06-13"), name = "AED", value = 1F / 0.063603F),
//        HistoryValue(date = LocalDate.parse("2022-06-14"), name = "AED", value = 1F / 0.062922F),
//        HistoryValue(date = LocalDate.parse("2022-06-15"), name = "AED", value = 1F / 0.064159F),
//        HistoryValue(date = LocalDate.parse("2022-06-16"), name = "AED", value = 1F / 0.064019F),
//        HistoryValue(date = LocalDate.parse("2022-06-17"), name = "AED", value = 1F / 0.065153F)
//    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrencyInfoBinding.inflate(inflater, container, false)
//        name = arguments?.getString(CURRENCY_NAME) ?: ""
//        currencyValue = arguments?.getFloat(CURRENCY_VALUE) ?: 0.0F
        viewModelFactory =
            CurrencyInfoViewModelFactory(CurrencyHistoryRepository(CurrencyService.INSTANCE))
        viewModel = ViewModelProvider(this, viewModelFactory)[CurrencyInfoViewModel::class.java]
        viewModel.loadCurrencyHistory(name)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.currencyHistory.observe(viewLifecycleOwner) { state ->
            when (state) {
                CurrencyInfoViewModel.State.Error -> {

                }
                CurrencyInfoViewModel.State.Loading -> {

                }
                is CurrencyInfoViewModel.State.Success -> {
                    binding.graph.setCurrencyHistory(state.data.history)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}