package ru.tatuna.mycurrency.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ru.tatuna.mycurrency.R
import ru.tatuna.mycurrency.pojo.CurrencyItem
import ru.tatuna.mycurrency.viewmodels.CurrencyListViewModel

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
                    .clickable(onClick = { navController.navigate("item_info/${it.name}/${it.value}") })
                    .background(color = colorResource(id = R.color.grey_300))
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = it.name, fontSize = 20.sp, fontWeight = FontWeight(500))
                Text(text = "$ ${it.value}", modifier = Modifier.offset(x = 8.dp, y = 4.dp))
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}