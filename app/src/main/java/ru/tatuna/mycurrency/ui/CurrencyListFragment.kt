package ru.tatuna.mycurrency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tatuna.mycurrency.CurrencyListAdapter
import ru.tatuna.mycurrency.ItemClickListener
import ru.tatuna.mycurrency.R
import ru.tatuna.mycurrency.databinding.FragmentCurrencyListBinding
import ru.tatuna.mycurrency.pojo.CurrencyItem
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
//    private val currencyList = listOf(
//        CurrencyItem("AED", (1F / 0.064951F)),
//        CurrencyItem("AFN", (1F / 1.559068F)),
//        CurrencyItem("ALL", (1F / 2.009515F)),
//        CurrencyItem("AMD", (1F / 7.366452F)),
//        CurrencyItem("ANG", (1F / 0.031458F)),
//        CurrencyItem("AOA", (1F / 7.674829F))
//    )

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

        viewModel.currencyList.observe(viewLifecycleOwner) { state ->
            when (state) {
                CurrencyListViewModel.State.Error -> {
                    binding.error.visibility = View.VISIBLE
                    binding.currencyList.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                }
                CurrencyListViewModel.State.Loading -> {
                    binding.error.visibility = View.GONE
                    binding.currencyList.visibility = View.GONE
                    binding.loading.visibility = View.VISIBLE
                }
                is CurrencyListViewModel.State.Success -> {
                    binding.error.visibility = View.GONE
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