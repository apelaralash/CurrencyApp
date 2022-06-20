package ru.tatuna.mycurrency.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.util.Range
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec.*
import androidx.core.content.ContextCompat
import ru.tatuna.mycurrency.R
import ru.tatuna.mycurrency.pojo.HistoryValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CurrencyHistoryGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // paints для текстов, присутствующих на графике
    private val periodNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.graph_period_text_size)
        color = Color.BLACK
    }

    private val currencyNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.graph_currency_text_size)
    }

    // Paint для рисования линий графика
    private val historyPointLinePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.purple_500)
        strokeWidth = 10F
        isAntiAlias = true
    }

    // Ограничения вьюхи
    private val axisLinePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.grey_300)
        strokeWidth = 3F
    }

    // Обводка и заполнение точки, обозначающей выбранное значение на графике
    private val currentHistoryPointPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.purple_500)
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    private val currentHistoryPointFillPaint = Paint().apply {
        color = Color.WHITE
    }

    // Переменные с данными, необходимыми для отображения сверху вьюхи
    private var currencyName: String = ""
    private var currentCurrencyValue: String = ""
    private var currentData: String? = null
    // Размеры нарисованных текстов
    private var currencyNameWidth: Float = 0.0F
    private var currentCurrencyValueWidth: Float = 0.0F
    private var currentDataWidth: Float = 0.0F
    // Коллекции с историей для рисования графика
    private var historyList: List<HistoryValue> = emptyList()
    private val uiHistoryList: MutableList<UiHistoryPoint> = mutableListOf()
    // Коллекции для рисования х и у подписей осей
    private var periods: MutableList<String> = mutableListOf()
    private var currencyValues: MutableList<String> = mutableListOf()

    // Значения последнего эвента
    private val lastPoint = PointF()
    private var lastPointerId = 0
    private var currentPoint: UiHistoryPoint? = null

    // Минимальные и максмальные координаты для расчёта правильного размера и положения графа
    private var maxY: Float = 0.0F
    private var minY: Float = 0.0F
    private var valueOfDivision: Float = 0.0F

    // Ширина контента и одного деления
    private var contentWidth = resources.getDimensionPixelSize(R.dimen.graph_period_width)
    private var periodWidth: Int = 0
    private val periodWidthFloat
        get() = periodWidth.toFloat()
    private val halfOfPeriodWidth
        get() = periodWidthFloat / 2

    fun setCurrencyHistory(history: List<HistoryValue>) {
        if (history != historyList) {
            getGraphData(history)

            // uiHistoryList необходим для быстрой отрисовки графика и точек
            history.forEachIndexed { index, historyValue ->
                uiHistoryList.add(UiHistoryPoint(historyValue, index))
            }
            // historyList используется для отображения значения и даты валюты над графиком
            historyList = history
            currencyName = historyList[0].name
            currencyNameWidth = currencyNamePaint.measureText(currencyName)
            currentCurrencyValue = "${historyList.last().value} $"
            // вызываем перерисовку
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        contentWidth = if (getMode(widthMeasureSpec) == UNSPECIFIED) {
            contentWidth
        } else {
            getSize(widthMeasureSpec) - paddingStart - paddingEnd
        }
        val height = contentWidth + contentWidth / 12

        setMeasuredDimension(contentWidth, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        periodWidth = contentWidth / 12
        uiHistoryList.forEach { it.countCoordinates() }
    }

    override fun onDraw(canvas: Canvas) = with(canvas) {
        drawPeriods()
        drawGraph()
        drawCurrentPoint()
        drawCurrencyValueAndDate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        return if (event.pointerCount == 1) processMove(event) else false
    }

    private fun processMove(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastPoint.set(event.x, event.y)
                lastPointerId = event.getPointerId(0)
                getCurrentPointPosition()
                invalidate()
                true
            }

            MotionEvent.ACTION_MOVE -> {
                lastPoint.set(event.x, event.y)
                val pointerId = event.getPointerId(0)
                if (lastPointerId == pointerId) {
                    getCurrentPointPosition()
                    invalidate()
                }
                lastPointerId = event.getPointerId(0)

                true
            }

            else -> false
        }
    }

    private fun Canvas.drawPeriods() {
        drawLine(
            0.0F,
            periodWidthFloat,
            periodWidthFloat * 12,
            periodWidthFloat,
            axisLinePaint
        )
        drawLine(
            0.0F,
            periodWidthFloat * 13 - 3,
            periodWidthFloat * 12,
            periodWidthFloat * 13 - 3,
            axisLinePaint
        )

        val dateY = periodNamePaint.getTextBaselineByCenter(periodWidth * 12 + periodWidth / 2f)
        periods.forEachIndexed { index, date ->
            val dateX = periodWidth * (2 * index + 3F) - periodNamePaint.measureText(date) / 2
            drawText(date, dateX, dateY, periodNamePaint)
        }
        val valueX = 0.5F * periodWidth
        currencyValues.forEachIndexed { index, value ->
            val valueY =
                periodNamePaint.getTextBaselineByCenter(periodWidthFloat * 2 * index + periodWidthFloat * 1.5f)
            drawText(value, valueX, valueY, periodNamePaint)
        }

    }

    private fun Canvas.drawGraph() {
        uiHistoryList.forEachIndexed { index, currentDataPoint ->
            if (index < uiHistoryList.size - 1) {
                val nextDataPoint = uiHistoryList[index + 1]
                val startX = currentDataPoint.valueX
                val startY = currentDataPoint.valueY
                val endX = nextDataPoint.valueX
                val endY = nextDataPoint.valueY
                drawLine(startX, startY, endX, endY, historyPointLinePaint)
            }
        }
    }

    private fun Canvas.drawCurrentPoint() {
        currentPoint?.let { point ->
            drawCircle(point.valueX, point.valueY, 10F, currentHistoryPointPaint)
            drawCircle(point.valueX, point.valueY, 10F, currentHistoryPointFillPaint)
        }
    }

    private fun Canvas.drawCurrencyValueAndDate() {
        val textY = currencyNamePaint.getTextBaselineByCenter(halfOfPeriodWidth)

        drawText(currencyName, halfOfPeriodWidth, textY, currencyNamePaint)
        drawText(currentCurrencyValue, periodWidthFloat + currencyNameWidth, textY, periodNamePaint)
        currentData?.let {
            this.drawText(it, contentWidth - currentDataWidth - halfOfPeriodWidth, textY, periodNamePaint)
        }
    }

    private fun getCurrentPointPosition() {
        uiHistoryList.find { item -> lastPoint.x in getPointPeriod(item.valueX) }?.let {
            currentPoint = it
            currentCurrencyValue = "${currentPoint?.history?.value} $"
            currentCurrencyValueWidth = periodNamePaint.measureText(currentCurrencyValue)
            currentData = it.history.date.format(DateTimeFormatter.ofPattern("dd.MM.YYYY"))
            currentDataWidth = periodNamePaint.measureText(currentData)
        }
    }

    private fun getPointPeriod(point: Float): ClosedFloatingPointRange<Float> =
        (point - periodWidthFloat / 2)..(point + periodWidthFloat / 2)

    // Получаем минимальные и максимальные значения валюты и преобразуем данные в читабельный вид
    private fun getGraphData(history: List<HistoryValue>) {
        maxY = history.maxByOrNull { it.value }?.value ?: 0.0F
        minY = history.minByOrNull { it.value }?.value ?: 0.0F
        valueOfDivision = (maxY - minY) / 10
        history.forEachIndexed { index, value ->
            if (index % 2 == 1) periods.add(getDateString(value.date))
        }
        repeat(6) { currencyValues.add(formatToDecimal(maxY - it * 2 * valueOfDivision)) }
    }

    private fun getDateString(date: LocalDate): String =
        "${date.dayOfMonth}.${date.month.value}"

    private fun formatToDecimal(num: Float): String = "%.2f".format(num)

    private fun Paint.getTextBaselineByCenter(center: Float) = center - (descent() + ascent()) / 2

    inner class UiHistoryPoint(val history: HistoryValue, private val index: Int) {
        var valueX: Float = 0.0F
        var valueY: Float = 0.0F

        init {
            countCoordinates()
        }

        fun countCoordinates() {
            valueX = periodWidthFloat * (index + 2F)
            valueY = periodWidth * 11.5F - ((history.value - minY) / valueOfDivision * periodWidth)
        }
    }
}