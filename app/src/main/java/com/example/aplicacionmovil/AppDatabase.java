package com.example.aplicacionmovil;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Medicamento.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instancia;

    public abstract MedicamentoDao medicamentoDao();
    public static AppDatabase getDatabase(final Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "catalogo_medicamentos_db")
                            .fallbackToDestructiveMigration() // ESTO ES LA CLAVE
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instancia;
    }
}