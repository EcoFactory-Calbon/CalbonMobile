package com.example.calbon

import PercentMarkerView
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.calbon.databinding.FragmentRelatorioBinding
import com.example.calbon.model.ResultadoRecebido
import com.example.calbon.model.RespostaItemEnvio
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Locale

class RelatorioFragment : Fragment(R.layout.fragment_relatorio) {

    private var _binding: FragmentRelatorioBinding? = null
    private val binding get() = _binding!!

    private val chartLabels = listOf("Jul", "Ago", "Set", "Out") // meses simulados

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRelatorioBinding.bind(view)

        // Simulando o objeto recebido do servidor
        val resultado = ResultadoRecebido(
            id = "68ed6a5e3fe1079b54765c20",
            numeroCracha = "12",
            dataResposta = "2025-10-13T18:08:46.335",
            nivelEmissao = 3.4,
            classificacaoEmissao = "Médio",
            respostas = listOf(
                RespostaItemEnvio(5, 3),  // Eletrodomésticos
                RespostaItemEnvio(13, 4), // Eletrodomésticos
                RespostaItemEnvio(30, 2), // Eletrodomésticos
                RespostaItemEnvio(35, 5), // Eletrodomésticos
                RespostaItemEnvio(3, 4),  // Transporte
                RespostaItemEnvio(16, 3), // Transporte
                RespostaItemEnvio(27, 5), // Transporte
                RespostaItemEnvio(39, 2), // Transporte
                RespostaItemEnvio(6, 4),  // Alimentação
                RespostaItemEnvio(10, 3), // Alimentação
                RespostaItemEnvio(15, 2), // Alimentação
                RespostaItemEnvio(20, 5)  // Energia
            )
        )

        try {
            // Calcular percentuais por categoria
            val categoriaPercentual = calcularCategorias(resultado)

            // 1. Preencher o novo Bloco de KPI
            binding.kpiValor?.text = String.format(Locale.getDefault(), "%.1f", resultado.nivelEmissao)
            binding.kpiClassificacao?.text = resultado.classificacaoEmissao

            // Opcional: Mudar cor do "badge" de classificação
            val (corFundo, corTexto) = when (resultado.classificacaoEmissao.lowercase()) {
                "médio" -> Pair(Color.parseColor("#FFC107"), Color.BLACK)
                "alto" -> Pair(Color.parseColor("#F44336"), Color.WHITE)
                "baixo" -> Pair(Color.parseColor("#4CAF50"), Color.WHITE)
                else -> Pair(Color.GRAY, Color.WHITE)
            }
            binding.kpiClassificacao?.setBackgroundColor(corFundo)
            binding.kpiClassificacao?.setTextColor(corTexto)

            // 2. Configurar o novo Gráfico de Barras
            setupBarChart(binding.barChartCategorias, categoriaPercentual)

            // 3. Gráfico de linha simulando evolução do nível de emissão
            val valoresLinha = listOf(2.5f, 3.0f, 3.2f, resultado.nivelEmissao.toFloat())
            setupLineChart(binding.lineChart, valoresLinha)

            val marker = PercentMarkerView(requireContext(), R.layout.marker_view)
            binding.lineChart.marker = marker

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Erro ao gerar gráficos: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun calcularCategorias(form: ResultadoRecebido): Map<String, Float> {
        val eletro = listOf(5, 13, 30, 35)
        val transporte = listOf(3, 16, 27, 39)
        val alimentacao = listOf(6, 10, 15)
        val energia = listOf(20)

        var somaEle = 0
        var somaTrans = 0
        var somaAlim = 0
        var somaEner = 0

        form.respostas.forEach {
            when (it.idPergunta) {
                in eletro -> somaEle += it.resposta
                in transporte -> somaTrans += it.resposta
                in alimentacao -> somaAlim += it.resposta
                in energia -> somaEner += it.resposta
            }
        }

        val total = somaEle + somaTrans + somaAlim + somaEner
        if (total == 0) return mapOf(
            "Eletrodomésticos" to 0f,
            "Transporte" to 0f,
            "Alimentação" to 0f,
            "Energia" to 0f
        )

        return mapOf(
            "Eletrodomésticos" to (somaEle * 100f / total),
            "Transporte" to (somaTrans * 100f / total),
            "Alimentação" to (somaAlim * 100f / total),
            "Energia" to (somaEner * 100f / total)
        )
    }

    // FUNÇÃO setupBarChart ATUALIZADA para aceitar BarChart? e evitar o erro de tipo.
    private fun setupBarChart(chart: BarChart?, data: Map<String, Float>) {
        // Mapeia os dados para uma ordem fixa
        val labels = listOf("Eletrodomésticos", "Transporte", "Alimentação", "Energia")

        val entries = ArrayList<BarEntry>()
        val colors = ArrayList<Int>()

        labels.forEachIndexed { index, label ->
            val value = data[label] ?: 0f
            entries.add(BarEntry(index.toFloat(), value))

            // Reutilizando as cores que você já tinha
            when (label) {
                "Eletrodomésticos" -> colors.add(Color.parseColor("#A07BFF"))
                "Transporte" -> colors.add(Color.parseColor("#9370DB"))
                "Alimentação" -> colors.add(Color.parseColor("#FFC107"))
                "Energia" -> colors.add(Color.parseColor("#F44336"))
                else -> colors.add(Color.GRAY)
            }
        }

        val dataSet = BarDataSet(entries, "Categorias").apply {
            this.colors = colors
            setDrawValues(true)
            valueTextColor = Color.WHITE
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}%"
                }
            }
        }

        // Aplica as configurações SÓ SE o chart não for nulo
        chart?.apply {
            setData(BarData(dataSet))
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)

            // Eixo X (Labels)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
                setDrawGridLines(false)
                granularity = 1f
                isGranularityEnabled = true
            }

            // Eixo Y (Valores)
            axisLeft.apply {
                textColor = Color.WHITE
                axisMinimum = 0f
                axisMaximum = 100f // Nossos valores são percentuais
                setDrawGridLines(true)
                gridColor = Color.parseColor("#555555") // Linha de grade sutil
            }
            axisRight.isEnabled = false // Remove o eixo da direita

            animateY(1000)
            invalidate()
        }
    }

    // Função do Gráfico de Linha (inalterada)
    private fun setupLineChart(chart: LineChart, valores: List<Float>) {
        val entries = valores.mapIndexed { index, v -> Entry(index.toFloat(), v) }
        val dataSet = LineDataSet(entries, "").apply {
            color = Color.parseColor("#B794F6")
            lineWidth = 3f
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            setDrawCircles(true)
            setDrawValues(false)
            setDrawFilled(true)
            // Certifique-se de que este Drawable existe: R.drawable.gradient_purple_area
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_purple_area)
            highLightColor = Color.TRANSPARENT
        }

        chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(chartLabels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = Color.WHITE
                granularity = 1f
            }
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            setTouchEnabled(true)
            animateY(900)
            invalidate()
        }
    }

    // A função setupPieChart foi removida, pois não é mais usada.

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}