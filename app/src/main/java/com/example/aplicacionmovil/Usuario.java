package com.example.aplicacionmovil;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey
    @NonNull
    public String correo;
    public String nombre;
    public String contrasena;

    public Usuario(@NonNull String correo, String nombre, String contrasena) {
        this.correo = correo;
        this.nombre = nombre;
        this.contrasena = contrasena;
    }
}
