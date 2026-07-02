package com.example.aplicacionmovil;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity(tableName = "medicamentos")
public class Medicamento implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nombre") public String nombre;
    @ColumnInfo(name = "descripcion") public String descripcion;
    @ColumnInfo(name = "presentacion") public String presentacion;
    @ColumnInfo(name = "dosis") public String dosis;
    @ColumnInfo(name = "contraindicaciones") public String contraindicaciones;
    @ColumnInfo(name = "advertencias") public String advertencias;
    @ColumnInfo(name = "principioActivo") public String principioActivo;
    @ColumnInfo(name = "fabricante") public String fabricante;
    @ColumnInfo(name = "indicaciones") public String indicaciones;
    @ColumnInfo(name = "categoria") public String categoria;
    @ColumnInfo(name = "isFavorito") public boolean isFavorito;

    // CONSTRUCTOR PRINCIPAL: Obligatorio para Room
    // Debe incluir todos los campos definidos en la clase
    public Medicamento(String nombre, String descripcion, String presentacion, String dosis,
                       String contraindicaciones, String advertencias, String principioActivo,
                       String fabricante, String indicaciones, String categoria, boolean isFavorito) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.presentacion = presentacion;
        this.dosis = dosis;
        this.contraindicaciones = contraindicaciones;
        this.advertencias = advertencias;
        this.principioActivo = principioActivo;
        this.fabricante = fabricante;
        this.indicaciones = indicaciones;
        this.categoria = categoria;
        this.isFavorito = isFavorito;
    }

    // Constructor de compatibilidad (usado solo si no quieres cambiar cada línea de MainActivity)
    // Room lo ignora gracias a la anotación @Ignore
    @Ignore
    public Medicamento(String nombre, String descripcion, String presentacion, String dosis,
                       String contraindicaciones, String advertencias, String principioActivo,
                       String fabricante, String indicaciones) {
        this(nombre, descripcion, presentacion, dosis, contraindicaciones, advertencias,
                principioActivo, fabricante, indicaciones, "General", false);
    }
}