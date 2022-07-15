package ru.tatuna.mycurrency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tatuna.mycurrency.CurrencyListAdapter
import ru.tatuna.mycurrency.ItemClickListener
import ru.tatuna.mycurrency.R
import ru.tatuna.mycurrency.databinding.FragmentCurrencyListBinding
import ru.tatuna.mycurrency.repositories.CurrencyListRepository
import ru.tatuna.mycurrency.service.CurrencyService
import ru.tatuna.mycurrency.viewmodels.CurrencyListViewModel
import ru.tatuna.mycurrency.viewmodels.CurrencyListViewModelFactory

class CurrencyListFragment : Fragment(), ItemClickListener {

    private var _binding: FragmentCurrencyListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CurrencyListViewModel
    private lateinit var viewModelFactory: CurrencyListViewModelFactory

    private lateinit var currencyListAdapter: CurrencyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrencyListBinding.inflate(inflater, container, false)
        viewModelFactory = CurrencyListViewModelFactory(CurrencyListRepository(CurrencyService.INSTANCE))
        viewModel = ViewModelProvider(this, viewModelFactory)[CurrencyListViewModel::class.java]
        viewModel.loadCurrencyList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currencyListAdapter = CurrencyListAdapter(this)
        binding.currencyList.adapter = currencyListAdapter
        binding.currencyList.layoutManager = LinearLayoutManager(requireContext())
        binding.errorView.errorBtn.setOnClickListener{ viewModel.loadCurrencyList() }

        viewModel.currencyList.observe(viewLifecycleOwner) { state ->
            when (state) {
                CurrencyListViewModel.State.Error -> {
                    binding.errorView.errorContainer.visibility = View.VISIBLE
                    binding.currencyList.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                }
                CurrencyListViewModel.State.Loading -> {
                    binding.errorView.errorContainer.visibility = View.GONE
                    binding.currencyList.visibility = View.GONE
                    binding.loading.visibility = View.VISIBLE
                }
                is CurrencyListViewModel.State.Success -> {
                    binding.errorView.errorContainer.visibility = View.GONE
                    binding.currencyList.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE
                    currencyListAdapter.setItems(state.data.history)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(name: String, value: Float) {
        val fragment = CurrencyInfoFragment()
        val bundle = Bundle()
        bundle.putString(CURRENCY_NAME, name)
        bundle.putFloat(CURRENCY_VALUE, value)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("InfoFragment")
            .commit()
    }

    companion object {
        const val CURRENCY_NAME = "name"
        const val CURRENCY_VALUE = "value"
    }
}