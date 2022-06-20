package ru.tatuna.mycurrency.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tatuna.mycurrency.repositories.CurrencyHistoryRepository
import ru.tatuna.mycurrency.repositories.CurrencyListRepository

class CurrencyInfoViewModelFactory(private val repository: CurrencyHistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyInfoViewModel::class.java)) {
            return CurrencyInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}