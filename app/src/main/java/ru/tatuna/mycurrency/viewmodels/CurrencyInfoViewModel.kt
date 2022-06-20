package ru.tatuna.mycurrency.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.tatuna.mycurrency.pojo.CurrencyHistoryResponse
import ru.tatuna.mycurrency.repositories.CurrencyHistoryRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CurrencyInfoViewModel(private val repository: CurrencyHistoryRepository) : ViewModel() {
    sealed class State {
        data class Success(val data: CurrencyHistoryResponse) : State()
        object Loading : State()
        object Error : State()
    }

    private val _currencyHistory: MutableLiveData<State> = MutableLiveData()
    val currencyHistory: LiveData<State>
    get() = _currencyHistory

    fun loadCurrencyHistory(currency: String) {
        _currencyHistory.value = State.Loading
        val endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val startDate = LocalDate.now().minusDays(9).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        viewModelScope.launch {
            _currencyHistory.value = when (val result = repository.execute(startDate = startDate, endDate = endDate, currency = currency)) {
                CurrencyHistoryRepository.Result.Error -> State.Error
                is CurrencyHistoryRepository.Result.Success -> State.Success(result.data)
            }
        }
    }
}