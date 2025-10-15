package com.example.calbon

import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PerguntasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perguntas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val radio1= findViewById<RadioButton>(R.id.radio1)
        val radio2= findViewById<RadioButton>(R.id.radio2)
        val radio3= findViewById<RadioButton>(R.id.radio3)
        val radio4= findViewById<RadioButton>(R.id.radio4)
        val radio5= findViewById<RadioButton>(R.id.radio5)

        radio1.setOnClickListener{
            radio2.isChecked = false
            radio3.isChecked = false
            radio4.isChecked = false
            radio5.isChecked = false
        }
        radio2.setOnClickListener{
            radio1.isChecked = false
            radio3.isChecked = false
            radio4.isChecked = false
            radio5.isChecked = false
        }
        radio3.setOnClickListener{
            radio1.isChecked = false
            radio2.isChecked = false
            radio4.isChecked = false
            radio5.isChecked = false
        }
        radio4.setOnClickListener{
            radio1.isChecked = false
            radio2.isChecked = false
            radio3.isChecked = false
            radio5.isChecked = false
        }
        radio5.setOnClickListener{
            radio1.isChecked = false
            radio2.isChecked = false
            radio3.isChecked = false
            radio4.isChecked = false
        }

        radio1.setOnLongClickListener{
            radio1.isChecked = false
            true
        }
        radio2.setOnLongClickListener{
            radio2.isChecked = false
            true
        }
        radio3.setOnLongClickListener{
            radio3.isChecked = false
            true
        }
        radio4.setOnLongClickListener{
            radio4.isChecked = false
            true
        }
        radio5.setOnLongClickListener{
            radio5.isChecked = false
            true
        }
    }
}