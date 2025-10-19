package com.example.calbon

import android.content.Context
import android.os.Bundle
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

class ChangeUsernameDialogFragment : DialogFragment() {

    private lateinit var listener: ChangeUsernameDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ChangeUsernameDialogListener
        } catch (e: ClassCastException) {
            // Tratamento de erro se a Activity/Fragment não implementar a interface
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

        // Lê os argumentos passados da Activity
        val title = arguments?.getString("title")
        val subtitle = arguments?.getString("subtitle")
        val field = arguments?.getString("field") // qual campo será atualizado

        // Atualiza título e subtítulo
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

            if (newValue.isNotEmpty()) {
                if (token.isNotEmpty() && userId != 0L) {
                    // Chamada para atualizar no servidor
                    lifecycleScope.launch {
                        try {
                            val map = mapOf(field!! to newValue)
                            val resposta = withContext(Dispatchers.IO) {
                                RetrofitClient.apiUsuario.atualizarUsuario("Bearer $token", userId, map)
                            }
                            if (resposta.isSuccessful) {
                                listener.onFieldChanged(field, newValue)
                                dismiss()
                            } else {
                                editText.error = "Erro ${resposta.code()} ao atualizar"
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            editText.error = "Falha na conexão"
                        }
                    }
                } else {
                    // fallback: apenas atualiza local
                    listener.onFieldChanged(field ?: "", newValue)
                    dismiss()
                }
            } else {
                editText.error = "Por favor, digite um valor."
            }
        }


        return view
    }


    override fun onStart() {
        super.onStart()
        // Define a largura do diálogo para ocupar quase toda a tela
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
