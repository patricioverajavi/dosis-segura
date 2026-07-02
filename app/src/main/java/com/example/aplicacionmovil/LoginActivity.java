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
            // CORREGIDO: Usamos txtUsuario y txtContrasena que definiste arriba
            String usuario = txtUsuario.getText().toString();
            String pass = txtContrasena.getText().toString();

            // Solo validamos al hacer clic
            if (usuario.isEmpty() || pass.isEmpty()) {
                com.google.android.material.snackbar.Snackbar.make(
                        findViewById(android.R.id.content),
                        "Por favor completa todos los campos",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                ).show();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("tipo_ingreso", "usuario");
                intent.putExtra("nombre_usuario", usuario); // Ya no necesitas el if extra porque el isEmpty lo valida arriba

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
}