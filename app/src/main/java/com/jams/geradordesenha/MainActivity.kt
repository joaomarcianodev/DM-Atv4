package com.jams.geradordesenha

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var swLetras: MaterialSwitch
    private lateinit var swVariando: MaterialSwitch
    private lateinit var swNumeros: MaterialSwitch
    private lateinit var swEspeciais: MaterialSwitch
    private lateinit var allSwitch: List<MaterialSwitch>
    private lateinit var slider: Slider
    private lateinit var btnGerar: Button
    private lateinit var senhaGeradaTextView: TextView
    private lateinit var linkCopiar: TextView
    private var toast: Toast? = null

    // Conjuntos de caracteres para a geração da senha
    private val letrasMinusculas = "abcdefghijklmnopqrstuvwxyz"
    private val letrasMaiusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val numeros = "0123456789"
    private val especiais = "!@#$%^&*()_+-=[]{}|;:,.<>?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //para o app não ocupar o fullscreen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Inicialização dos Componentes ---
        slider = findViewById(R.id.slider_tamanho)
        val valorTextView = findViewById<TextView>(R.id.valor_tamanho)
        swLetras = findViewById(R.id.opt_minusculas)
        swVariando = findViewById(R.id.opt_maiusculas)
        swNumeros = findViewById(R.id.opt_numeros)
        swEspeciais = findViewById(R.id.opt_especiais)
        allSwitch = listOf(swLetras, swVariando, swNumeros, swEspeciais)
        btnGerar = findViewById(R.id.btn_gerar)
        senhaGeradaTextView = findViewById(R.id.senhagerada)
        linkCopiar = findViewById(R.id.link_copiar)

        // --- Configuração dos Listeners ---

        // Listener para alterar o tamanho da senha no TextView
        valorTextView.text = slider.value.toInt().toString()
        slider.addOnChangeListener { _, value, _ ->
            valorTextView.text = value.toInt().toString()
        }

        // Listener para a lógica dos switches
        val listener = CompoundButton.OnCheckedChangeListener { toggledSwitch, isChecked ->
            ConferirSwitch(toggledSwitch, isChecked)
        }
        allSwitch.forEach { it.setOnCheckedChangeListener(listener) }

        // Listener para o botão de gerar senha
        btnGerar.setOnClickListener {
            gerarSenha()
        }

        // Listener para o link de copiar
        linkCopiar.setOnClickListener {
            copiarSenha()
        }
    }

    private fun ConferirSwitch(toggledSwitch: CompoundButton, isChecked: Boolean) {
        if (!isChecked) {
            if (!allSwitch.any { it.isChecked }) {
                toggledSwitch.isChecked = true
                toast?.cancel()
                toast = Toast.makeText(
                    this,
                    "Pelo menos uma opção deve estar ativa.",
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }
    }

    /**
     * Gera uma senha com base nas opções selecionadas e atualiza o TextView.
     */
    private fun gerarSenha() {
        val tamanhoSenha = slider.value.toInt()
        val charPool = StringBuilder()

        // 1. Monta o conjunto de caracteres permitidos
        if (swLetras.isChecked) {
            charPool.append(letrasMinusculas)
        }
        if (swVariando.isChecked) {
            charPool.append(letrasMaiusculas)
        }
        if (swNumeros.isChecked) {
            charPool.append(numeros)
        }
        if (swEspeciais.isChecked) {
            charPool.append(especiais)
        }

        // 2. Gera a senha aleatória
        val senha = StringBuilder(tamanhoSenha)
        for (i in 0 until tamanhoSenha) {
            val randomIndex = Random.nextInt(charPool.length)
            senha.append(charPool[randomIndex])
        }

        // 3. Exibe a senha no TextView
        senhaGeradaTextView.text = senha.toString()
        senhaGeradaTextView.setTextColor(getColor(R.color.escura))
        linkCopiar.setTextColor(getColor(R.color.escura))
    }

    /**
     * Copia o texto da senha gerada para a área de transferência do dispositivo.
     */
    private fun copiarSenha() {
        val senha = senhaGeradaTextView.text.toString()

        // Verifica se o texto não é o inicial antes de copiar
        if (senha.isNotEmpty() && senha != getString(R.string.aguardando)) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("senha_copiada", senha)
            clipboard.setPrimaryClip(clip)


            // Exibe uma confirmação para o usuário
            toast?.cancel()
            toast = Toast.makeText(this, "Senha copiada!", Toast.LENGTH_SHORT)
            toast?.show()
        } else {
            // Opcional: Avisa o usuário que não há senha para copiar
            toast?.cancel()
            toast = Toast.makeText(this, "Gere uma senha primeiro.", Toast.LENGTH_SHORT)
            toast?.show()
        }
    }
}