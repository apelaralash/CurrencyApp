package ru.tatuna.mycurrency.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import ru.tatuna.mycurrency.ui.views.ErrorView
import ru.tatuna.mycurrency.ui.views.LoadingView
import ru.tatuna.mycurrency.ui.views.success.ItemInfoSuccessView
import ru.tatuna.mycurrency.viewmodels.CurrencyInfoViewModel

@Composable
fun ItemInfoScreen(itemName: String, itemValue: Float, viewModel: CurrencyInfoViewModel) {

    val viewState = viewModel.currencyHistory.observeAsState()

    when (val state = viewState.value) {
        CurrencyInfoViewModel.State.Error -> ErrorView { viewModel.loadCurrencyHistory(itemName) }
        CurrencyInfoViewModel.State.Loading -> LoadingView()
        is CurrencyInfoViewModel.State.Success -> {
            ItemInfoSuccessView(state.data.history, itemName, itemValue)
        }
    }

    LaunchedEffect(key1 = viewState, block = { viewModel.loadCurrencyHistory(itemName) })
}