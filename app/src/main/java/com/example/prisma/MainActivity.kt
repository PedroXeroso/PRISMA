package com.example.prisma

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etNomeUsuario)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val tvErroEmail = findViewById<TextView>(R.id.tvErroEmail)
        val tvErroSenha = findViewById<TextView>(R.id.tvErroSenha)
        val botaoEntrar = findViewById<Button>(R.id.botao)

        botaoEntrar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            tvErroEmail.visibility = View.GONE
            tvErroSenha.visibility = View.GONE

            var validado = true

            if (email.isEmpty()) {
                tvErroEmail.text = "O campo não pode estar vazio"
                tvErroEmail.visibility = View.VISIBLE
                validado = false
            } else if (!email.endsWith("@mesquita.com.br")) {
                tvErroEmail.text = "Use o e-mail @mesquita.com.br"
                tvErroEmail.visibility = View.VISIBLE
                validado = false
            }

            if (senha.isEmpty()) {
                tvErroSenha.text = "A senha não pode estar vazia"
                tvErroSenha.visibility = View.VISIBLE
                validado = false
            } else if (senha.length < 6) {
                tvErroSenha.text = "A senha deve ter no mínimo 6 caracteres"
                tvErroSenha.visibility = View.VISIBLE
                validado = false
            }

            if (validado) {
                val nomeParaMostrar = email.split("@")[0].replace(".", " ").replaceFirstChar { it.uppercase() }
                val intent = Intent(this, LoadingActivity::class.java)
                intent.putExtra("NOME_USUARIO", nomeParaMostrar)
                startActivity(intent)
            }
        }
    }
}