package ru.tatuna.mycurrency.pojo

import java.time.LocalDate

data class HistoryValue(
    val name: String,
    val date: LocalDate,
    val value: Float
)
