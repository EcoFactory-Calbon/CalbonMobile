package com.example.calbon

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

/**
 * Marker customizado para exibir o valor de cada ponto no LineChart.
 * Usa o layout custom_marker_view.xml como base visual.
 */
class CustomMarkerView(
    context: Context,
    layoutResource: Int = R.layout.custom_marker_view
) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        // Exibe o valor em formato de porcentagem (ex: 74%)
        tvContent.text = String.format("%.0f%%", e?.y ?: 0f)
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        // Centraliza o marcador acima do ponto
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}
