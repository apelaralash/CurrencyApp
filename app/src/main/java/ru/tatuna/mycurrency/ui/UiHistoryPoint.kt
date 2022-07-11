package ru.tatuna.mycurrency.ui

import ru.tatuna.mycurrency.pojo.HistoryValue

class UiHistoryPoint(
    val history: HistoryValue,
    index: Int,
    periodWidth: Float,
    minCurrencyValue: Float,
    valueOfDivision: Float
) {
    var realX: Float = periodWidth * (index + 2F)
    var realY: Float = periodWidth * 11.5F - ((history.value - minCurrencyValue) / valueOfDivision * periodWidth)
}