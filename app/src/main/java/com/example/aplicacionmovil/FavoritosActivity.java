package com.example.aplicacionmovil;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.LiveData;

public class FavoritosActivity extends AppCompatActivity {

    private RecyclerView rvFavoritos;
    private MedicamentoAdapter adapter;
    private MedicamentoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        // 1. Configurar botón de regreso
        ImageView btnRegresar = findViewById(R.id.btnRegresar);
        if (btnRegresar != null) {
            btnRegresar.setOnClickListener(v -> finish());
        }

        // 2. Configurar RecyclerView
        rvFavoritos = findViewById(R.id.rvFavoritos);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(this));

        // 3. Inicializar ViewModel (el puente con Kotlin)
        viewModel = new ViewModelProvider(this).get(MedicamentoViewModel.class);

        // 4. Inicializar adaptador con lista vacía
        boolean esInvitado = "invitado".equals(getIntent().getStringExtra("tipo_ingreso"));
        adapter = new MedicamentoAdapter(viewModel, new ArrayList<Medicamento>(), "favoritos", esInvitado);
        rvFavoritos.setAdapter(adapter);

        // 5. OBSERVAR los cambios en la Base de Datos en tiempo real
        viewModel.getFavoritos().observe(this, medicamentosFavoritos -> {
            // 1. Verificamos que la lista no sea nula
            if (medicamentosFavoritos != null) {

                // 2. Usamos .size() en lugar de .isEmpty() porque size()
                // funciona en cualquier tipo de List en Java sin errores de resolución
                if (medicamentosFavoritos.size() > 0) {
                    adapter.actualizarLista(medicamentosFavoritos);
                } else {
                    adapter.actualizarLista(new ArrayList<>());
                    Toast.makeText(this, "No tienes medicamentos favoritos aún", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}