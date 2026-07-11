package com.example.aplicacionmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText txtUsuario, txtContrasena;
    Button btnIngresar, btnInvitado;
    TextView txtCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Vincular vistas
        txtUsuario = findViewById(R.id.txtUsuario);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnInvitado = findViewById(R.id.btnInvitado);
        txtCrearCuenta = findViewById(R.id.txtCrearCuenta);

        // Evento para ingresar con credenciales
        btnIngresar.setOnClickListener(v -> {
            String usuario = txtUsuario.getText().toString().trim();
            String pass = txtContrasena.getText().toString().trim();

            if (usuario.isEmpty()) {
                txtUsuario.setError("El correo es obligatorio");
            } else if (pass.isEmpty()) {
                txtContrasena.setError("La contraseña es obligatoria");
            } else if (!isValidEmail(usuario)) {
                mostrarError("Correo electrónico inválido (debe contener @)");
            } else if (!isValidPassword(pass)) {
                mostrarError("La contraseña debe tener al menos 6 caracteres");
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("tipo_ingreso", "usuario");
                intent.putExtra("nombre_usuario", usuario);
                startActivity(intent);
                finish();
            }
        });

        // Evento para ingresar como invitado (CORREGIDO: Ahora envía el extra "invitado")
        btnInvitado.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tipo_ingreso", "invitado");
            startActivity(intent);
            finish();
        });

        // Evento para abrir la pantalla de Registro
        txtCrearCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    // Prueba 1: Validación de correo
    public boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Prueba 2: Validación de contraseña (mínimo 6 caracteres)
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    private void mostrarError(String mensaje) {
        com.google.android.material.snackbar.Snackbar.make(
                findViewById(android.R.id.content),
                mensaje,
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
        ).show();
    }
}