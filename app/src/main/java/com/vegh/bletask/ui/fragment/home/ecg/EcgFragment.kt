package com.vegh.bletask.ui.fragment.home.ecg

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope


import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.vegh.bletask.MainActivity
import com.vegh.bletask.R
import com.vegh.bletask.utils.Filters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class EcgFragment : Fragment() {
    private lateinit var graphFiltered: GraphView
    private val seriesFiltered = LineGraphSeries<DataPoint>()
    private lateinit var viewportFiltered: Viewport
    private val lastDataRaw = IntArray(513)
    private val lastDataFiltered = DoubleArray(513)
    private var X_Axis_Variable = 0.0
    private var firstSamples = 0

    private val viewModel: EcgViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ecg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView(view)
        collectEcgData()
    }

    private fun initializeView(view: View) {
        graphFiltered = view.findViewById(R.id.graph)
        seriesFiltered.thickness = 3
        seriesFiltered.color = Color.parseColor("#00ff00")
        graphFiltered.addSeries(seriesFiltered)
        graphFiltered.gridLabelRenderer.isHorizontalLabelsVisible = true
        graphFiltered.gridLabelRenderer.isVerticalLabelsVisible = false
        graphFiltered.setBackgroundColor(resources.getColor(R.color.black))
        viewportFiltered = graphFiltered.viewport
        viewportFiltered.isYAxisBoundsManual = true
        viewportFiltered.setMinY(-128.0)
        viewportFiltered.setMaxY(128.0)
        viewportFiltered.isScalable = true

        view.findViewById<View>(R.id.back).setOnClickListener {
            MainActivity.self.navController?.navigateUp()
        }
    }

    private fun addEntry(dataX: Int, time: Double) {
        X_Axis_Variable += time / 1000
        var minY = dataX
        var maxY = dataX
        var minF = 0.0
        var maxF = 0.0
        var dataFX = 0.0

        if (firstSamples < 512) {
            firstSamples++
            for (i in 0 until 512) {
                lastDataRaw[i] = lastDataRaw[i + 1]
                if (lastDataRaw[i] > maxY) maxY = lastDataRaw[i]
                if (lastDataRaw[i] < minY) minY = lastDataRaw[i]
            }
            lastDataRaw[512] = dataX
            if (lastDataRaw[512] > maxY) maxY = lastDataRaw[512]
            if (lastDataRaw[512] < minY) minY = lastDataRaw[512]
            seriesFiltered.appendData(DataPoint(X_Axis_Variable - 1, dataX.toDouble()), true, 512 * 5)
            viewportFiltered.setMinY(-5.0)
            viewportFiltered.setMaxY(5.0)
        } else {
            for (i in 0 until 512) {
                lastDataRaw[i] = lastDataRaw[i + 1]
                if (lastDataRaw[i] > maxY) maxY = lastDataRaw[i]
                if (lastDataRaw[i] < minY) minY = lastDataRaw[i]
            }
            lastDataRaw[512] = dataX
            dataFX = 0.0
            for (i in 0 until 512) {
                dataFX += lastDataRaw[i] * Filters.EcgFilter[i]
            }
            maxF = dataFX
            minF = dataFX
            seriesFiltered.appendData(DataPoint(X_Axis_Variable - 1, dataFX), true, 5 * 256)
            for (i in 0 until 512) {
                lastDataFiltered[i] = lastDataFiltered[i + 1]
                if (lastDataFiltered[i] > maxF) maxF = lastDataFiltered[i]
                if (lastDataFiltered[i] < minF) minF = lastDataFiltered[i]
            }
            lastDataFiltered[512] = dataFX
            viewportFiltered.setMinY(minF - 50)
            viewportFiltered.setMaxY(maxF + 50)
        }
    }

private fun collectEcgData() {
    lifecycleScope.launch {
        viewModel.ecgData.collect { data ->
            val deltaTime = System.currentTimeMillis() - (X_Axis_Variable * 1000).toLong()
            val ECG_Temp = ShortArray(data.size / 2)
            for (i in data.indices step 2) {
                ECG_Temp[i / 2] =
                    ((data[i].toInt() and 0xFF) or ((data[i + 1].toInt() and 0xFF) shl 8)).toShort()
                addEntry(ECG_Temp[i / 2].toInt(), deltaTime / ECG_Temp.size.toDouble())
            }
        }
    }
}
}
