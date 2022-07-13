package ru.tatuna.mycurrency.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tatuna.mycurrency.pojo.HistoryValue
import ru.tatuna.mycurrency.ui.CurrencyGraphState
import ru.tatuna.mycurrency.ui.views.ErrorView
import ru.tatuna.mycurrency.ui.views.LoadingView
import ru.tatuna.mycurrency.viewmodels.CurrencyInfoViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ItemInfoScreen(itemName: String, itemValue: Float, viewModel: CurrencyInfoViewModel) {
//    val historyList = listOf(
//        HistoryValue(date = LocalDate.parse("2022-06-08"), name = "AED", value = 1F / 0.061741F),
//        HistoryValue(date = LocalDate.parse("2022-06-09"), name = "AED", value = 1F / 0.062741F),
//        HistoryValue(date = LocalDate.parse("2022-06-10"), name = "AED", value = 1F / 0.063741F),
//        HistoryValue(date = LocalDate.parse("2022-06-11"), name = "AED", value = 1F / 0.063742F),
//        HistoryValue(date = LocalDate.parse("2022-06-12"), name = "AED", value = 1F / 0.033017F),
//        HistoryValue(date = LocalDate.parse("2022-06-13"), name = "AED", value = 1F / 0.063603F),
//        HistoryValue(date = LocalDate.parse("2022-06-14"), name = "AED", value = 1F / 0.062922F),
//        HistoryValue(date = LocalDate.parse("2022-06-15"), name = "AED", value = 1F / 0.064159F),
//        HistoryValue(date = LocalDate.parse("2022-06-16"), name = "AED", value = 1F / 0.064019F),
//        HistoryValue(date = LocalDate.parse("2022-06-17"), name = "AED", value = 1F / 0.065153F)
//    )
    val viewState = viewModel.currencyHistory.observeAsState()

    when (val state = viewState.value) {
        CurrencyInfoViewModel.State.Error -> ErrorView { viewModel.loadCurrencyHistory(itemName) }
        CurrencyInfoViewModel.State.Loading -> LoadingView()
        is CurrencyInfoViewModel.State.Success -> {
            ItemInfoSuccess(state.data.history, itemName, itemValue)
        }
    }

    LaunchedEffect(key1 = viewState, block = { viewModel.loadCurrencyHistory(itemName) })
}

fun DrawScope.drawTopText(
    name: String,
    value: Float
) {
    val itemNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 56.sp.value
        typeface = Typeface.DEFAULT_BOLD
    }

    val nameWidth = itemNamePaint.measureText(name)
    val nameBaseline = itemNamePaint.descent() - itemNamePaint.ascent()

    drawIntoCanvas {
        it.nativeCanvas.apply {
            drawText(name, 10.0f, nameBaseline, itemNamePaint)
            drawText("$$value", 30.0f + nameWidth, nameBaseline, Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 34.sp.value })
        }
    }
}

@Composable
fun ItemInfoSuccess(historyList: List<HistoryValue>, itemName: String, itemValue: Float) {
    val pointOffset: MutableState<Offset?> = mutableStateOf(null)
    val itemValueState: MutableState<Float> = mutableStateOf(0.0F)
    val state = CurrencyGraphState(historyList)

    itemValueState.value = itemValue
    val modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures {
                pointOffset.value = it
            }
        }
    Canvas(modifier = modifier) {
        state.setViewSize(size.width)

        drawTopText(itemName, itemValueState.value)
        drawLine(
            color = Color.Gray,
            strokeWidth = 3.dp.value,
            start = Offset(0f, state.getTopLineY()),
            end = Offset(size.width, state.getTopLineY())
        )

        drawLine(
            color = Color.Gray,
            strokeWidth = 3.dp.value,
            start = Offset(0f, state.getBottomLineY()),
            end = Offset(size.width, state.getBottomLineY())
        )

        for (i in 1 until state.uiHistoryPoints.size) {
            val curItem = state.uiHistoryPoints[i]
            val prevItem = state.uiHistoryPoints[i - 1]
            drawLine(
                color = Color.Blue,
                strokeWidth = 10.dp.value,
                start = Offset(prevItem.realX, prevItem.realY),
                end = Offset(curItem.realX, curItem.realY)
            )
        }

        val periodNamePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        periodNamePaint.textSize = 34.sp.value
        state.getCurrencyDateList().forEachIndexed { index, date ->
            drawIntoCanvas { canvas ->
                val text = date.format(DateTimeFormatter.ofPattern("dd.MM"))
                canvas.nativeCanvas.drawText(
                    text,
                    state.getTopLineY() * (2 * index + 3F) - periodNamePaint.measureText(text) / 2,
                    state.getTopLineY() * 12.5f,
                    periodNamePaint
                )
            }
        }

        state.getCurrencyValuesList().forEachIndexed { index, item ->
            drawIntoCanvas { canvas ->
                val text = "%.3f".format(item)
                canvas.nativeCanvas.drawText(
                    text,
                    state.getTopLineY() * 0.5f,
                    state.getTopLineY() * (2 * index + 1.5f),
                    periodNamePaint
                )
            }
        }

        pointOffset.value?.let { offset ->
            state.uiHistoryPoints.forEach { uiHistoryPoint ->
                if (offset.x in (uiHistoryPoint.realX - state.getTopLineY() / 2)..(uiHistoryPoint.realX + state.getTopLineY() / 2)) {
                    drawCircle(
                        color = Color.Blue,
                        radius = 14f,
                        center = Offset(uiHistoryPoint.realX, uiHistoryPoint.realY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 7f,
                        center = Offset(uiHistoryPoint.realX, uiHistoryPoint.realY)
                    )
                    itemValueState.value = uiHistoryPoint.history.value
                }
            }
        }
    }
}