package com.example.aplicacionmovil;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MedicamentoDao {

    // Método síncrono para usar dentro del Worker
    @Query("SELECT * FROM medicamentos WHERE isFavorito = 1")
    List<Medicamento> getFavoritosDirecto();

    // Insertar un nuevo medicamento
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Medicamento medicamento);

    // Obtener todos los medicamentos
    @Query("SELECT * FROM medicamentos")
    LiveData<List<Medicamento>> getAll();

    // Obtener solo favoritos
    @Query("SELECT * FROM medicamentos WHERE isFavorito = 1")
    LiveData<List<Medicamento>> getFavoritos();

    // Actualizar un medicamento (útil para marcar/desmarcar favorito)
    @Update
    void update(Medicamento medicamento);

    // Borrar un medicamento
    @Delete
    void delete(Medicamento medicamento);
}