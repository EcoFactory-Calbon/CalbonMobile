package com.example.calbon

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.calbon.api.AtualizacaoPerfil
import com.example.calbon.api.RetrofitClient
import com.example.calbon.api.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import android.widget.ProgressBar
import com.example.calbon.utils.NotificationUtils


class InfoPessoaisActivity : AppCompatActivity(), ChangeUsernameDialogListener {

    private lateinit var nome_info: TextView
    private lateinit var nome_completo: TextView
    private lateinit var email: TextView
    private lateinit var email_info: TextView
    private lateinit var senha: TextView
    private lateinit var num_cracha: TextView
    private lateinit var cod_empresa: TextView
    private lateinit var progressBar: ProgressBar


    companion object {
        private const val TAG = "InfoPessoaisActivity"
    }

    private fun showChangeDialog(title: String, subtitle: String, field: String) {
        val dialog = ChangeUsernameDialogFragment()

        val bundle = Bundle().apply {
            putString("title", title)
            putString("subtitle", subtitle)
            putString("field", field)
        }
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "change$field")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info_pessoais)
        val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val senhaReal = prefs.getString("SENHA_REAL", "") ?: ""
        progressBar = findViewById(R.id.progressBar)

        nome_info = findViewById(R.id.nome_info)
        nome_completo = findViewById(R.id.nome_completo)
        email = findViewById(R.id.email)
        email_info = findViewById(R.id.email_info)
        senha = findViewById(R.id.senha)
        num_cracha = findViewById(R.id.num_cracha)
        cod_empresa = findViewById(R.id.cod_empresa)

        val editar_nome = findViewById<ImageView>(R.id.editar_nome)
        val editar_senha = findViewById<ImageView>(R.id.editar_senha)
        val voltar = findViewById<ImageView>(R.id.voltar)

        editar_nome.setOnClickListener {
            showChangeDialog(
                "Alterar Nome Completo",
                nome_completo.text.toString(),
                "nome_completo"
            )
        }

        editar_senha.setOnClickListener {
            showChangeDialog("Alterar Senha", senhaReal, "senha")
        }

        voltar.setOnClickListener { finish() }

        val numeroCracha = intent.getIntExtra("numeroCracha", -1)
        if (numeroCracha != -1) buscarUsuario(numeroCracha)
        else Log.e(TAG, "Nenhum número de crachá recebido!")
    }

    private fun buscarUsuario(cracha: Int) {
        progressBar.visibility = View.VISIBLE

        val api = RetrofitClient.getApiUsuario(this)
        lifecycleScope.launch {
            try {
                val resposta = withContext(Dispatchers.IO) { api.buscarPorCracha(cracha) }
                if (resposta.isSuccessful) {
                    resposta.body()?.firstOrNull()?.let { preencherCampos(it) } ?: mostrarErro()
                } else {
                    mostrarErro()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro na requisição", e)
                mostrarErro()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun preencherCampos(usuario: Usuario) {
        fun valorOuVazio(valor: String?) = valor ?: ""
        val nomeCompleto = "${valorOuVazio(usuario.nome)} ${valorOuVazio(usuario.sobrenome)}".trim()
        nome_info.text = nomeCompleto
        nome_completo.text = nomeCompleto
        email.text = valorOuVazio(usuario.email)
        email_info.text = valorOuVazio(usuario.email)  // Atualiza o novo TextView
        senha.text = "********"
        num_cracha.text = usuario.numeroCracha.toString()
        cod_empresa.text = usuario.id_Localizacao.toString()
    }

    private fun mostrarErro() {
        nome_info.text = "Erro ao carregar"
        nome_completo.text = "Erro ao carregar"
        email.text = "Erro ao carregar"
        email_info.text = "Erro ao carregar"  // Atualiza o novo TextView
        senha.text = "********"
        num_cracha.text = "N° do Crachá: -"
        cod_empresa.text = "Código da Empresa: -"
    }

    // Chamado pelo dialog
    override fun onFieldChanged(field: String, newValue: String) {
        Log.d(TAG, "Campo alterado: $field para $newValue")

        val partes = if (field == "nome_completo") newValue.split(" ") else emptyList()
        val atualizacao = AtualizacaoPerfil(
            nome = if (field == "nome_completo") partes.getOrNull(0) else null,
            sobrenome = if (field == "nome_completo") partes.drop(1).joinToString(" ") else null,
            email = if (field == "email") newValue else null,
            senha = if (field == "senha") newValue else null
        )

        // Atualiza UI imediatamente
        when (field) {
            "nome_completo" -> {
                nome_info.text = newValue
                nome_completo.text = newValue
            }

            "email" -> {
                email.text = newValue
                email_info.text = newValue
            }

            "senha" -> {
                senha.text = "********"
            }
        }

        // Atualiza o backend
        atualizarPerfil(atualizacao)

        // Atualiza o valor local da senha se foi alterada
        if (field == "senha") {
            val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
            prefs.edit().putString("SENHA_REAL", newValue).apply()
        }
    }


    private fun AtualizacaoPerfil.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        nome?.let { map["nome"] = it }
        sobrenome?.let { map["sobrenome"] = it }
        email?.let { map["email"] = it }
        senha?.let { map["senha"] = it }
        return map
    }

    private fun atualizarPerfil(atualizacao: AtualizacaoPerfil) {
        val api = RetrofitClient.getApiUsuario(this)
        val camposMap = atualizacao.toMap()

        lifecycleScope.launch {
            try {
                val resposta: Response<Void> =
                    withContext(Dispatchers.IO) { api.atualizarPerfil(camposMap) }

                if (resposta.isSuccessful) {
                    Log.d(TAG, "Perfil atualizado com sucesso")

                    // Pega o número do crachá do usuário logado
                    val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                    val numeroCracha = prefs.getInt("NUMERO_CRACHA", -1)

                    if (numeroCracha != -1) {
                        NotificationUtils.sendNotification(
                            this@InfoPessoaisActivity,
                            numeroCracha,
                            "Perfil atualizado",
                            "Suas informações foram alteradas com sucesso"
                        )
                    } else {
                        Log.e(TAG, "Número do crachá não encontrado para enviar notificação")
                    }

                } else {
                    Log.e(TAG, "Erro ao atualizar perfil: ${resposta.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exceção ao atualizar perfil", e)
            }
        }
    }
}