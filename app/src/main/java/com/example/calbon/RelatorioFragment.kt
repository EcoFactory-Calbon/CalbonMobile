    package com.example.calbon

    import android.graphics.Color
    import android.os.Bundle
    import android.view.View
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.Fragment
    import com.github.mikephil.charting.charts.LineChart
    import com.github.mikephil.charting.charts.PieChart
    import com.github.mikephil.charting.components.XAxis
    import com.github.mikephil.charting.data.Entry
    import com.github.mikephil.charting.data.LineData
    import com.github.mikephil.charting.data.LineDataSet
    import com.github.mikephil.charting.data.PieData
    import com.github.mikephil.charting.data.PieDataSet
    import com.github.mikephil.charting.data.PieEntry
    import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

    class RelatorioFragment : Fragment(R.layout.fragment_relatorio) {

        // Rótulos do eixo X (meses simulados: Fev a Jul)
        private val chartLabels = listOf("Fev", "Mar", "Abr", "Mai", "Jun", "Jul")

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Referências aos gráficos
            val pieChart1 = view.findViewById<PieChart>(R.id.pieChart1)
            val pieChart2 = view.findViewById<PieChart>(R.id.pieChart2)
            val pieChart3 = view.findViewById<PieChart>(R.id.pieChart3)
            val lineChart = view.findViewById<LineChart>(R.id.lineChart)

            // O primeiro PieChart (74% no topo) está na estrutura de KPI Secundário no XML
            setupPieChart(pieChart1, 74f, "Aumento Lorem", "#B794F6")

            // Configura os Gráficos de Rosca/Donut de Categoria
            setupPieChart(pieChart2, 55f, "Eletrodomésticos", "#A07BFF") // Roxo Claro
            setupPieChart(pieChart3, 75f, "Meios de Transporte", "#9370DB") // Roxo Médio

            // Configura o Gráfico de Linha (o grande de Fev a Jul)
            setupLineChart(lineChart)
        }

        /**
         * Configura o PieChart como Donut com cor primária e texto central.
         */
        private fun setupPieChart(chart: PieChart, value: Float, label: String, colorHex: String) {
            val entries = listOf(
                PieEntry(value, ""),
                PieEntry(100f - value, "")
            )
            val dataSet = PieDataSet(entries, "")

            // Cores: PrimaryColor (Valor), Fundo Escuro #1A1A30 (Restante)
            dataSet.colors = listOf(Color.parseColor(colorHex), Color.parseColor("#1A1A30"))
            dataSet.setDrawValues(false)
            dataSet.sliceSpace = 0f

            chart.data = PieData(dataSet)
            chart.setDrawHoleEnabled(true)
            chart.holeRadius = 70f
            chart.transparentCircleRadius = 75f
            chart.setHoleColor(Color.TRANSPARENT)

            // Texto Central (Percentual)
            chart.centerText = String.format("%.0f%%", value)
            chart.setCenterTextColor(Color.WHITE)
            chart.setCenterTextSize(18f)

            // Remove elementos visuais
            chart.legend.isEnabled = false
            chart.description.isEnabled = false
            chart.setTouchEnabled(false)

            chart.invalidate()
        }

        /**
         * Configura o LineChart como Gráfico de Área Suave com Rótulos de Eixo X e Destaque (74%).
         */
        private fun setupLineChart(chart: LineChart) {
            // Dados simulados com 6 pontos para replicar o Fev a Jul
            // O último ponto é o pico com valor '74' (para bater com o rótulo da imagem)
            val entries = listOf(
                Entry(0f, 40f),
                Entry(1f, 55f),
                Entry(2f, 50f),
                Entry(3f, 65f),
                Entry(4f, 60f),
                Entry(5f, 74f) // Pico de Julho
            )

            val dataSet = LineDataSet(entries, "Evolução").apply {
                // Estilo da Linha (Roxo, Curva Suave)
                color = Color.parseColor("#B794F6")
                lineWidth = 3f
                setDrawCircles(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER // CHAVE: Linha Curva Suave
                setDrawValues(false)

                // Estilo da Área Preenchida (Gradiente)
                setDrawFilled(true)
                fillAlpha = 100
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_purple_area)
                if (drawable != null) {
                    fillDrawable = drawable
                }

                // Destaque (Linha Tracejada Vertical)
                setDrawVerticalHighlightIndicator(true)
                setDrawHorizontalHighlightIndicator(false)
                highLightColor = Color.parseColor("#FFFFFF") // Linha de destaque branca
            }

            val lineData = LineData(dataSet)
            chart.data = lineData

            // --- Configurações dos Eixos ---
            chart.setBackgroundColor(Color.TRANSPARENT)
            chart.setNoDataText("") // Remove a mensagem de 'Sem dados'

            // Eixo X (Meses)
            val xAxis = chart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(chartLabels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.parseColor("#CCCCCC")
            xAxis.setDrawGridLines(false)
            xAxis.axisLineColor = Color.parseColor("#333333") // Linha do eixo X
            xAxis.granularity = 1f

            // Eixos Y (Removidos)
            chart.axisLeft.isEnabled = false
            chart.axisRight.isEnabled = false
            chart.legend.isEnabled = false
            chart.description.isEnabled = false
            chart.setTouchEnabled(true)

            // --- Destaque Final (Balão 74% e Linha Tracejada) ---

            // Aplica o MarkerView customizado para o rótulo (74%)
            val marker = CustomMarkerView(requireContext(), R.layout.custom_marker_view)
            marker.chartView = chart
            chart.marker = marker

            // Força o destaque no último ponto (Julho, valor 74f)
            val lastEntryIndex = entries.lastIndex
            chart.highlightValue(entries[lastEntryIndex].x, 0)

            chart.invalidate()
        }
    }
