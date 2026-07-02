package com.example.aplicacionmovil;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicamentoRepository {
    private final MedicamentoDao dao;
    private final LiveData<List<Medicamento>> todosMedicamentos;
    private final LiveData<List<Medicamento>> favoritos;
    // Creamos un hilo secundario para las operaciones de escritura
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MedicamentoRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.medicamentoDao();
        todosMedicamentos = dao.getAll();
        favoritos = dao.getFavoritos();
    }

    public void insert(Medicamento medicamento) {
        // Ejecutamos en segundo plano
        executorService.execute(() -> dao.insert(medicamento));
    }

    public LiveData<List<Medicamento>> getTodos() {
        return todosMedicamentos;
    }

    public LiveData<List<Medicamento>> getFavoritos() {
        return favoritos;
    }

    public void update(Medicamento medicamento) {
        // Ejecutamos en segundo plano
        executorService.execute(() -> dao.update(medicamento));
    }

    public void delete(Medicamento medicamento) {
        // Ejecutamos en segundo plano
        executorService.execute(() -> dao.delete(medicamento));
    }
}