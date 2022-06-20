package ru.tatuna.mycurrency.pojo

data class CurrencyItemDetails(
    val name: String,
    val value: Float,
    val history: List<HistoryValue>
)
