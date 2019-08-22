package com.mxt.anitrend.util

import android.content.Context
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mxt.anitrend.R
import com.mxt.anitrend.extension.getCompatColorAttr

/**
 * Created by max on 2018/02/24.
 */

class ChartUtil {

    class StepXAxisFormatter<K> : ValueFormatter() {

        private lateinit var dataModel: List<K>
        private lateinit var chartBase: BarLineChartBase<*>

        fun setChartBase(chartBase: BarLineChartBase<*>): StepXAxisFormatter<*> {
            this.chartBase = chartBase
            return this
        }

        fun setDataModel(dataModel: List<K>): StepXAxisFormatter<K> {
            this.dataModel = dataModel
            return this
        }

        /**
         * Called when a value from an axis is to be formatted
         * before being drawn. For performance reasons, avoid excessive calculations
         * and memory allocations inside this method.
         *
         * @param value the value to be formatted
         * @param axis  the axis the value belongs to
         * @return formatted label value
         */
        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            val index = value.toInt()
            val model = dataModel[index]
            return model.toString()
        }

        fun build(context: Context) {
            with (chartBase.xAxis) {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                if (dataModel.size <= 10)
                    labelCount = dataModel.size
                textColor = context.getCompatColorAttr(R.attr.titleColor)
                valueFormatter = this@StepXAxisFormatter
            }
        }
    }

    class StepYAxisFormatter : ValueFormatter() {

        private lateinit var chartBase: BarLineChartBase<*>

        fun setChartBase(chartBase: BarLineChartBase<*>): StepYAxisFormatter {
            this.chartBase = chartBase
            return this
        }

        /**
         * Called when a value from an axis is to be formatted
         * before being drawn. For performance reasons, avoid excessive calculations
         * and memory allocations inside this method.
         *
         * @param value the value to be formatted
         * @param axis  the axis the value belongs to
         * @return formatted label value
         */
        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            val formatted = value.toInt()
            return formatted.toString()
        }

        fun build(context: Context) {
            chartBase.legend.textColor = context.getCompatColorAttr(R.attr.titleColor)

            with (chartBase.axisLeft) {
                setLabelCount(5, false)
                spaceTop = 15f
                axisMinimum = 0f
                disableGridDashedLine()
                disableAxisLineDashedLine()
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                textColor = context.getCompatColorAttr(R.attr.titleColor)
                valueFormatter = this@StepYAxisFormatter
            }
            chartBase.axisRight.isEnabled = false
        }
    }
}
