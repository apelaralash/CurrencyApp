package ru.tatuna.mycurrency.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tatuna.mycurrency.repositories.CurrencyListRepository

class CurrencyListViewModelFactory(private val repository: CurrencyListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyListViewModel::class.java)) {
            return CurrencyListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}