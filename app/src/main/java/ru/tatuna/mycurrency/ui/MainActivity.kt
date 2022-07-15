package ru.tatuna.mycurrency.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.tatuna.mycurrency.repositories.CurrencyHistoryRepository
import ru.tatuna.mycurrency.repositories.CurrencyListRepository
import ru.tatuna.mycurrency.service.CurrencyService
import ru.tatuna.mycurrency.ui.screens.ItemInfoScreen
import ru.tatuna.mycurrency.ui.screens.ItemListScreen
import ru.tatuna.mycurrency.viewmodels.CurrencyInfoViewModel
import ru.tatuna.mycurrency.viewmodels.CurrencyInfoViewModelFactory
import ru.tatuna.mycurrency.viewmodels.CurrencyListViewModel
import ru.tatuna.mycurrency.viewmodels.CurrencyListViewModelFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            Scaffold {
                NavigationComponent(navController)
            }
        }
    }
}

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "item_list"
    ) {
        composable("item_list") {
            val viewModelFactory =
                CurrencyListViewModelFactory(CurrencyListRepository(CurrencyService.INSTANCE))
            val viewModel = viewModel(
                modelClass = CurrencyListViewModel::class.java,
                factory = viewModelFactory
            )

            ItemListScreen(navController, viewModel)
        }
        composable(
            route = "item_info/{item_name}/{item_value}",
            arguments = listOf(
                navArgument("item_name") { type = NavType.StringType },
                navArgument("item_value") { type = NavType.FloatType })
        ) {
            val name = it.arguments?.getString("item_name") ?: ""
            val value: Float = it.arguments?.getFloat("item_value") ?: 0.0F
            val viewModelFactory =
                CurrencyInfoViewModelFactory(CurrencyHistoryRepository(CurrencyService.INSTANCE))
            val viewModel = viewModel(
                modelClass = CurrencyInfoViewModel::class.java,
                factory = viewModelFactory
            )

            ItemInfoScreen(name, value, viewModel)
        }
    }
}