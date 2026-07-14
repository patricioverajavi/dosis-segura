package com.example.aplicacionmovil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // 1. Vincular vistas
        ImageView btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> finish());
        TextView txtPerfilNombre = findViewById(R.id.txtPerfilNombre);
        TextView txtPerfilCorreo = findViewById(R.id.txtPerfilCorreo);
        TextView txtPerfilAcceso = findViewById(R.id.txtPerfilAcceso);
        LinearLayout cardMisFavoritos = findViewById(R.id.cardMisFavoritos);
        LinearLayout cardEstadisticas = findViewById(R.id.cardEstadisticas);

        Button btnAccionSesion = findViewById(R.id.btnAccionSesion);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnRegresar.setOnClickListener(v -> finish());

        // 2. Obtener estado
        String tipoIngreso = getIntent().getStringExtra("tipo_ingreso");
        boolean esInvitado = "invitado".equals(tipoIngreso);

        // 3. Lógica de visualización
        if (esInvitado) {
            txtPerfilNombre.setText("Invitado");
            txtPerfilCorreo.setText("No disponible");
            txtPerfilAcceso.setText("Acceso Limitado");
            cardMisFavoritos.setVisibility(View.GONE);
            cardEstadisticas.setVisibility(View.GONE);

            btnAccionSesion.setText("Iniciar Sesión");
            btnCerrarSesion.setVisibility(View.VISIBLE);
            btnCerrarSesion.setText("Cerrar Sesión");
        } else {
            txtPerfilNombre.setText("Patricio Vera");
            txtPerfilCorreo.setText("patricioverajavier@gmail.com");
            txtPerfilAcceso.setText("Usuario Autenticado");
            cardMisFavoritos.setVisibility(View.VISIBLE);
            cardEstadisticas.setVisibility(View.VISIBLE);

            btnAccionSesion.setText("Cerrar Sesión");
            btnCerrarSesion.setVisibility(View.GONE);
        }

        // 4. Acción común de salida
        View.OnClickListener accionSalir = v -> {
            // 1. REINICIAMOS LA VARIABLE GLOBAL para que al volver a entrar pida la cédula
            MainActivity.mensajeAccesoMostrado = false;
            // 2. CORRECCIÓN: El mensaje debe ir como segundo argumento
            com.google.android.material.snackbar.Snackbar.make(
                    findViewById(android.R.id.content),
                    "Cerrando sesión...",
                    com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
            ).show();

            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }, 600);
        };

        // Asignación a los botones de sesión
        btnAccionSesion.setOnClickListener(accionSalir);
        btnCerrarSesion.setOnClickListener(accionSalir);

        // 5. IMPLEMENTACIÓN DE FAVORITOS
        cardMisFavoritos.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoritosActivity.class);
            intent.putExtra("tipo_ingreso", getIntent().getStringExtra("tipo_ingreso"));
            startActivity(intent);
        });

        // 6. IMPLEMENTACIÓN DE ESTADÍSTICAS (Opcional, si tienes la clase)
        cardEstadisticas.setOnClickListener(v -> {
            // Intent hacia tu actividad de estadísticas
        });
    }
}
