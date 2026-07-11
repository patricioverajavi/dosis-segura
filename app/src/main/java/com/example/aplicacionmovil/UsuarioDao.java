package com.example.aplicacionmovil;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void registrar(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasena = :pass LIMIT 1")
    Usuario login(String correo, String pass);

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo")
    int verificarSiExiste(String correo);
}
