package com.example.prisma

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.util.Calendar

class AgendamentoActivity : AppCompatActivity() {

    private var diaDaSemanaSelecionado: Int = -1
    private val listaHorariosAgendados = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendamento)

        val iconSair = findViewById<ImageView>(R.id.iconSair)
        val tvNomeUsuario = findViewById<TextView?>(R.id.tvNomeUsuario)
        val tvDataSelecionada = findViewById<TextView?>(R.id.tvDataSelecionada)
        val containerHorarios = findViewById<LinearLayout>(R.id.containerHorarios)
        val btnNovoAgendamento = findViewById<Button>(R.id.btnNovoAgendamento)

        val nomeRecebido = intent.getStringExtra("NOME_USUARIO") ?: "Convidado"
        tvNomeUsuario?.text = "Olá, $nomeRecebido"

        atualizarAvisoVazio()

        iconSair?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        tvDataSelecionada?.setOnClickListener {
            val calendario = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this, { _, ano, mes, dia ->
                val calAux = Calendar.getInstance()
                calAux.set(ano, mes, dia)
                val dayOfWeek = calAux.get(Calendar.DAY_OF_WEEK)

                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(this, "Não atendemos aos finais de semana!", Toast.LENGTH_SHORT).show()
                    tvDataSelecionada.text = "Clique para selecionar"
                    diaDaSemanaSelecionado = -1
                } else {
                    val dataFinal = String.format("%02d/%02d/%d", dia, mes + 1, ano)
                    tvDataSelecionada.text = dataFinal
                    diaDaSemanaSelecionado = dayOfWeek
                    listaHorariosAgendados.clear()
                    containerHorarios?.removeAllViews()
                    atualizarAvisoVazio()
                }
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        btnNovoAgendamento?.setOnClickListener {
            if (diaDaSemanaSelecionado == -1) {
                Toast.makeText(this, "Selecione uma data válida primeiro!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calendario = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(this, { _, hora, minuto ->
                if (hora in 8..17) {
                    val horarioFormatado = String.format("%02d:%02d", hora, minuto)

                    if (listaHorariosAgendados.contains(horarioFormatado)) {
                        Toast.makeText(this, "Você já agendou esse horário!", Toast.LENGTH_SHORT).show()
                    } else {
                        listaHorariosAgendados.add(horarioFormatado)
                        if (containerHorarios != null) {
                            adicionarCardHorario(containerHorarios, horarioFormatado)
                            atualizarAvisoVazio()
                        }
                    }
                } else {
                    Toast.makeText(this, "Escolha entre 08h e 17h", Toast.LENGTH_SHORT).show()
                }
            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true)

            timePickerDialog.show()
        }
    }

    private fun adicionarCardHorario(container: LinearLayout, horario: String) {
        val card = MaterialCardView(this).apply {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(0, 0, 0, 24)
            layoutParams = params
            radius = 36f
            cardElevation = 2f
            setCardBackgroundColor(Color.WHITE)
            setContentPadding(40, 30, 40, 30)
        }

        val layoutHorizontal = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val layoutTextos = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val textoHorario = TextView(this).apply {
            text = horario
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.parseColor("#333333"))
            setTypeface(null, Typeface.BOLD)
        }

        val textoStatus = TextView(this).apply {
            text = "Agendado com sucesso"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#757575"))
        }

        layoutTextos.addView(textoHorario)
        layoutTextos.addView(textoStatus)

        val btnExcluir = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_delete)
            background = null
            setColorFilter(Color.RED)
            setPadding(20, 20, 20, 20)
            setOnClickListener {
                val builder = AlertDialog.Builder(this@AgendamentoActivity)
                builder.setTitle("Excluir Agendamento")
                builder.setMessage("Deseja remover o horário das $horario?")
                builder.setPositiveButton("Sim") { _, _ ->
                    container.removeView(card)
                    listaHorariosAgendados.remove(horario)
                    Toast.makeText(context, "Removido", Toast.LENGTH_SHORT).show()
                    atualizarAvisoVazio()
                }
                builder.setNegativeButton("Não", null)
                builder.show()
            }
        }

        layoutHorizontal.addView(layoutTextos)
        layoutHorizontal.addView(btnExcluir)
        card.addView(layoutHorizontal)
        container.addView(card, 0)
    }

    private fun atualizarAvisoVazio() {
        val tvListaVazia = findViewById<TextView>(R.id.tvListaVazia)
        val containerHorarios = findViewById<LinearLayout>(R.id.containerHorarios)

        if (containerHorarios?.childCount == 0) {
            tvListaVazia?.visibility = View.VISIBLE
        } else {
            tvListaVazia?.visibility = View.GONE
        }
    }
}