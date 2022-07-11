package ru.tatuna.mycurrency.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.tatuna.mycurrency.pojo.CurrencyItem
import ru.tatuna.mycurrency.repositories.CurrencyListRepository
import ru.tatuna.mycurrency.service.CurrencyService
import ru.tatuna.mycurrency.ui.screens.ItemInfoScreen
import ru.tatuna.mycurrency.ui.screens.ItemListScreen
import ru.tatuna.mycurrency.viewmodels.CurrencyListViewModel
import ru.tatuna.mycurrency.viewmodels.CurrencyListViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrencyListViewModel
    private lateinit var viewModelFactory: CurrencyListViewModelFactory
    private val currencyList = listOf(
        CurrencyItem("AED", (1F / 0.064951F)),
        CurrencyItem("AFN", (1F / 1.559068F)),
        CurrencyItem("ALL", (1F / 2.009515F)),
        CurrencyItem("AMD", (1F / 7.366452F)),
        CurrencyItem("ANG", (1F / 0.031458F)),
        CurrencyItem("AOA", (1F / 7.674829F))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory =
            CurrencyListViewModelFactory(CurrencyListRepository(CurrencyService.INSTANCE))
        viewModel = ViewModelProvider(this, viewModelFactory)[CurrencyListViewModel::class.java]
        viewModel.loadCurrencyList()
        setContent {
            val navController = rememberNavController()

            Scaffold {
                NavigationComponent(navController, viewModel, currencyList)
            }
        }
    }
}

@Composable
fun NavigationComponent(
    navController: NavHostController,
    viewModel: CurrencyListViewModel,
    currencyList: List<CurrencyItem>
) {
    NavHost(
        navController = navController,
        startDestination = "item_list"
    ) {
        composable("item_list") {
            ItemListScreen(navController, viewModel, currencyList)
        }
        composable(
            route = "item_info/{item_name}/{item_value}",
            arguments = listOf(
                navArgument("item_name") { type = NavType.StringType },
                navArgument("item_value") { type = NavType.FloatType })
        ) {
            val name = it.arguments?.getString("item_name") ?: ""
            val value: Float = it.arguments?.getFloat("item_value") ?: 0.0F
            ItemInfoScreen(name, value)
        }
    }
}