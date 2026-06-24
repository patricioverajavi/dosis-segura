package com.vera.loginactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnCrearCuenta: MaterialButton
    private lateinit var btnInvitado: MaterialButton
    private lateinit var progressBar: android.widget.ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Verificar sesión activa
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        if (usuarioActual != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        btnLogin = findViewById(R.id.btnLogin)
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)
        btnInvitado = findViewById(R.id.btnInvitado)
        progressBar = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            if (validarCampos(correo, contrasena)) {
                iniciarSesion(correo, contrasena)
            }
        }

        btnCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnInvitado.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("esInvitado", true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this,
                            "Error al ingresar como invitado",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validarCampos(correo: String, contrasena: String): Boolean {
        if (correo.isEmpty()) {
            Toast.makeText(this,
                "Ingresa tu correo electrónico",
                Toast.LENGTH_SHORT).show()
            etCorreo.requestFocus()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this,
                "Ingresa un correo válido",
                Toast.LENGTH_SHORT).show()
            etCorreo.requestFocus()
            return false
        }
        if (contrasena.isEmpty()) {
            Toast.makeText(this,
                "Ingresa tu contraseña",
                Toast.LENGTH_SHORT).show()
            etContrasena.requestFocus()
            return false
        }
        if (contrasena.length < 6) {
            Toast.makeText(this,
                "La contraseña debe tener al menos 6 caracteres",
                Toast.LENGTH_SHORT).show()
            etContrasena.requestFocus()
            return false
        }
        return true
    }

    private fun iniciarSesion(correo: String, contrasena: String) {
        progressBar.visibility = android.view.View.VISIBLE
        btnLogin.isEnabled = false

        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                progressBar.visibility = android.view.View.GONE
                btnLogin.isEnabled = true

                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("esInvitado", false)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val mensaje = when {
                        task.exception?.message?.contains(
                            "password is invalid") == true ->
                            "Contraseña incorrecta"
                        task.exception?.message?.contains(
                            "no user record") == true ->
                            "No existe una cuenta con este correo"
                        task.exception?.message?.contains(
                            "blocked") == true ->
                            "Demasiados intentos. Intenta más tarde"
                        else -> "Correo o contraseña incorrectos"
                    }
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }
            }
    }
}