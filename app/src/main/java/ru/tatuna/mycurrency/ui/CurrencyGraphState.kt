package ru.tatuna.mycurrency.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import ru.tatuna.mycurrency.pojo.HistoryValue

class CurrencyGraphState(private var historyValues: List<HistoryValue> = listOf()) {
    // размеры области для рисования
    private var viewWidth = 0f
    private var viewHeight = 0f
    private var periodSize = 0f

    // количество исторических поинтов
    private val countOfPoints = 10

    // минимальная и максимальная цены видимых свечей
    private val maxPrice by derivedStateOf { historyValues.maxOfOrNull { it.value } ?: 0f }
    private val minPrice by derivedStateOf { historyValues.minOfOrNull { it.value } ?: 0f }
    private val valueOfDivision by derivedStateOf { (maxPrice - minPrice) / countOfPoints }

    // список исторических точек для отображения
    val uiHistoryPoints by derivedStateOf {
        if (historyValues.isNotEmpty()) {
            historyValues.mapIndexed { index, item ->
                UiHistoryPoint(item, index, periodSize, minPrice, valueOfDivision)
            }
        } else {
            emptyList()
        }
    }

    fun setViewSize(width: Float) {
        viewWidth = width
        periodSize = width / 12f
        viewHeight = periodSize * 13f
    }

    fun getCurrencyValuesList() = List(6) { maxPrice - 2 * it * valueOfDivision }

    fun getCurrencyDateList() = List(5) { historyValues[2 * it + 1].date }

    fun getTopLineY() = periodSize

    fun getBottomLineY() = viewHeight
}