package ru.tatuna.mycurrency.ui.views.success

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import java.time.format.DateTimeFormatter

@Composable
fun ItemInfoSuccessView(historyList: List<HistoryValue>, itemName: String, itemValue: Float) {
    val pointOffset: MutableState<Offset?> = mutableStateOf(null)
    val itemValueState: MutableState<Float> = mutableStateOf(0.0F)
    val state = CurrencyGraphState(historyList)

    itemValueState.value = itemValue

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures {
                pointOffset.value = it
            }
        }
    ) {
        state.setViewSize(size.width)

        drawTopText(itemName, itemValueState.value)
        drawAxisAndValues(state)

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

        pointOffset.value?.let { offset ->
            state.uiHistoryPoints.forEach { uiHistoryPoint ->
                if (offset.x in (uiHistoryPoint.realX - state.getPeriodSize() / 2)..(uiHistoryPoint.realX + state.getPeriodSize() / 2)) {
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
            drawText(
                "$$value",
                30.0f + nameWidth,
                nameBaseline,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 34.sp.value })
        }
    }
}

fun DrawScope.drawAxisAndValues(state: CurrencyGraphState) {
    val periodNamePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    periodNamePaint.textSize = 34.sp.value

    val periodSize = state.getPeriodSize()

    drawLine(
        color = Color.Gray,
        strokeWidth = 3.dp.value,
        start = Offset(0f, state.getPeriodSize()),
        end = Offset(size.width, state.getPeriodSize())
    )

    drawLine(
        color = Color.Gray,
        strokeWidth = 3.dp.value,
        start = Offset(0f, state.getBottomLineY()),
        end = Offset(size.width, state.getBottomLineY())
    )

    state.getCurrencyDateList().forEachIndexed { index, date ->
        drawIntoCanvas { canvas ->
            val text = date.format(DateTimeFormatter.ofPattern("dd.MM"))
            canvas.nativeCanvas.drawText(
                text,
                periodSize * (2 * index + 3F) - periodNamePaint.measureText(text) / 2,
                periodSize * 12.5f,
                periodNamePaint
            )
        }
    }

    state.getCurrencyValuesList().forEachIndexed { index, item ->
        drawIntoCanvas { canvas ->
            val text = "%.3f".format(item)
            canvas.nativeCanvas.drawText(
                text,
                periodSize * 0.5f,
                periodSize * (2 * index + 1.5f),
                periodNamePaint
            )
        }
    }
}