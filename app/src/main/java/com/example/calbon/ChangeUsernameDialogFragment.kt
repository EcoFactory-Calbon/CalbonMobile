package com.example.calbon

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.calbon.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ChangeUsernameDialogFragment : DialogFragment() {

    private lateinit var listener: ChangeUsernameDialogListener
    private val TAG = "ChangeUsernameDialog"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ChangeUsernameDialogListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "Activity não implementa ChangeUsernameDialogListener", e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_edit_data, container, false)

        val editText = view.findViewById<EditText>(R.id.editTextNewUsername)
        val btnConfirm = view.findViewById<Button>(R.id.buttonConfirm)
        val iconClose = view.findViewById<ImageView>(R.id.iconClose)
        val titleTextView = view.findViewById<TextView>(R.id.Title)
        val subtitleTextView = view.findViewById<TextView>(R.id.SubTitle)

        val title = arguments?.getString("title")
        val subtitle = arguments?.getString("subtitle")
        val field = arguments?.getString("field")
        if (field == "senha") {
            val prefs = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
            val senhaAtual = prefs.getString("SENHA_REAL", "") ?: ""
            editText.setText(senhaAtual)
        }


        titleTextView.text = title
        subtitleTextView.text = subtitle
        editText.hint = title
        editText.setText(subtitle)

        // Fechar diálogo
        iconClose.setOnClickListener { dismiss() }

        // Confirmar alteração
        btnConfirm.setOnClickListener {
            val newValue = editText.text.toString().trim()
            val token = arguments?.getString("token") ?: ""
            val userId = arguments?.getLong("userId") ?: 0L

            if (newValue.isEmpty()) {
                editText.error = "Por favor, digite um valor."
                return@setOnClickListener
            }

            val map: Map<String, Any> = mapOf(field!! to newValue as Any)
            Log.d(TAG, "Map de atualização: $map")

            if (token.isNotEmpty() && userId != 0L) {
                lifecycleScope.launch {
                    try {
                        Log.d(TAG, "Iniciando chamada de atualização no servidor...")
                        val resposta: Response<Void> = withContext(Dispatchers.IO) {
                            RetrofitClient.getApiUsuario(requireContext()).atualizarPerfil(map)
                        }
                        if (resposta.isSuccessful) {
                            Log.d(TAG, "Perfil atualizado com sucesso: ${resposta.code()}")
                            listener.onFieldChanged(field, newValue)
                            dismiss()
                        } else {
                            val erro = resposta.errorBody()?.string()
                            Log.e(TAG, "Erro ao atualizar perfil: $erro")
                            editText.error = "Erro ${resposta.code()} ao atualizar"
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Exceção ao atualizar perfil", e)
                        editText.error = "Falha na conexão"
                    }
                }
            } else {
                // Atualização local como fallback
                Log.d(TAG, "Atualização local (fallback) para $field: $newValue")
                listener.onFieldChanged(field, newValue)
                dismiss()
            }
        }


        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
