package ru.tatuna.mycurrency.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import ru.tatuna.mycurrency.ui.views.ErrorView
import ru.tatuna.mycurrency.ui.views.LoadingView
import ru.tatuna.mycurrency.ui.views.success.ItemListSuccessView
import ru.tatuna.mycurrency.viewmodels.CurrencyListViewModel

@Composable
fun ItemListScreen(
    navController: NavHostController,
    viewModel: CurrencyListViewModel
) {
    val viewState = viewModel.currencyList.observeAsState()

    when (val state = viewState.value) {
        CurrencyListViewModel.State.Error -> ErrorView { viewModel.loadCurrencyList() }
        CurrencyListViewModel.State.Loading -> LoadingView()
        is CurrencyListViewModel.State.Success -> ItemListSuccessView(state.data.history, navController)
    }

    LaunchedEffect(key1 = viewState, block = { viewModel.loadCurrencyList() })
}