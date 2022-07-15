package ru.tatuna.mycurrency.ui.views.success

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun ItemListSuccessView(historyList: List<CurrencyItem>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        historyList.forEach { currencyItem ->
            item {
                Row(
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("item_info/${currencyItem.name}/${currencyItem.value}") })
                        .background(color = colorResource(id = R.color.grey_300))
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = currencyItem.name, fontSize = 20.sp, fontWeight = FontWeight(500))
                    Text(text = "$ ${currencyItem.value}", modifier = Modifier.offset(x = 8.dp, y = 4.dp))
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}