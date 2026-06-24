package com.vera.loginactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var etConfirmarContrasena: TextInputEditText
    private lateinit var btnRegistrar: MaterialButton
    private lateinit var btnVolver: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnVolver = findViewById(R.id.btnVolver)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val confirmar = etConfirmarContrasena.text.toString().trim()

            if (validarCampos(nombre, correo, contrasena, confirmar)) {
                crearCuenta(correo, contrasena)
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun validarCampos(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmar: String
    ): Boolean {
        if (nombre.isEmpty()) {
            Toast.makeText(this,
                "Ingresa tu nombre",
                Toast.LENGTH_SHORT).show()
            etNombre.requestFocus()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this,
                "Ingresa un correo válido",
                Toast.LENGTH_SHORT).show()
            etCorreo.requestFocus()
            return false
        }
        if (contrasena.length < 6) {
            Toast.makeText(this,
                "La contraseña debe tener al menos 6 caracteres",
                Toast.LENGTH_SHORT).show()
            etContrasena.requestFocus()
            return false
        }
        if (contrasena != confirmar) {
            Toast.makeText(this,
                "Las contraseñas no coinciden",
                Toast.LENGTH_SHORT).show()
            etConfirmarContrasena.requestFocus()
            return false
        }
        return true
    }

    private fun crearCuenta(correo: String, contrasena: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,
                        "Cuenta creada exitosamente",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val mensaje = when {
                        task.exception?.message?.contains(
                            "email address is already in use") == true ->
                            "Este correo ya está registrado"
                        task.exception?.message?.contains(
                            "badly formatted") == true ->
                            "El formato del correo no es válido"
                        task.exception?.message?.contains(
                            "password is invalid") == true ->
                            "La contraseña debe tener al menos 6 caracteres"
                        else -> "Error al crear cuenta. Intenta de nuevo"
                    }
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }
            }
    }
}