package com.example.calbon

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.calbon.api.AtualizacaoPerfil
import com.example.calbon.api.RetrofitClient
import com.example.calbon.api.Usuario
import com.example.calbon.utils.NotificationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class InfoPessoaisActivity : AppCompatActivity(), ChangeUsernameDialogListener {

    private lateinit var nome_info: TextView
    private lateinit var nome_completo: TextView
    private lateinit var email: TextView
    private lateinit var email_info: TextView
    private lateinit var senha: TextView
    private lateinit var num_cracha: TextView
    private lateinit var cod_empresa: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView

    private var selectedImageUri: Uri? = null

    companion object {
        private const val TAG = "InfoPessoaisActivity"
        const val PREFS_NAME = "APP_PREFS"
        const val IMAGE_URI_KEY = "USER_IMAGE_URI"
    }

    // Permissão para acessar galeria
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) pickImageLauncher.launch("image/*")
        else Log.e(TAG, "Permissão negada")
    }

    // Abrir galeria para selecionar imagem
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleImageSelected(it) }
    }

    private fun handleImageSelected(uri: Uri) {
        selectedImageUri = uri

        // Mostra imagem no ImageView
        Glide.with(this)
            .load(uri)
            .transform(CircleCrop())
            .into(imageView)

        // Upload para Cloudinary
        val uploadOptions = hashMapOf<String, String>("folder" to "user_profiles")
        MediaManager.get().upload(uri)
            .options(uploadOptions)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val cloudUrl = resultData?.get("secure_url") as? String
                    cloudUrl?.let { url ->
                        // Salva URL do Cloudinary em SharedPreferences
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putString(IMAGE_URI_KEY, url)
                            .apply()

                        // Atualiza backend
                        val atualizacao = AtualizacaoPerfil(fotoUrl = url)
                        atualizarPerfil(atualizacao)
                    }
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e(TAG, "Erro upload Cloudinary: ${error?.description}")
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info_pessoais)

        progressBar = findViewById(R.id.progressBar)
        imageView = findViewById(R.id.imageView)
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

        editar_nome.setOnClickListener { showChangeDialog("Alterar Nome Completo", nome_completo.text.toString(), "nome_completo") }
        editar_senha.setOnClickListener {
            val senhaReal = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString("SENHA_REAL", "") ?: ""
            showChangeDialog("Alterar Senha", senhaReal, "senha")
        }
        voltar.setOnClickListener { finish() }

        // Clique no ImageView
        imageView.setOnClickListener {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*")
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }

        // Carrega imagem persistida
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(IMAGE_URI_KEY, null)?.let { uriString ->
            Glide.with(this)
                .load(uriString)
                .transform(CircleCrop())
                .into(imageView)
        }

        val numeroCracha = intent.getIntExtra("numeroCracha", -1)
        if (numeroCracha != -1) buscarUsuario(numeroCracha)
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

    private fun buscarUsuario(cracha: Int) {
        progressBar.visibility = android.view.View.VISIBLE
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
                progressBar.visibility = android.view.View.GONE
            }
        }
    }

    private fun preencherCampos(usuario: Usuario) {
        nome_info.text = "${usuario.nome} ${usuario.sobrenome}"
        nome_completo.text = "${usuario.nome} ${usuario.sobrenome}"
        email.text = usuario.email
        email_info.text = usuario.email
        senha.text = "********"
        num_cracha.text = usuario.numeroCracha.toString()
        cod_empresa.text = usuario.id_Localizacao.toString()

        usuario.fotoUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .transform(CircleCrop())
                .into(imageView)
        }
    }

    private fun mostrarErro() {
        nome_info.text = "Erro"
        nome_completo.text = "Erro"
        email.text = "Erro"
        email_info.text = "Erro"
        senha.text = "********"
        num_cracha.text = "-"
        cod_empresa.text = "-"
        imageView.setImageResource(R.drawable.circle_border)
    }

    override fun onFieldChanged(field: String, newValue: String) {
        val partes = if (field == "nome_completo") newValue.split(" ") else emptyList()
        val atualizacao = AtualizacaoPerfil(
            nome = if (field == "nome_completo") partes.getOrNull(0) else null,
            sobrenome = if (field == "nome_completo") partes.drop(1).joinToString(" ") else null,
            email = if (field == "email") newValue else null,
            senha = if (field == "senha") newValue else null
        )
        atualizarPerfil(atualizacao)
    }

    // Função de extensão para converter AtualizacaoPerfil em Map
    private fun AtualizacaoPerfil.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        nome?.let { map["nome"] = it }
        sobrenome?.let { map["sobrenome"] = it }
        email?.let { map["email"] = it }
        senha?.let { map["senha"] = it }
        fotoUrl?.let { map["fotoUrl"] = it }
        return map
    }

    private fun atualizarPerfil(atualizacao: AtualizacaoPerfil) {
        val api = RetrofitClient.getApiUsuario(this)
        val camposMap = atualizacao.toMap()
        lifecycleScope.launch {
            try {
                val resposta: Response<Void> = withContext(Dispatchers.IO) { api.atualizarPerfil(camposMap) }
                if (!resposta.isSuccessful) Log.e(TAG, "Erro ao atualizar perfil")
            } catch (e: Exception) {
                Log.e(TAG, "Exceção ao atualizar perfil", e)
            }
        }
    }
}
