package ru.tatuna.mycurrency.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.tatuna.mycurrency.pojo.CurrencyDateResponse
import ru.tatuna.mycurrency.repositories.CurrencyListRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CurrencyListViewModel(private val repository: CurrencyListRepository) : ViewModel() {
    sealed class State {
        data class Success(val data: CurrencyDateResponse) : State()
        object Loading : State()
        object Error : State()
    }

    private val _currencyList: MutableLiveData<State> = MutableLiveData()
    val currencyList: LiveData<State>
    get() = _currencyList

    fun loadCurrencyList() {
        _currencyList.value = State.Loading
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        viewModelScope.launch {
            _currencyList.value = when (val result = repository.execute(date)) {
                CurrencyListRepository.Result.Error -> State.Error
                is CurrencyListRepository.Result.Success -> State.Success(result.data)
            }
        }
    }
}