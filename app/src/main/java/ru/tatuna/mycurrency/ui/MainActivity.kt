package ru.tatuna.mycurrency.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.tatuna.mycurrency.R
import ru.tatuna.mycurrency.pojo.CurrencyItem
import ru.tatuna.mycurrency.repositories.CurrencyListRepository
import ru.tatuna.mycurrency.service.CurrencyService
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
            route = "item_info/{item_name}",
            arguments = listOf(navArgument("item_name") { type = NavType.StringType })
        ) {
            it.arguments?.getString("item_name")?.let { it1 -> ItemInfoScreen(it1) }
        }
    }
}

@Composable
fun ItemListScreen(
    navController: NavHostController,
    viewModel: CurrencyListViewModel,
    currencyList: List<CurrencyItem>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        currencyList.forEach {
            Row(
                modifier = Modifier
                    .clickable(onClick = { navController.navigate("item_info/${it.name}") })
                    .background(color = colorResource(id = R.color.grey_300))
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(text = it.name, fontSize = 20.sp, fontWeight = FontWeight(500))
                Text(text = "$ ${it.value}", modifier = Modifier.offset(x = 8.dp))
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
fun ItemInfoScreen(itemName: String) {
    Text(text = itemName)
}