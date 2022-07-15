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
import ru.tatuna.mycurrency.ui.CurrencyListFragment.Companion.CURRENCY_NAME
import ru.tatuna.mycurrency.ui.CurrencyListFragment.Companion.CURRENCY_VALUE
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrencyInfoBinding.inflate(inflater, container, false)
        name = arguments?.getString(CURRENCY_NAME) ?: ""
        currencyValue = arguments?.getFloat(CURRENCY_VALUE) ?: 0.0F
        viewModelFactory =
            CurrencyInfoViewModelFactory(CurrencyHistoryRepository(CurrencyService.INSTANCE))
        viewModel = ViewModelProvider(this, viewModelFactory)[CurrencyInfoViewModel::class.java]
        viewModel.loadCurrencyHistory(name)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.errorView.errorBtn.setOnClickListener{ viewModel.loadCurrencyHistory(name) }
        viewModel.currencyHistory.observe(viewLifecycleOwner) { state ->
            when (state) {
                CurrencyInfoViewModel.State.Error -> {
                    binding.errorView.errorContainer.visibility = View.VISIBLE
                    binding.graph.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                }
                CurrencyInfoViewModel.State.Loading -> {
                    binding.errorView.errorContainer.visibility = View.GONE
                    binding.graph.visibility = View.GONE
                    binding.loading.visibility = View.VISIBLE
                }
                is CurrencyInfoViewModel.State.Success -> {
                    binding.errorView.errorContainer.visibility = View.GONE
                    binding.graph.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE
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