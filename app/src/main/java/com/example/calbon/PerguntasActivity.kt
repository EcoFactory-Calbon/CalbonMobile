package com.example.calbon

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calbon.model.Pergunta
import com.example.calbon.model.RespostaEnvio
import com.example.calbon.model.RespostaItemEnvio
import com.example.calbon.model.ResultadoRecebido
import com.example.calbon.retrofit.RetrofitMongoClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.Locale

class PerguntasActivity : AppCompatActivity() {

    private lateinit var textPergunta: TextView
    private lateinit var btnProximo: Button

    private var listaPerguntas: List<Pergunta> = emptyList()
    private var indiceAtual: Int = 0
    private val respostasDoUsuario = mutableMapOf<Int, Int>()

    private val NUMERO_CRACHA = "12345-6"

    private lateinit var radioGroup: List<RadioButton>

    companion object {
        const val FORMULARIO_ENVIADO_OK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perguntas)

        textPergunta = findViewById(R.id.textPergunta)
        btnProximo = findViewById(R.id.btnProximo)

        radioGroup = listOf(
            findViewById(R.id.radio1),
            findViewById(R.id.radio2),
            findViewById(R.id.radio3),
            findViewById(R.id.radio4),
            findViewById(R.id.radio5)
        )

        setupRadioButtons()
        btnProximo.setOnClickListener { lidarComProximaPergunta() }

        carregarListaDePerguntas()
    }

    // ===============================================================
    // Carregar Perguntas
    // ===============================================================

    private fun carregarListaDePerguntas() {
        textPergunta.text = "Carregando perguntas..."
        btnProximo.isEnabled = false

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: Response<List<Pergunta>> = withContext(Dispatchers.IO) {
                    RetrofitMongoClient.instance.listarPerguntas()
                }

                if (response.isSuccessful && response.body() != null) {

                    var perguntasCompletas = response.body()!!
                    val MAX_PERGUNTAS = 5

                    if (perguntasCompletas.size > MAX_PERGUNTAS) {
                        perguntasCompletas = perguntasCompletas.subList(0, MAX_PERGUNTAS)
                    }

                    listaPerguntas = perguntasCompletas

                    if (listaPerguntas.isNotEmpty()) {
                        exibirPergunta(indiceAtual)
                    } else {
                        textPergunta.text = "Nenhuma pergunta encontrada."
                    }

                } else {
                    textPergunta.text = "Erro ao carregar perguntas: ${response.code()}"
                }

            } catch (e: Exception) {
                textPergunta.text = "Erro: Verifique a conexão."
                Log.e("API_CALL", "Erro: ${e.message}")
            }
        }
    }

    // ===============================================================
    // Enviar Resultado
    // ===============================================================

    private fun enviarResultado() {
        textPergunta.text = "Calculando e enviando resultados..."
        btnProximo.isEnabled = false

        val listaRespostasEnvio = respostasDoUsuario.map { (id, resposta) ->
            RespostaItemEnvio(idPergunta = id, resposta = resposta)
        }

        val dadosParaEnvio = RespostaEnvio(
            numeroCracha = NUMERO_CRACHA,
            respostas = listaRespostasEnvio
        )

        val emissaoCalculadaLocal = calcularEmissaoLocal(listaRespostasEnvio)
        Log.d("CALCULO_LOCAL", "Emissão Local: $emissaoCalculadaLocal")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: Response<ResultadoRecebido> = withContext(Dispatchers.IO) {
                    RetrofitMongoClient.instance.enviarRespostas(dadosParaEnvio)
                }

                if (response.isSuccessful && response.body() != null) {

                    Toast.makeText(this@PerguntasActivity, "Formulário Enviado com Sucesso!", Toast.LENGTH_LONG).show()

                    // ✅ SALVAR DATA DA ÚLTIMA RESPOSTA
                    val prefs = getSharedPreferences("formulario_prefs", MODE_PRIVATE)
                    prefs.edit().putLong("ultima_resposta", System.currentTimeMillis()).apply()

                    // ✅ Finaliza a Activity
                    setResult(FORMULARIO_ENVIADO_OK)
                    finish()

                } else {
                    Toast.makeText(this@PerguntasActivity, "Erro ao enviar: ${response.code()}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@PerguntasActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("API_ENVIO", "Erro: ${e.message}")
            }
        }
    }

    // ===============================================================
    // Cálculo Local
    // ===============================================================

    private fun calcularEmissaoLocal(respostas: List<RespostaItemEnvio>): Double {
        var emissaoTotal = 0.0

        for (respostaItem in respostas) {
            val pergunta = listaPerguntas.find { it.id == respostaItem.idPergunta }

            if (pergunta != null) {
                val pontuacaoBase = getPontuacaoParaResposta(respostaItem.resposta)
                val pesoCategoria = getPesoPorCategoria(pergunta.categoria)

                emissaoTotal += pontuacaoBase * pesoCategoria
            }
        }
        return emissaoTotal
    }

    private fun getPontuacaoParaResposta(resposta: Int): Double {
        return when (resposta) {
            1 -> 100.0
            2 -> 300.0
            3 -> 600.0
            4 -> 1000.0
            5 -> 1500.0
            else -> 0.0
        }
    }

    private fun getPesoPorCategoria(categoria: String): Double {
        return when (categoria.toLowerCase(Locale.ROOT)) {
            "transporte" -> 1.2
            "alimentação" -> 0.9
            "consumo" -> 0.7
            "energia" -> 0.8
            "conhecimento" -> 0.2
            else -> 0.0
        }
    }

    // ===============================================================
    // Fluxo das Perguntas
    // ===============================================================

    private fun exibirPergunta(indice: Int) {
        radioGroup.forEach { it.isEnabled = true }
        btnProximo.isEnabled = false

        if (indice in listaPerguntas.indices) {
            val perguntaAtual = listaPerguntas[indice]
            textPergunta.text = perguntaAtual.pergunta
            limparSelecaoRadioButtons()
        } else {
            textPergunta.text = "Fim! Enviando respostas..."
            btnProximo.isEnabled = false
            enviarResultado()
        }
    }

    private fun lidarComProximaPergunta() {
        if (indiceAtual < listaPerguntas.size) {
            val resposta = getRespostaSelecionada()

            if (resposta == 0) {
                Toast.makeText(this, "Selecione uma opção.", Toast.LENGTH_SHORT).show()
                return
            }

            val idPerguntaAtual = listaPerguntas[indiceAtual].id
            respostasDoUsuario[idPerguntaAtual] = resposta

            indiceAtual++
            exibirPergunta(indiceAtual)
        } else {
            enviarResultado()
        }
    }

    private fun setupRadioButtons() {
        radioGroup.forEachIndexed { index, radioButton ->
            radioButton.setOnClickListener {
                limparSelecaoRadioButtons()
                radioButton.isChecked = true
                btnProximo.isEnabled = true
            }
        }
    }

    private fun limparSelecaoRadioButtons() {
        radioGroup.forEach { it.isChecked = false }
    }

    private fun getRespostaSelecionada(): Int {
        radioGroup.forEachIndexed { index, button ->
            if (button.isChecked) return index + 1
        }
        return 0
    }
}
