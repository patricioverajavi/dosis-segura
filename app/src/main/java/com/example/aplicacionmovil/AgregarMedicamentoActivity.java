package com.example.aplicacionmovil;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class AgregarMedicamentoActivity extends AppCompatActivity {

    private MedicamentoViewModel viewModel;
    private EditText etNombre, etDescripcion, etDosis, etPresentacion,
            etPrincipioActivo, etFabricante, etIndicaciones,
            etContraindicaciones, etAdvertencias, etCategoria;
    private Button btnGuardar;
    private TextView tvTitulo;

    private Medicamento medicamentoEditar = null; // null = modo crear

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_medicamento);

        viewModel = new ViewModelProvider(this).get(MedicamentoViewModel.class);

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etDosis = findViewById(R.id.etDosis);
        etPresentacion = findViewById(R.id.etPresentacion);
        etPrincipioActivo = findViewById(R.id.etPrincipioActivo);
        etFabricante = findViewById(R.id.etFabricante);
        etIndicaciones = findViewById(R.id.etIndicaciones);
        etContraindicaciones = findViewById(R.id.etContraindicaciones);
        etAdvertencias = findViewById(R.id.etAdvertencias);
        etCategoria = findViewById(R.id.etCategoria);
        btnGuardar = findViewById(R.id.btnGuardar);
        tvTitulo = findViewById(R.id.tvTitulo);

        // ¿Recibimos un ID? → modo editar
        int id = getIntent().getIntExtra("medicamento_id", -1);
        if (id != -1) {
            // Busca el medicamento en la BD y precarga los campos
            viewModel.getTodos().observe(this, lista -> {
                for (Medicamento m : lista) {
                    if (m.id == id) {
                        medicamentoEditar = m;
                        precargarCampos(m);
                        break;
                    }
                }
            });
            tvTitulo.setText("Editar Medicamento");
            btnGuardar.setText("Actualizar");
        } else {
            tvTitulo.setText("Agregar Medicamento");
            btnGuardar.setText("Guardar");
        }

        btnGuardar.setOnClickListener(v -> guardarOActualizar());
    }

    private void precargarCampos(Medicamento m) {
        etNombre.setText(m.nombre);
        etDescripcion.setText(m.descripcion);
        etDosis.setText(m.dosis);
        etPresentacion.setText(m.presentacion);
        etPrincipioActivo.setText(m.principioActivo);
        etFabricante.setText(m.fabricante);
        etIndicaciones.setText(m.indicaciones);
        etContraindicaciones.setText(m.contraindicaciones);
        etAdvertencias.setText(m.advertencias);
        etCategoria.setText(m.categoria);
    }

    private void guardarOActualizar() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String dosis = etDosis.getText().toString().trim();
        String presentacion = etPresentacion.getText().toString().trim();
        String principioActivo = etPrincipioActivo.getText().toString().trim();
        String fabricante = etFabricante.getText().toString().trim();
        String indicaciones = etIndicaciones.getText().toString().trim();
        String contraindicaciones = etContraindicaciones.getText().toString().trim();
        String advertencias = etAdvertencias.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            return;
        }

        if (categoria.isEmpty()) categoria = "Otros";

        if (medicamentoEditar != null) {
            // MODO EDITAR
            medicamentoEditar.nombre = nombre;
            medicamentoEditar.descripcion = descripcion;
            medicamentoEditar.dosis = dosis;
            medicamentoEditar.presentacion = presentacion;
            medicamentoEditar.principioActivo = principioActivo;
            medicamentoEditar.fabricante = fabricante;
            medicamentoEditar.indicaciones = indicaciones;
            medicamentoEditar.contraindicaciones = contraindicaciones;
            medicamentoEditar.advertencias = advertencias;
            medicamentoEditar.categoria = categoria;

            viewModel.update(medicamentoEditar);
            Toast.makeText(this, "✓ Medicamento actualizado", Toast.LENGTH_SHORT).show();

        } else {
            // MODO CREAR
            Medicamento nuevo = new Medicamento(
                    nombre, descripcion, presentacion, dosis,
                    contraindicaciones, advertencias,
                    principioActivo, fabricante, indicaciones,
                    categoria, false
            );
            viewModel.insert(nuevo);

            // Notificación al agregar medicamento nuevo
            NotificacionHelper.notificarDirecto(
                    this,
                    "💊 Medicamento agregado",
                    nombre + " fue agregado al catálogo."
            );  }
        finish();
    }
}