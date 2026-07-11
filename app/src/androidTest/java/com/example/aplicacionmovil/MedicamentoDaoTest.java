package com.example.aplicacionmovil;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MedicamentoDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private MedicamentoDao dao;

    // ══════════════════════
    // BEFORE — se ejecuta antes de cada prueba
    // ══════════════════════
    @Before
    public void crearBaseDeDatos() {
        Context context = ApplicationProvider.getApplicationContext();

        // Base de datos EN MEMORIA — no persiste, solo para pruebas
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        dao = database.medicamentoDao();
    }

    // ══════════════════════
    // AFTER — limpia después de cada prueba
    // ══════════════════════
    @After
    public void cerrarBaseDeDatos() {
        database.close();
    }

    // ══════════════════════
    // PRUEBA 1 — CREATE
    // ══════════════════════
    @Test
    public void insertar_medicamento_y_verificar_que_existe() {
        // ARRANGE — preparar el medicamento
        Medicamento medicamento = new Medicamento(
                "Paracetamol 500mg",
                "Alivio del dolor",
                "Caja con 20 tabletas",
                "500mg cada 8 horas",
                "Hipersensibilidad",
                "No exceder dosis",
                "Paracetamol",
                "Laboratorios Pharma",
                "Dolor de cabeza",
                "Analgésicos",
                false // isFavorito = false
        );

        // ACT — insertar en Room
        dao.insert(medicamento);

        // ASSERT — verificar que se guardó
        // CORRECCIÓN: Usar getAllDirecto() porque el medicamento NO es favorito
        List<Medicamento> lista = dao.getAllDirecto();
        assertNotNull(lista);
        assertEquals("Debe haber 1 medicamento en la lista", 1, lista.size());
        assertEquals("Paracetamol 500mg", lista.get(0).nombre);
    }

    // ══════════════════════
    // PRUEBA 2 — READ favoritos
    // ══════════════════════
    @Test
    public void insertar_favorito_y_recuperarlo() {
        // ARRANGE
        Medicamento medicamento = new Medicamento(
                "Ibuprofeno 400mg",
                "Antiinflamatorio",
                "Caja con 24 tabletas",
                "400mg cada 8 horas",
                "Úlcera gástrica",
                "Tomar con alimentos",
                "Ibuprofeno",
                "Genfar",
                "Inflamación",
                "Analgésicos",
                true  // ← isFavorito = true
        );

        // ACT
        dao.insert(medicamento);

        // ASSERT — debe aparecer en favoritos
        List<Medicamento> favoritos = dao.getFavoritosDirecto();
        assertEquals(1, favoritos.size());
        assertEquals("Ibuprofeno 400mg", favoritos.get(0).nombre);
        assertTrue(favoritos.get(0).isFavorito);
    }

    // ══════════════════════
    // PRUEBA 3 — DELETE
    // ══════════════════════
    @Test
    public void insertar_y_eliminar_medicamento() {
        // ARRANGE
        Medicamento medicamento = new Medicamento(
                "Aspirina 500mg",
                "Protector cardíaco",
                "Caja con 30 tabletas",
                "500mg cada 8 horas",
                "Úlcera péptica",
                "No dar a niños",
                "Ácido Acetilsalicílico",
                "Bayer",
                "Migraña",
                "Analgésicos",
                true
        );

        // ACT — insertar y luego eliminar
        dao.insert(medicamento);
        List<Medicamento> antesDeEliminar = dao.getFavoritosDirecto();
        assertEquals(1, antesDeEliminar.size());

        dao.delete(antesDeEliminar.get(0));

        // ASSERT — lista debe estar vacía
        List<Medicamento> despuesDeEliminar = dao.getFavoritosDirecto();
        assertEquals(0, despuesDeEliminar.size());
    }

    // ══════════════════════
    // PRUEBA 4 — UPDATE
    // ══════════════════════
    @Test
    public void insertar_y_actualizar_medicamento() {
        // ARRANGE
        Medicamento medicamento = new Medicamento(
                "Naproxeno 500mg",
                "Antiinflamatorio",
                "Caja con 15 tabletas",
                "500mg cada 12 horas",
                "Insuficiencia renal",
                "Puede causar molestias",
                "Naproxeno",
                "Chalver",
                "Artritis",
                "Analgésicos",
                false // Inicialmente no es favorito
        );

        // ACT — insertar
        dao.insert(medicamento);

        // CORRECCIÓN: Recuperar usando getAllDirecto() ya que inicialmente no es favorito
        List<Medicamento> lista = dao.getAllDirecto();
        assertFalse("La lista no debe estar vacía", lista.isEmpty());

        // Marcar como favorito
        Medicamento actualizado = lista.get(0);
        actualizado.isFavorito = true;
        dao.update(actualizado);

        // ASSERT — debe aparecer en favoritos
        List<Medicamento> favoritos = dao.getFavoritosDirecto();
        assertEquals(1, favoritos.size());
        assertTrue(favoritos.get(0).isFavorito);
        assertEquals("Naproxeno 500mg", favoritos.get(0).nombre);
    }
}
