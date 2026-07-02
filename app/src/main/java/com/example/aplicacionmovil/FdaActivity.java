package com.example.aplicacionmovil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FdaActivity extends AppCompatActivity {

    private FdaViewModel viewModel;
    private FdaAdapter adapter;
    private LinearLayout layoutLoading, layoutError;
    private RecyclerView rvFda;
    private TextView txtError;
    private Button btnReintentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fda);

        // Vincular vistas
        layoutLoading  = findViewById(R.id.layoutLoading);
        layoutError    = findViewById(R.id.layoutError);
        rvFda          = findViewById(R.id.rvFda);
        txtError       = findViewById(R.id.txtError);
        btnReintentar  = findViewById(R.id.btnReintentar);

        // Configurar RecyclerView
        adapter = new FdaAdapter();
        rvFda.setLayoutManager(new LinearLayoutManager(this));
        rvFda.setAdapter(adapter);

        // Configurar ViewModel
        viewModel = new ViewModelProvider(this).get(FdaViewModel.class);

        // Observar estados
        observarEstados();

        // Cargar medicamentos
        viewModel.cargarMedicamentos();

        // Botón reintentar
        btnReintentar.setOnClickListener(v -> viewModel.cargarMedicamentos());
    }
    private void observarEstados() {
        new Thread(() -> {
            try {
                while (true) {
                    ApiState state = viewModel.getEstado().getValue();
                    runOnUiThread(() -> {
                        if (state instanceof ApiState.Loading) {
                            mostrarLoading();
                        } else if (state instanceof ApiState.Success) {
                            ApiState.Success success = (ApiState.Success) state;
                            mostrarExito(success.getMedicamentos());
                        } else if (state instanceof ApiState.Error) {
                            ApiState.Error error = (ApiState.Error) state;
                            mostrarError(error.getMensaje());
                        }
                    });
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        rvFda.setVisibility(View.GONE);
    }

    private void mostrarExito(java.util.List<FdaResponse.DrugResult> lista) {
        layoutLoading.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        rvFda.setVisibility(View.VISIBLE);
        adapter.actualizarLista(lista);
    }

    private void mostrarError(String mensaje) {
        layoutLoading.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        rvFda.setVisibility(View.GONE);
        txtError.setText(mensaje);
    }
}