package com.example.aplicacionmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity {

    // Es buena práctica dejar los componentes como privados
    private EditText txtRegNombre, txtRegCorreo, txtRegContrasena;
    private Button btnRegistrarCuenta;
    private TextView txtVolverLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // 1. Inicializar Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Vincular elementos de la interfaz
        txtRegNombre = findViewById(R.id.txtRegNombre);
        txtRegCorreo = findViewById(R.id.txtRegCorreo);
        txtRegContrasena = findViewById(R.id.txtRegContrasena);
        btnRegistrarCuenta = findViewById(R.id.btnRegistrarCuenta);
        txtVolverLogin = findViewById(R.id.txtVolverLogin);

        // Acción del botón registrar
        btnRegistrarCuenta.setOnClickListener(v -> {
            String nombre = txtRegNombre.getText().toString().trim();
            String correo = txtRegCorreo.getText().toString().trim();
            String contrasena = txtRegContrasena.getText().toString().trim();

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(RegistroActivity.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // 2. Llamada a Firebase para crear el usuario
                mAuth.createUserWithEmailAndPassword(correo, contrasena)
                        .addOnCompleteListener(RegistroActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Éxito: El usuario se guardó en Firebase
                                Toast.makeText(RegistroActivity.this, "¡Bienvenido, " + nombre + "! Cuenta creada en Firebase.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Error: Por ejemplo, correo inválido o contraseña corta
                                Toast.makeText(RegistroActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // Acción para regresar al Login
        txtVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}