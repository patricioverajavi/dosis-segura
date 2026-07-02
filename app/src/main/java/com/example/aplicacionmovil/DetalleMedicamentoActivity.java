package com.example.aplicacionmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleMedicamentoActivity extends AppCompatActivity {

    private ImageView btnAtras;
    private ImageButton btnAñadirFavoritoDetail;
    private TextView txtDetalleNombre, txtDetalleDescripcion, txtDetalleDosis,
            txtDetalleContraindicaciones, txtDetalleAdvertencias,
            txtDetallePrincipioActivo, txtDetallePresentacion,
            txtDetalleFabricante, txtDetalleIndicaciones;

    private Medicamento medicamentoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_medicamento);

        inicializarVistas();
        medicamentoSeleccionado = (Medicamento) getIntent().getSerializableExtra("medicamento");

        if (medicamentoSeleccionado != null) {
            cargarDatosEnVistas();
            sincronizarIconoFavorito();
        }

        // Configurar botón Atrás
        if (btnAtras != null) {
            btnAtras.setOnClickListener(v -> cerrarConResultado(false));
        }

        // Lógica del botón Favoritos CORREGIDA
        if (btnAñadirFavoritoDetail != null) {
            btnAñadirFavoritoDetail.setOnClickListener(v -> {
                if (medicamentoSeleccionado != null) {
                    medicamentoSeleccionado.isFavorito = !medicamentoSeleccionado.isFavorito;
                    sincronizarIconoFavorito();

                    String mensaje = medicamentoSeleccionado.isFavorito ?
                            "Añadido a favoritos" : "Eliminado de favoritos";

                    // 1. Usamos Snackbar en lugar de Toast para evitar que se quede pegado
                    com.google.android.material.snackbar.Snackbar.make(
                            findViewById(android.R.id.content),
                            mensaje,
                            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show();

                    // 2. Si es favorito, cerramos con un pequeño retraso para que el usuario vea el mensaje
                    if (medicamentoSeleccionado.isFavorito) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            cerrarConResultado(true);
                        }, 600); // 600 milisegundos de espera antes de cerrar
                    }
                }
            });
        }
    }

    private void cerrarConResultado(boolean irAFavoritos) {
        Intent data = new Intent();
        data.putExtra("irAFavoritos", irAFavoritos);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Al usar el botón físico de atrás, cerramos sin forzar el modo favoritos
        cerrarConResultado(false);
        super.onBackPressed();
    }

    private void inicializarVistas() {
        btnAtras = findViewById(R.id.btnAtras);
        btnAñadirFavoritoDetail = findViewById(R.id.btnAñadirFavoritoDetail);
        txtDetalleNombre = findViewById(R.id.txtDetalleNombre);
        txtDetalleDescripcion = findViewById(R.id.txtDetalleDescripcion);
        txtDetalleDosis = findViewById(R.id.txtDetalleDosis);
        txtDetalleContraindicaciones = findViewById(R.id.txtDetalleContraindicaciones);
        txtDetalleAdvertencias = findViewById(R.id.txtDetalleAdvertencias);
        txtDetallePrincipioActivo = findViewById(R.id.txtDetallePrincipioActivo);
        txtDetallePresentacion = findViewById(R.id.txtDetallePresentacion);
        txtDetalleFabricante = findViewById(R.id.txtDetalleFabricante);
        txtDetalleIndicaciones = findViewById(R.id.txtDetalleIndicaciones);
    }

    private void cargarDatosEnVistas() {
        txtDetalleNombre.setText(medicamentoSeleccionado.nombre);
        txtDetalleDescripcion.setText(medicamentoSeleccionado.descripcion);
        txtDetalleDosis.setText(medicamentoSeleccionado.dosis);
        txtDetalleContraindicaciones.setText(medicamentoSeleccionado.contraindicaciones);
        txtDetalleAdvertencias.setText(medicamentoSeleccionado.advertencias);
        txtDetallePrincipioActivo.setText(medicamentoSeleccionado.principioActivo);
        txtDetallePresentacion.setText(medicamentoSeleccionado.presentacion);
        txtDetalleFabricante.setText(medicamentoSeleccionado.fabricante);
        txtDetalleIndicaciones.setText(medicamentoSeleccionado.indicaciones);
    }

    private void sincronizarIconoFavorito() {
        btnAñadirFavoritoDetail.setImageResource(medicamentoSeleccionado.isFavorito ?
                android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
    }
}