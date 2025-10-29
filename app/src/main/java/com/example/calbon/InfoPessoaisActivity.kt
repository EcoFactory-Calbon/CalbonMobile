package com.example.calbon

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.calbon.api.AtualizacaoPerfil
import com.example.calbon.api.RetrofitClient
import com.example.calbon.api.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.File

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
    private var cameraImageUri: Uri? = null

    companion object {
        private const val TAG = "InfoPessoaisActivity"
        const val PREFS_NAME = "APP_PREFS"
        const val IMAGE_URI_KEY = "USER_IMAGE_URI"
    }

    // === Lançadores ===

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleImageSelected(it) }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraImageUri != null) {
            handleImageSelected(cameraImageUri!!)
        } else {
            Log.e(TAG, "Captura da câmera falhou ou URI nula.")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Log.e(TAG, "Permissão negada pelo usuário.")
            return@registerForActivityResult
        }

        // Verifica qual ação o usuário escolheu (galeria ou câmera)
        when (lastAction) {
            ActionType.GALLERY -> pickImageLauncher.launch("image/*")
            ActionType.CAMERA -> openCamera()
            else -> {}
        }
    }

    private enum class ActionType { GALLERY, CAMERA, NONE }
    private var lastAction: ActionType = ActionType.NONE

    // === onCreate ===

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

        editar_nome.setOnClickListener {
            showChangeDialog("Alterar Nome Completo", nome_completo.text.toString(), "nome_completo")
        }
        editar_senha.setOnClickListener {
            val senhaReal = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString("SENHA_REAL", "") ?: ""
            showChangeDialog("Alterar Senha", senhaReal, "senha")
        }
        voltar.setOnClickListener { finish() }

        // Clique na imagem
        imageView.setOnClickListener { showImageSourceDialog() }

        // Carrega imagem já salva
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(IMAGE_URI_KEY, null)?.let { uriString ->
            Glide.with(this)
                .load(uriString)
                .transform(CircleCrop())
                .into(imageView)
        }

        val numeroCracha = intent.getIntExtra("numeroCracha", -1)
        if (numeroCracha != -1) buscarUsuario(numeroCracha)
    }

    // === Escolha de origem da imagem ===

    private fun showImageSourceDialog() {
        val options = arrayOf("Selecionar da Galeria", "Tirar Foto")

        AlertDialog.Builder(this)
            .setTitle("Escolher imagem de perfil")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        lastAction = ActionType.GALLERY
                        checkAndRequestPermission(getGalleryPermission())
                    }
                    1 -> {
                        lastAction = ActionType.CAMERA
                        checkAndRequestPermission(Manifest.permission.CAMERA)
                    }
                }
            }
            .show()
    }

    private fun getGalleryPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun checkAndRequestPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                when (lastAction) {
                    ActionType.GALLERY -> pickImageLauncher.launch("image/*")
                    ActionType.CAMERA -> openCamera()
                    else -> {}
                }
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    // === Abrir câmera ===

    private fun openCamera() {
        try {
            val imageFile = File.createTempFile("profile_", ".jpg", cacheDir)
            val imageUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                imageFile
            )
            cameraImageUri = imageUri
            takePictureLauncher.launch(imageUri)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao abrir câmera: ${e.message}")
        }
    }

    // === Upload e atualização ===

    // InfoPessoaisActivity.kt (Linhas 211 em diante, aproximadamente)

    private fun handleImageSelected(uri: Uri) {
        selectedImageUri = uri

        Glide.with(this)
            .load(uri)
            .transform(CircleCrop())
            .into(imageView)

        val uploadOptions = hashMapOf<String, String>("folder" to "user_profiles")

        try {
            MediaManager.get().upload(uri)
                .options(uploadOptions)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        // Opcional: Mostrar uma barra de progresso ou indicador de carregamento
                    }
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val cloudUrl = resultData?.get("secure_url") as? String
                        cloudUrl?.let { url ->
                            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                .edit()
                                .putString(IMAGE_URI_KEY, url)
                                .apply()
                            atualizarPerfil(AtualizacaoPerfil(fotoUrl = url))
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e(TAG, "Erro upload Cloudinary: ${error?.description}")

                        AlertDialog.Builder(this@InfoPessoaisActivity)
                            .setTitle("Erro no Upload")
                            .setMessage("Falha ao enviar a imagem. Detalhes: ${error?.description ?: "Erro desconhecido."}")
                            .setPositiveButton("OK", null)
                            .show()
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Cloudinary não inicializado: ${e.message}")

            AlertDialog.Builder(this)
                .setTitle("Erro de Configuração")
                .setMessage("O serviço de imagens não foi inicializado corretamente. Por favor, reinicie o aplicativo.")
                .setPositiveButton("OK", null)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Erro desconhecido durante o upload: ${e.message}", e)

            AlertDialog.Builder(this)
                .setTitle("Erro")
                .setMessage("Ocorreu um erro inesperado ao processar a imagem. Tente novamente.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    // === API ===

    private fun buscarUsuario(cracha: Int) {
        progressBar.visibility = android.view.View.VISIBLE
        val api = RetrofitClient.getApiUsuario(this)
        lifecycleScope.launch {
            try {
                val resposta = withContext(Dispatchers.IO) { api.buscarPorCracha(cracha) }
                if (resposta.isSuccessful) {
                    resposta.body()?.firstOrNull()?.let { preencherCampos(it) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro na requisição", e)
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

    private fun atualizarPerfil(atualizacao: AtualizacaoPerfil) {
        val api = RetrofitClient.getApiUsuario(this)
        val camposMap = mutableMapOf<String, Any>()
        atualizacao.nome?.let { camposMap["nome"] = it }
        atualizacao.sobrenome?.let { camposMap["sobrenome"] = it }
        atualizacao.email?.let { camposMap["email"] = it }
        atualizacao.senha?.let { camposMap["senha"] = it }
        atualizacao.fotoUrl?.let { camposMap["fotoUrl"] = it }

        lifecycleScope.launch {
            try {
                val resposta: Response<Void> = withContext(Dispatchers.IO) {
                    api.atualizarPerfil(camposMap)
                }
                if (!resposta.isSuccessful) Log.e(TAG, "Erro ao atualizar perfil")
            } catch (e: Exception) {
                Log.e(TAG, "Exceção ao atualizar perfil", e)
            }
        }
    }

    // === Diálogo de edição ===

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
}
